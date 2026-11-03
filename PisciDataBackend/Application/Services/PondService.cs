using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class PondService
    {
        private readonly PondRepository _pondRepository;

        public PondService(PondRepository pondRepository)
        {
            _pondRepository = pondRepository;
        }

        public async Task<IEnumerable<PondDto>> GetAllAsync(int userId)
        {
            var ponds = await _pondRepository.GetByOwnerAsync(userId);
            var references = await _pondRepository.GetActiveWaterQualityReferencesAsync();
            return ponds.Select(pond => MapToDto(pond, references));
        }

        public async Task<PondDto?> GetByIdAsync(int id, int userId)
        {
            var pond = await _pondRepository.GetByIdForOwnerAsync(id, userId);
            if (pond == null)
                return null;

            var references = await _pondRepository.GetActiveWaterQualityReferencesAsync();
            return MapToDto(pond, references);
        }

        public async Task<(PondDto? Dto, List<string> Errors)> CreateAsync(
            CreatePondDto dto, int userId)
        {
            var errors = PondValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            if (!await _pondRepository.FarmBelongsToOwnerAsync(dto.FarmId, userId))
                return (null, new List<string> { "La granja no existe o no pertenece al usuario." });

            var pond = new Pond
            {
                FarmId = dto.FarmId,
                Code = dto.Code,
                Name = dto.Name,
                Shape = dto.Shape,
                Width = dto.Width,
                Length = dto.Length,
                Diameter = dto.Diameter,
                Depth = dto.Depth,
                Area = dto.Area,
                Description = dto.Description,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _pondRepository.AddAsync(pond);
            return (MapToDto(pond, Array.Empty<Waterqualityreference>()), errors);
        }

        public async Task<(PondDto? Dto, List<string> Errors)> UpdateAsync(
            int id, UpdatePondDto dto, int userId)
        {
            var errors = PondValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var pond = await _pondRepository.GetByIdForOwnerAsync(id, userId);
            if (pond == null)
                return (null, errors);

            if (!await _pondRepository.FarmBelongsToOwnerAsync(dto.FarmId, userId))
                return (null, new List<string> { "La granja no existe o no pertenece al usuario." });

            pond.FarmId = dto.FarmId;
            pond.Code = dto.Code;
            pond.Name = dto.Name;
            pond.Shape = dto.Shape;
            pond.Width = dto.Width;
            pond.Length = dto.Length;
            pond.Diameter = dto.Diameter;
            pond.Depth = dto.Depth;
            pond.Area = dto.Area;
            pond.Description = dto.Description;
            pond.UpdatedAt = DateTime.UtcNow;

            await _pondRepository.UpdateAsync(pond);
            return (MapToDto(pond, Array.Empty<Waterqualityreference>()), errors);
        }

        public async Task<bool> DeleteAsync(int id, int userId)
        {
            var pond = await _pondRepository.GetByIdForOwnerAsync(id, userId);
            if (pond == null)
                return false;

            await _pondRepository.DeleteAsync(pond);
            return true;
        }

        private static PondDto MapToDto(
            Pond pond, IReadOnlyCollection<Waterqualityreference> references)
        {
            var latestMeasurement = pond.Productioncycles
                .SelectMany(cycle => cycle.Waterqualities.Select(quality => new
                {
                    Quality = quality,
                    cycle.SpeciesId
                }))
                .OrderByDescending(item => item.Quality.MeasurementDate)
                .ThenByDescending(item => item.Quality.MeasurementTime)
                .ThenByDescending(item => item.Quality.Id)
                .FirstOrDefault();

            return new PondDto
            {
                Id = pond.Id,
                FarmId = pond.FarmId,
                Code = pond.Code,
                Name = pond.Name,
                Shape = pond.Shape,
                Width = pond.Width,
                Length = pond.Length,
                Diameter = pond.Diameter,
                Depth = pond.Depth,
                Area = pond.Area,
                Description = pond.Description,
                CreatedAt = pond.CreatedAt,
                UpdatedAt = pond.UpdatedAt,
                IsActive = pond.IsActive,
                LatestWaterQuality = latestMeasurement == null
                    ? null
                    : MapWaterQuality(latestMeasurement.Quality,
                        latestMeasurement.SpeciesId, references)
            };
        }

        private static WaterQualityDto MapWaterQuality(
            Waterquality quality, int speciesId,
            IReadOnlyCollection<Waterqualityreference> references)
        {
            var result = new WaterQualityDto
            {
                Id = quality.Id,
                ProductionCycleId = quality.ProductionCycleId,
                MeasurementDate = quality.MeasurementDate,
                MeasurementTime = quality.MeasurementTime,
                TransparencyCm = quality.TransparencyCm,
                TemperatureC = quality.TemperatureC,
                Ph = quality.Ph,
                DissolvedOxygen = quality.DissolvedOxygen,
                Observations = quality.Observations
            };

            result.Parameters.Add(CreateParameter(
                "TRANSPARENCY", "cm", quality.TransparencyCm, speciesId, references));
            result.Parameters.Add(CreateParameter(
                "PH", "pH", quality.Ph, speciesId, references));
            result.Parameters.Add(CreateParameter(
                "TEMPERATURE", "C", quality.TemperatureC, speciesId, references));
            result.Parameters.Add(CreateParameter(
                "DISSOLVED_OXYGEN", "mg/L", quality.DissolvedOxygen, speciesId, references));

            return result;
        }

        private static WaterQualityParameterDto CreateParameter(
            string parameter, string unit, decimal? value, int speciesId,
            IReadOnlyCollection<Waterqualityreference> references)
        {
            var reference = references
                .Where(item => item.Parameter == parameter
                    && item.Unit == unit
                    && (item.SpeciesId == speciesId || item.SpeciesId == null))
                .OrderBy(item => item.SpeciesId == null)
                .ThenByDescending(item => item.Id)
                .FirstOrDefault();

            return new WaterQualityParameterDto
            {
                Parameter = parameter,
                Unit = unit,
                Value = value,
                OptimalMin = reference?.OptimalMin,
                OptimalMax = reference?.OptimalMax,
                WarningMin = reference?.WarningMin,
                WarningMax = reference?.WarningMax,
                CriticalMin = reference?.CriticalMin,
                CriticalMax = reference?.CriticalMax,
                Status = GetQualityStatus(value, reference),
                RecommendedAction = reference?.RecommendedAction
            };
        }

        private static string GetQualityStatus(
            decimal? value, Waterqualityreference? reference)
        {
            if (!value.HasValue || reference == null)
                return "NO DISPONIBLE";

            if ((reference.CriticalMin.HasValue && value.Value <= reference.CriticalMin.Value)
                || (reference.CriticalMax.HasValue && value.Value >= reference.CriticalMax.Value))
                return "CRÍTICO";

            var isOptimal = (!reference.OptimalMin.HasValue
                    || value.Value >= reference.OptimalMin.Value)
                && (!reference.OptimalMax.HasValue
                    || value.Value <= reference.OptimalMax.Value);
            if (isOptimal)
                return "ÓPTIMO";

            var isWarning = (reference.OptimalMin.HasValue
                    && value.Value < reference.OptimalMin.Value
                    && (!reference.WarningMin.HasValue
                        || value.Value >= reference.WarningMin.Value))
                || (reference.OptimalMax.HasValue
                    && value.Value > reference.OptimalMax.Value
                    && (!reference.WarningMax.HasValue
                        || value.Value <= reference.WarningMax.Value));

            return isWarning ? "ADVERTENCIA" : "NO DISPONIBLE";
        }
    }
}
