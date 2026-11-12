namespace PisciDataBackend.Application.DTOs
{
    public class PondDto
    {
        public int Id { get; set; }
        public int FarmId { get; set; }
        public string FarmName { get; set; } = null!;
        public string Code { get; set; } = null!;
        public string? Name { get; set; }
        public string? Shape { get; set; }
        public decimal? Width { get; set; }
        public decimal? Length { get; set; }
        public decimal? Diameter { get; set; }
        public decimal? Depth { get; set; }
        public decimal? Area { get; set; }
        public string? Description { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public bool? IsActive { get; set; }
        public WaterQualityDto? LatestWaterQuality { get; set; }
    }

    public class WaterQualityDto
    {
        public int Id { get; set; }
        public int ProductionCycleId { get; set; }
        public DateOnly MeasurementDate { get; set; }
        public TimeOnly MeasurementTime { get; set; }
        public decimal? TransparencyCm { get; set; }
        public decimal? TemperatureC { get; set; }
        public decimal? Ph { get; set; }
        public decimal? DissolvedOxygen { get; set; }
        public string? Observations { get; set; }
        public List<WaterQualityParameterDto> Parameters { get; set; } = new();
    }

    public class WaterQualityParameterDto
    {
        public string Parameter { get; set; } = null!;
        public string Unit { get; set; } = null!;
        public decimal? Value { get; set; }
        public decimal? OptimalMin { get; set; }
        public decimal? OptimalMax { get; set; }
        public decimal? WarningMin { get; set; }
        public decimal? WarningMax { get; set; }
        public decimal? CriticalMin { get; set; }
        public decimal? CriticalMax { get; set; }
        public string Status { get; set; } = "NO DISPONIBLE";
        public string? RecommendedAction { get; set; }
    }

    public class CreatePondDto
    {
        public int FarmId { get; set; }
        public string Code { get; set; } = null!;
        public string? Name { get; set; }
        public string? Shape { get; set; }
        public decimal? Width { get; set; }
        public decimal? Length { get; set; }
        public decimal? Diameter { get; set; }
        public decimal? Depth { get; set; }
        public decimal? Area { get; set; }
        public string? Description { get; set; }
    }

    public class UpdatePondDto
    {
        public int FarmId { get; set; }
        public string Code { get; set; } = null!;
        public string? Name { get; set; }
        public string? Shape { get; set; }
        public decimal? Width { get; set; }
        public decimal? Length { get; set; }
        public decimal? Diameter { get; set; }
        public decimal? Depth { get; set; }
        public decimal? Area { get; set; }
        public string? Description { get; set; }
    }
}
