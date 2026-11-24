namespace PisciDataBackend.Application.DTOs
{
    public class CreateWaterQualityDto
    {
        public int PondId { get; set; }
        public int? ProductionCycleId { get; set; }
        public DateOnly MeasurementDate { get; set; }
        public TimeOnly MeasurementTime { get; set; }
        public decimal? TransparencyCm { get; set; }
        public decimal? TemperatureC { get; set; }
        public decimal? Ph { get; set; }
        public decimal? DissolvedOxygen { get; set; }
        public string? Observations { get; set; }
    }

    public class UpdateWaterQualityDto
    {
        public DateOnly MeasurementDate { get; set; }
        public TimeOnly MeasurementTime { get; set; }
        public decimal? TransparencyCm { get; set; }
        public decimal? TemperatureC { get; set; }
        public decimal? Ph { get; set; }
        public decimal? DissolvedOxygen { get; set; }
        public string? Observations { get; set; }
    }

    public class WaterQualityReferenceDto
    {
        public int Id { get; set; }
        public int? SpeciesId { get; set; }
        public string Parameter { get; set; } = null!;
        public string Unit { get; set; } = null!;
        public decimal? OptimalMin { get; set; }
        public decimal? OptimalMax { get; set; }
        public decimal? WarningMin { get; set; }
        public decimal? WarningMax { get; set; }
        public decimal? CriticalMin { get; set; }
        public decimal? CriticalMax { get; set; }
        public string? RecommendedAction { get; set; }
        public string? Source { get; set; }
    }

    public class WaterQualitySummaryDto
    {
        public int PondId { get; set; }
        public string PondCode { get; set; } = null!;
        public string? PondName { get; set; }
        public int FarmId { get; set; }
        public string FarmName { get; set; } = null!;
        public int? ProductionCycleId { get; set; }
        public WaterQualityDto? LatestMeasurement { get; set; }
        public int ParametersInRange { get; set; }
        public int ParametersOutOfRange { get; set; }
        public int ParametersRequiringAttention { get; set; }
        public int ParametersWithoutReference { get; set; }
        public string Status { get; set; } = "SIN MEDICIONES";
    }
}
