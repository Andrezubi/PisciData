namespace PisciDataBackend.Application.DTOs
{
    public class PondDto
    {
        public int Id { get; set; }
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
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public bool? IsActive { get; set; }
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
