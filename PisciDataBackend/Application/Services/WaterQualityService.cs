using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Repositories;
using WaterQualityDto = PisciDataBackend.Application.DTOs.WaterQualityDto;
using WaterQualityParameterDto = PisciDataBackend.Application.DTOs.WaterQualityParameterDto;

namespace PisciDataBackend.Application.Services
{
    public class WaterQualityService
    {
        private readonly WaterQualityRepository _waterQualityRepository;
        private readonly FarmRepository _farmRepository;

        public WaterQualityService(
            WaterQualityRepository waterQualityRepository,
            FarmRepository farmRepository)
        {
            _waterQualityRepository = waterQualityRepository;
            _farmRepository = farmRepository;
        }

        public async Task<IEnumerable<WaterQualityDto>?> GetByFarmAsync(
            int farmId, int userId, DateOnly? from, DateOnly? to)
        {
            if (!await _farmRepository.EnsureFarmOwnedByUser(farmId, userId))
                return null;

            var items = await _waterQualityRepository.GetByFarmOwnerAsync(farmId, userId, from, to);
            var references = await _waterQualityRepository.GetActiveReferencesAsync();
            return items.Select(item => MapToDto(item, references));
        }

        public async Task<IEnumerable<WaterQualityDto>?> GetByPondAsync(
            int pondId, int userId, DateOnly? from, DateOnly? to)
        {
            var pond = await _waterQualityRepository.GetPondForOwnerAsync(pondId, userId);
            if (pond == null || !await _farmRepository.EnsureFarmOwnedByUser(pond.FarmId, userId))
                return null;

            var items = await _waterQualityRepository.GetByPondOwnerAsync(pondId, userId, from, to);
            var references = await _waterQualityRepository.GetActiveReferencesAsync();
            return items.Select(item => MapToDto(item, references));
        }

        public async Task<WaterQualityDto?> GetLatestByPondAsync(int pondId, int userId)
        {
            var pond = await _waterQualityRepository.GetPondForOwnerAsync(pondId, userId);
            if (pond == null || !await _farmRepository.EnsureFarmOwnedByUser(pond.FarmId, userId))
                return null;

            var item = await _waterQualityRepository.GetLatestByPondOwnerAsync(pondId, userId);
            if (item == null)
                return null;

            var references = await _waterQualityRepository.GetActiveReferencesAsync();
            return MapToDto(item, references);
        }

        public async Task<WaterQualitySummaryDto?> GetSummaryByPondAsync(int pondId, int userId)
        {
            var pond = await _waterQualityRepository.GetPondForOwnerAsync(pondId, userId);
            if (pond == null || !await _farmRepository.EnsureFarmOwnedByUser(pond.FarmId, userId))
                return null;

            var latest = await _waterQualityRepository.GetLatestByPondOwnerAsync(pondId, userId);
            var references = await _waterQualityRepository.GetActiveReferencesAsync();
            var latestDto = latest == null ? null : MapToDto(latest, references);

            var summary = new WaterQualitySummaryDto
            {
                PondId = pond.Id,
                PondCode = pond.Code,
                PondName = pond.Name,
                FarmId = pond.FarmId,
                FarmName = pond.Farm.Name,
                ProductionCycleId = latest?.ProductionCycleId,
                LatestMeasurement = latestDto,
                Status = "SIN MEDICIONES"
            };

            if (latestDto == null)
                return summary;

            summary.ParametersInRange = latestDto.Parameters.Count(p => p.Status == "ÓPTIMO");
            summary.ParametersOutOfRange = latestDto.Parameters.Count(p =>
                p.Status == "ADVERTENCIA" || p.Status == "CRÍTICO");
            summary.ParametersRequiringAttention = latestDto.Parameters.Count(p =>
                p.Status == "ADVERTENCIA" || p.Status == "CRÍTICO");
            summary.ParametersWithoutReference = latestDto.Parameters.Count(p =>
                p.Status == "NO DISPONIBLE");

            if (latestDto.Parameters.Any(p => p.Status == "CRÍTICO"))
                summary.Status = "CRÍTICO";
            else if (latestDto.Parameters.Any(p => p.Status == "ADVERTENCIA"))
                summary.Status = "ADVERTENCIA";
            else if (latestDto.Parameters.Any(p => p.Status == "ÓPTIMO"))
                summary.Status = "ÓPTIMO";
            else
                summary.Status = "SIN REFERENCIA";

            return summary;
        }

