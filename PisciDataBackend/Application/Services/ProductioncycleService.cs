using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class ProductioncycleService
    {
        private readonly ProductioncycleRepository _productioncycleRepository;

        public ProductioncycleService(ProductioncycleRepository productioncycleRepository)
        {
            _productioncycleRepository = productioncycleRepository;
        }

        public async Task<IEnumerable<ProductioncycleDto>> GetByFarmAsync(int farmId, int userId)
        {
            var cycles = await _productioncycleRepository.GetByFarmOwnerAsync(farmId, userId);
            var feedingByAge = await _productioncycleRepository.GetActiveFeedingByAgeAsync();
            return cycles.Select(cycle => MapToDto(cycle, feedingByAge));
        }

        public async Task<ProductioncycleDto?> GetByIdAsync(int id, int userId)
        {
            var cycle = await _productioncycleRepository.GetOwnedByIdAsync(id, userId);
            if (cycle == null)
                return null;

            var feedingByAge = await _productioncycleRepository.GetActiveFeedingByAgeAsync();
            return MapToDto(cycle, feedingByAge);
        }

        public async Task<(ProductioncycleDto? Dto, List<string> Errors)> CreateAsync(
            CreateProductioncycleDto dto, int userId)
        {
            var errors = ProductioncycleValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            if (!await _productioncycleRepository.PondBelongsToOwnerAsync(dto.PondId, userId)
                || !await _productioncycleRepository.SpeciesExistsAsync(dto.SpeciesId))
                return (null, new List<string> { "El estanque o la especie no existen o no están disponibles." });

            var cycle = new Productioncycle
            {
                PondId = dto.PondId,
                SpeciesId = dto.SpeciesId,
                StartDate = dto.StartDate,
                EndDate = dto.EndDate,
                InitialFishCount = dto.InitialFishCount,
                InitialAverageWeightGrams = dto.InitialAverageWeightGrams,
                InitialAgeDays = dto.InitialAgeDays,
                Observations = dto.Observations,
                UserId = userId,
                IsActive = true
            };

            await _productioncycleRepository.AddAsync(cycle);
            var created = await _productioncycleRepository.GetOwnedByIdAsync(cycle.Id, userId);
            if (created == null)
                return (null, new List<string> { "No se pudo recuperar el ciclo creado." });

            var feedingByAge = await _productioncycleRepository.GetActiveFeedingByAgeAsync();
            return (MapToDto(created, feedingByAge), errors);
        }

        public async Task<(ProductioncycleDto? Dto, List<string> Errors)> UpdateAsync(
            int id, UpdateProductioncycleDto dto, int userId)
        {
            var errors = ProductioncycleValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var cycle = await _productioncycleRepository.GetOwnedByIdAsync(id, userId);
            if (cycle == null
                || !await _productioncycleRepository.PondBelongsToOwnerAsync(dto.PondId, userId)
                || !await _productioncycleRepository.SpeciesExistsAsync(dto.SpeciesId))
                return (null, errors);

            cycle.PondId = dto.PondId;
            cycle.SpeciesId = dto.SpeciesId;
            cycle.StartDate = dto.StartDate;
            cycle.EndDate = dto.EndDate;
            cycle.InitialFishCount = dto.InitialFishCount;
            cycle.InitialAverageWeightGrams = dto.InitialAverageWeightGrams;
            cycle.InitialAgeDays = dto.InitialAgeDays;
            cycle.Observations = dto.Observations;
            cycle.UserId = userId;

            await _productioncycleRepository.UpdateAsync(cycle);
            var updated = await _productioncycleRepository.GetOwnedByIdAsync(id, userId);
            if (updated == null)
                return (null, errors);

            var feedingByAge = await _productioncycleRepository.GetActiveFeedingByAgeAsync();
            return (MapToDto(updated, feedingByAge), errors);
        }

        public async Task<bool> DeleteAsync(int id, int userId)
        {
            var cycle = await _productioncycleRepository.GetOwnedByIdAsync(id, userId);
            if (cycle == null)
                return false;

            cycle.UserId = userId;
            await _productioncycleRepository.DeleteAsync(cycle);
            return true;
        }

        private static ProductioncycleDto MapToDto(
            Productioncycle cycle, IReadOnlyCollection<Feedingbyage> feedingByAge)
        {
            var effectiveDate = cycle.EndDate ?? DateOnly.FromDateTime(DateTime.UtcNow);
            var elapsedDays = Math.Max(0, effectiveDate.DayNumber - cycle.StartDate.DayNumber);
            int? currentAgeDays = cycle.InitialAgeDays.HasValue
                ? cycle.InitialAgeDays.Value + elapsedDays
                : null;

            var harvestedFish = cycle.Harvests
                .Where(harvest => harvest.IsActive != false)
                .Sum(harvest => harvest.FishCount);
            var deadFish = cycle.Mortalities
                .Where(mortality => mortality.IsActive != false)
                .Sum(mortality => mortality.DeadFishCount);
            int? currentFishCount = cycle.InitialFishCount.HasValue
                ? Math.Max(0, cycle.InitialFishCount.Value - harvestedFish - deadFish)
                : null;

            var biometricWeight = cycle.Biometrics
                .Where(biometric => biometric.IsActive != false
                    && biometric.AverageWeightGrams.HasValue
                    && biometric.AverageWeightGrams.Value > 0)
                .OrderByDescending(biometric => biometric.MeasurementDate)
                .ThenByDescending(biometric => biometric.Id)
                .Select(biometric => biometric.AverageWeightGrams)
                .FirstOrDefault();

            var estimatedWeight = biometricWeight ?? feedingByAge
                .Where(item => item.MinimumAgeDays <= (currentAgeDays ?? 0)
                    && item.MaximumAgeDays >= (currentAgeDays ?? 0)
                    && (item.SpeciesId == cycle.SpeciesId || item.SpeciesId == null))
                .OrderBy(item => item.SpeciesId == null)
                .ThenBy(item => item.MaximumAgeDays - item.MinimumAgeDays)
                .ThenByDescending(item => item.MinimumAgeDays)
                .ThenBy(item => item.Id)
                .Select(item => item.ApproximatedFishWeightGrams)
                .FirstOrDefault();

            var status = cycle.EndDate.HasValue || cycle.IsActive == false
                ? "FINALIZADO"
                : currentFishCount.HasValue && currentFishCount.Value > 0
                    ? "ACTIVO"
                    : "EN_SEGUIMIENTO";

            return new ProductioncycleDto
            {
                Id = cycle.Id,
                PondId = cycle.PondId,
                SpeciesId = cycle.SpeciesId,
                StartDate = cycle.StartDate,
                EndDate = cycle.EndDate,
                InitialFishCount = cycle.InitialFishCount,
                InitialAverageWeightGrams = cycle.InitialAverageWeightGrams,
                InitialAgeDays = cycle.InitialAgeDays,
                Observations = cycle.Observations,
                CreatedAt = cycle.CreatedAt,
                UpdatedAt = cycle.UpdatedAt,
                IsActive = cycle.IsActive,
                FarmId = cycle.Pond.FarmId,
                FarmName = cycle.Pond.Farm.Name,
                FarmIsActive = cycle.Pond.Farm.IsActive,
                PondCode = cycle.Pond.Code,
                PondName = cycle.Pond.Name,
                SpeciesName = cycle.Species.CommonName,
                CurrentFishCount = currentFishCount,
                CurrentAverageWeightGrams = estimatedWeight,
                CurrentAgeDays = currentAgeDays,
                Status = status
            };
        }
    }
}