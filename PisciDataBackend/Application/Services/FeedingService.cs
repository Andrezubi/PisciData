using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class FeedingService
    {
        private readonly FeedingRepository _feedingRepository;

        public FeedingService(FeedingRepository feedingRepository)
        {
            _feedingRepository = feedingRepository;
        }

        public async Task<IEnumerable<FeedingDetailDto>> GetAllAsync()
        {
            var feedings = await _feedingRepository.GetAllAsync();
            return feedings.Select(MapToDetailDto);
        }

        public async Task<IEnumerable<FeedingDetailDto>> GetByProductionCycleIdAsync(int cycleId)
        {
            var feedings = await _feedingRepository.GetByProductionCycleIdAsync(cycleId);
            return feedings.Select(MapToDetailDto);
        }

        public async Task<FeedingDetailDto?> GetByIdAsync(int id)
        {
            var feeding = await _feedingRepository.GetByIdWithFeedAsync(id);
            return feeding == null ? null : MapToDetailDto(feeding);
        }

        public async Task<(FeedingDetailDto? Dto, List<string> Errors)> CreateAsync(CreateFeedingDto dto, int? userId = null)
        {
            var errors = FeedingValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var mealNumber = dto.MealNumber;
            if (!mealNumber.HasValue || mealNumber.Value <= 0)
            {
                var countToday = await _feedingRepository.GetMealCountForDateAsync(dto.ProductionCycleId, dto.FeedingDate);
                mealNumber = countToday + 1;
            }

            var feeding = new Feeding
            {
                ProductionCycleId = dto.ProductionCycleId,
                FeedId = dto.FeedId,
                FeedingDate = dto.FeedingDate,
                FeedingTime = dto.FeedingTime,
                QuantityKg = dto.QuantityKg,
                MealNumber = mealNumber,
                Behavior = string.IsNullOrWhiteSpace(dto.Behavior) ? null : dto.Behavior.Trim(),
                Observations = string.IsNullOrWhiteSpace(dto.Observations) ? null : dto.Observations.Trim(),
                UserId = userId,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _feedingRepository.AddAsync(feeding);
            var created = await _feedingRepository.GetByIdWithFeedAsync(feeding.Id);
            return (MapToDetailDto(created ?? feeding), errors);
        }

        public async Task<(FeedingDetailDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateFeedingDto dto, int? userId = null)
        {
            var errors = FeedingValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var feeding = await _feedingRepository.GetByIdWithFeedAsync(id);
            if (feeding == null)
                return (null, errors);

            feeding.ProductionCycleId = dto.ProductionCycleId;
            feeding.FeedId = dto.FeedId;
            feeding.FeedingDate = dto.FeedingDate;
            feeding.FeedingTime = dto.FeedingTime;
            feeding.QuantityKg = dto.QuantityKg;
            feeding.MealNumber = dto.MealNumber;
            feeding.Behavior = string.IsNullOrWhiteSpace(dto.Behavior) ? null : dto.Behavior.Trim();
            feeding.Observations = string.IsNullOrWhiteSpace(dto.Observations) ? null : dto.Observations.Trim();
            feeding.UserId = userId ?? feeding.UserId;
            feeding.UpdatedAt = DateTime.UtcNow;

            await _feedingRepository.UpdateAsync(feeding);
            var updated = await _feedingRepository.GetByIdWithFeedAsync(id);
            return (MapToDetailDto(updated ?? feeding), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var feeding = await _feedingRepository.GetByIdAsync(id);
            if (feeding == null)
                return false;

            await _feedingRepository.DeleteAsync(feeding);
            return true;
        }

        public async Task<FeedingRecommendationDto?> GetRecommendationAsync(int cycleId, DateOnly? date = null)
        {
            var cycle = await _feedingRepository.GetCycleForRecommendationAsync(cycleId);
            if (cycle == null)
                return null;

            var evalDate = date ?? DateOnly.FromDateTime(DateTime.UtcNow);
            var deadFish = cycle.Mortalities
                .Where(m => m.IsActive != false)
                .Sum(m => m.DeadFishCount);
            var harvestedFish = cycle.Harvests
                .Where(h => h.IsActive != false)
                .Sum(h => h.FishCount);
            var currentFishCount = Math.Max(0, (cycle.InitialFishCount ?? 0) - deadFish - harvestedFish);

            var elapsedDays = Math.Max(0, evalDate.DayNumber - cycle.StartDate.DayNumber);
            var currentAgeDays = (cycle.InitialAgeDays ?? 0) + elapsedDays;

            var latestBiometric = cycle.Biometrics
                .Where(b => b.IsActive != false && b.AverageWeightGrams.HasValue && b.AverageWeightGrams.Value > 0)
                .OrderByDescending(b => b.MeasurementDate)
                .ThenByDescending(b => b.Id)
                .FirstOrDefault();

            var hasBiometrics = latestBiometric != null;

            // Calcular sugerencia por edad (siempre disponible como cálculo base o alternativo)
            var ageRecords = await _feedingRepository.GetFeedingByAgeListAsync(cycle.SpeciesId);
            var ageCandidates = ageRecords.Where(r => r.SpeciesId == cycle.SpeciesId).ToList();
            if (!ageCandidates.Any())
                ageCandidates = ageRecords.Where(r => r.SpeciesId == null).ToList();

            Feedingbyage? matchedAge = ageCandidates
                .Where(r => r.MinimumAgeDays <= currentAgeDays && r.MaximumAgeDays >= currentAgeDays)
                .OrderBy(r => r.MaximumAgeDays - r.MinimumAgeDays)
                .FirstOrDefault();

            if (matchedAge == null && ageCandidates.Any())
            {
                var maxAge = ageCandidates.Max(r => r.MaximumAgeDays);
                if (currentAgeDays >= maxAge)
                    matchedAge = ageCandidates.OrderByDescending(r => r.MaximumAgeDays).FirstOrDefault();
                else
                    matchedAge = ageCandidates.OrderBy(r => r.MinimumAgeDays).FirstOrDefault();
            }

            FeedingRecommendationDto? ageRecommendation = null;
            if (matchedAge != null)
            {
                int refCount = matchedAge.ReferenceFishCount > 0 ? matchedAge.ReferenceFishCount : 1000;
                decimal dailyAmountForRef = matchedAge.DailyAmountKilo ?? 0m;
                decimal dailyTotalKg = Math.Round(dailyAmountForRef * (currentFishCount / (decimal)refCount), 3);
                int dailyMeals = (matchedAge.DailyMeals.HasValue && matchedAge.DailyMeals.Value > 0) ? matchedAge.DailyMeals.Value : 3;
                decimal portionPerMealKg = dailyMeals > 0 ? Math.Round(dailyTotalKg / dailyMeals, 3) : dailyTotalKg;
                bool isPowder = matchedAge.PelletSizeMinMm == null && matchedAge.PelletSizeMaxMm == null;
                decimal? approxWeight = matchedAge.ApproximatedFishWeightGrams;
                decimal? biomassKg = approxWeight.HasValue ? Math.Round((currentFishCount * approxWeight.Value) / 1000m, 2) : null;

                ageRecommendation = new FeedingRecommendationDto
                {
                    Method = "BY_AGE",
                    HasBiometrics = hasBiometrics,
                    ProductionCycleId = cycle.Id,
                    CurrentFishCount = currentFishCount,
                    AverageWeightGrams = approxWeight,
                    CurrentBiomassKg = biomassKg,
                    CurrentAgeDays = currentAgeDays,
                    FeedingRatePercentage = matchedAge.FeedingRatePercentage,
                    DailyTotalKg = dailyTotalKg,
                    DailyMeals = dailyMeals,
                    PortionPerMealKg = portionPerMealKg,
                    IdealFeedPhase = matchedAge.FeedPhase,
                    IdealPelletSizeMinMm = matchedAge.PelletSizeMinMm,
                    IdealPelletSizeMaxMm = matchedAge.PelletSizeMaxMm,
                    IsPowder = isPowder,
                    IdealProteinMinPercentage = matchedAge.ProteinMinPercentage,
                    IdealProteinMaxPercentage = matchedAge.ProteinMaxPercentage,
                    Source = matchedAge.Source
                };
            }

            if (hasBiometrics)
            {
                decimal avgWeightGrams = latestBiometric!.AverageWeightGrams!.Value;
                decimal biomassKg = Math.Round((currentFishCount * avgWeightGrams) / 1000m, 2);

                var weightRecords = await _feedingRepository.GetFeedingByWeightListAsync(cycle.SpeciesId);
                var weightCandidates = weightRecords.Where(r => r.SpeciesId == cycle.SpeciesId).ToList();
                if (!weightCandidates.Any())
                    weightCandidates = weightRecords.Where(r => r.SpeciesId == null).ToList();

                Feedingbyweight? matchedWeight = weightCandidates
                    .Where(r => r.MinimumWeightGrams <= avgWeightGrams && r.MaximumWeightGrams >= avgWeightGrams)
                    .OrderBy(r => r.MaximumWeightGrams - r.MinimumWeightGrams)
                    .FirstOrDefault();

                if (matchedWeight == null && weightCandidates.Any())
                {
                    var maxWeight = weightCandidates.Max(r => r.MaximumWeightGrams);
                    if (avgWeightGrams >= maxWeight)
                        matchedWeight = weightCandidates.OrderByDescending(r => r.MaximumWeightGrams).FirstOrDefault();
                    else
                        matchedWeight = weightCandidates.OrderBy(r => r.MinimumWeightGrams).FirstOrDefault();
                }

                if (matchedWeight != null)
                {
                    decimal feedingRate = matchedWeight.FeedingRatePercentage ?? 0m;
                    decimal dailyTotalKg = Math.Round(biomassKg * (feedingRate / 100m), 3);
                    int dailyMeals = (matchedWeight.DailyMeals.HasValue && matchedWeight.DailyMeals.Value > 0) ? matchedWeight.DailyMeals.Value : 3;
                    decimal portionPerMealKg = dailyMeals > 0 ? Math.Round(dailyTotalKg / dailyMeals, 3) : dailyTotalKg;
                    bool isPowder = matchedWeight.PelletSizeMinMm == null && matchedWeight.PelletSizeMaxMm == null;

                    var weightRecommendation = new FeedingRecommendationDto
                    {
                        Method = "BY_WEIGHT",
                        HasBiometrics = true,
                        ProductionCycleId = cycle.Id,
                        CurrentFishCount = currentFishCount,
                        AverageWeightGrams = avgWeightGrams,
                        CurrentBiomassKg = biomassKg,
                        CurrentAgeDays = currentAgeDays,
                        FeedingRatePercentage = feedingRate,
                        DailyTotalKg = dailyTotalKg,
                        DailyMeals = dailyMeals,
                        PortionPerMealKg = portionPerMealKg,
                        IdealFeedPhase = matchedWeight.FeedPhase,
                        IdealPelletSizeMinMm = matchedWeight.PelletSizeMinMm,
                        IdealPelletSizeMaxMm = matchedWeight.PelletSizeMaxMm,
                        IsPowder = isPowder,
                        IdealProteinMinPercentage = matchedWeight.ProteinMinPercentage,
                        IdealProteinMaxPercentage = matchedWeight.ProteinMaxPercentage,
                        Source = matchedWeight.Source,
                        AlternativeAgeRecommendation = ageRecommendation
                    };

                    return weightRecommendation;
                }
            }

            // Si no tiene biometría o no se encontró registro por peso, se retorna la de edad
            return ageRecommendation;
        }

        private static FeedingDetailDto MapToDetailDto(Feeding feeding)
        {
            return new FeedingDetailDto
            {
                Id = feeding.Id,
                ProductionCycleId = feeding.ProductionCycleId,
                FeedId = feeding.FeedId,
                FeedBrand = feeding.Feed?.Brand ?? string.Empty,
                FeedProductName = feeding.Feed?.ProductName,
                FeedPelletSizeMm = feeding.Feed?.PelletSizeMm,
                FeedProteinPercentage = feeding.Feed?.ProteinPercentage,
                FeedPhase = feeding.Feed?.Phase,
                FeedingDate = feeding.FeedingDate,
                FeedingTime = feeding.FeedingTime,
                QuantityKg = feeding.QuantityKg,
                MealNumber = feeding.MealNumber,
                Behavior = feeding.Behavior,
                Observations = feeding.Observations,
                CreatedAt = feeding.CreatedAt,
                UpdatedAt = feeding.UpdatedAt,
                IsActive = feeding.IsActive
            };
        }
    }
}