        public async Task<WaterQualityDto?> GetByIdAsync(int id, int userId)
        {
            var item = await _waterQualityRepository.GetOwnedByIdAsync(id, userId);
            if (item == null)
                return null;

            if (!await _farmRepository.EnsureFarmOwnedByUser(
                    item.ProductionCycle.Pond.FarmId, userId))
                return null;

            var references = await _waterQualityRepository.GetActiveReferencesAsync();
            return MapToDto(item, references);
        }

        public async Task<(WaterQualityDto? Dto, List<string> Errors)> CreateAsync(
            CreateWaterQualityDto dto, int userId)
        {
            var errors = WaterQualityValidator.ValidateCreate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var pond = await _waterQualityRepository.GetPondForOwnerAsync(dto.PondId, userId);
            if (pond == null || !await _farmRepository.EnsureFarmOwnedByUser(pond.FarmId, userId))
                return (null, new List<string> { "El estanque no existe o no pertenece al usuario." });

            var cycle = await _waterQualityRepository.GetActiveCycleForPondOwnerAsync(
                dto.PondId, dto.ProductionCycleId, userId);
            if (cycle == null)
                return (null, new List<string> { "El estanque no tiene un ciclo productivo activo disponible." });

            var item = new Waterquality
            {
                ProductionCycleId = cycle.Id,
                MeasurementDate = dto.MeasurementDate,
                MeasurementTime = dto.MeasurementTime,
                TransparencyCm = dto.TransparencyCm,
                TemperatureC = dto.TemperatureC,
                Ph = dto.Ph,
                DissolvedOxygen = dto.DissolvedOxygen,
                Observations = dto.Observations,
                IsActive = true,
                UserId = userId
            };

            await _waterQualityRepository.AddAsync(item);
            var created = await _waterQualityRepository.GetOwnedByIdAsync(item.Id, userId);
            if (created == null)
                return (null, new List<string> { "No se pudo recuperar la medición creada." });

            var references = await _waterQualityRepository.GetActiveReferencesAsync();
            return (MapToDto(created, references), errors);
        }

        public async Task<(WaterQualityDto? Dto, List<string> Errors)> UpdateAsync(
            int id, UpdateWaterQualityDto dto, int userId)
        {
            var errors = WaterQualityValidator.ValidateUpdate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var item = await _waterQualityRepository.GetTrackedOwnedByIdAsync(id, userId);
            if (item == null)
                return (null, new List<string> { "La medición no existe o no pertenece al usuario." });

            if (!await _farmRepository.EnsureFarmOwnedByUser(
                    item.ProductionCycle.Pond.FarmId, userId))
                return (null, new List<string> { "La medición no existe o no pertenece al usuario." });

            item.MeasurementDate = dto.MeasurementDate;
            item.MeasurementTime = dto.MeasurementTime;
            item.TransparencyCm = dto.TransparencyCm;
            item.TemperatureC = dto.TemperatureC;
            item.Ph = dto.Ph;
            item.DissolvedOxygen = dto.DissolvedOxygen;
            item.Observations = dto.Observations;
            item.UserId = userId;

            await _waterQualityRepository.UpdateAsync(item);
            var updated = await _waterQualityRepository.GetOwnedByIdAsync(id, userId);
            if (updated == null)
                return (null, new List<string> { "No se pudo recuperar la medición actualizada." });

            var references = await _waterQualityRepository.GetActiveReferencesAsync();
            return (MapToDto(updated, references), errors);
        }

        public async Task<bool> DeleteAsync(int id, int userId)
        {
            var item = await _waterQualityRepository.GetTrackedOwnedByIdAsync(id, userId);
            if (item == null)
                return false;

            if (!await _farmRepository.EnsureFarmOwnedByUser(
                    item.ProductionCycle.Pond.FarmId, userId))
                return false;

            item.UserId = userId;
            await _waterQualityRepository.DeleteAsync(item);
            return true;
        }

        public async Task<IEnumerable<WaterQualityReferenceDto>> GetReferencesAsync()
        {
            var references = await _waterQualityRepository.GetActiveReferencesAsync();
            return references.Select(reference => new WaterQualityReferenceDto
            {
                Id = reference.Id,
                SpeciesId = reference.SpeciesId,
                Parameter = reference.Parameter,
                Unit = reference.Unit,
                OptimalMin = reference.OptimalMin,
                OptimalMax = reference.OptimalMax,
                WarningMin = reference.WarningMin,
                WarningMax = reference.WarningMax,
                CriticalMin = reference.CriticalMin,
                CriticalMax = reference.CriticalMax,
                RecommendedAction = reference.RecommendedAction,
                Source = reference.Source
            });
        }

        private static WaterQualityDto MapToDto(
            Waterquality item,
            IReadOnlyCollection<Waterqualityreference> references)
        {
            var cycle = item.ProductionCycle;
            var pond = cycle.Pond;
            var result = new WaterQualityDto
            {
                Id = item.Id,
                ProductionCycleId = item.ProductionCycleId,
                PondId = pond.Id,
                PondCode = pond.Code,
                PondName = pond.Name,
                FarmId = pond.FarmId,
                FarmName = pond.Farm.Name,
                SpeciesId = cycle.SpeciesId,
                SpeciesName = cycle.Species.CommonName,
                MeasurementDate = item.MeasurementDate,
                MeasurementTime = item.MeasurementTime,
                TransparencyCm = item.TransparencyCm,
                TemperatureC = item.TemperatureC,
                Ph = item.Ph,
                DissolvedOxygen = item.DissolvedOxygen,
                Observations = item.Observations,
                CreatedAt = item.CreatedAt,
                UpdatedAt = item.UpdatedAt,
                IsActive = item.IsActive
            };

            result.Parameters.Add(CreateParameter(
                "TRANSPARENCY", "cm", item.TransparencyCm, cycle.SpeciesId, references));
            result.Parameters.Add(CreateParameter(
                "PH", "pH", item.Ph, cycle.SpeciesId, references));
            result.Parameters.Add(CreateParameter(
                "TEMPERATURE", "C", item.TemperatureC, cycle.SpeciesId, references));
            result.Parameters.Add(CreateParameter(
                "DISSOLVED_OXYGEN", "mg/L", item.DissolvedOxygen, cycle.SpeciesId, references));

            return result;
        }

        private static WaterQualityParameterDto CreateParameter(
            string parameter,
            string unit,
            decimal? value,
            int speciesId,
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
            decimal? value,
            Waterqualityreference? reference)
        {
            if (!value.HasValue || reference == null)
                return "NO DISPONIBLE";

            if ((reference.CriticalMin.HasValue
                    && value.Value <= reference.CriticalMin.Value)
                || (reference.CriticalMax.HasValue
                    && value.Value >= reference.CriticalMax.Value))
                return "CRÍTICO";

            var isOptimal = (!reference.OptimalMin.HasValue
                    || value.Value >= reference.OptimalMin.Value)
                && (!reference.OptimalMax.HasValue
                    || value.Value <= reference.OptimalMax.Value);
            if (isOptimal)
                return "ÓPTIMO";

            var isWarning = (reference.WarningMin.HasValue
                    && reference.OptimalMin.HasValue
                    && value.Value < reference.OptimalMin.Value
                    && value.Value >= reference.WarningMin.Value)
                || (reference.WarningMax.HasValue
                    && reference.OptimalMax.HasValue
                    && value.Value > reference.OptimalMax.Value
                    && value.Value <= reference.WarningMax.Value);

            return isWarning ? "ADVERTENCIA" : "NO DISPONIBLE";
        }
    }
}
