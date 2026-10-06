namespace PisciDataBackend.Application.DTOs
{
    public class SupplyDto
    {
        public int Id { get; set; }
        public int FarmId { get; set; }
        public string Name { get; set; } = null!;
        public string? Category { get; set; }
        public decimal Quantity { get; set; }
        public string Unit { get; set; } = null!;
        public decimal? MinimumStock { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public bool? IsActive { get; set; }
    }

    public class CreateSupplyDto
    {
        public int FarmId { get; set; }
        public string Name { get; set; } = null!;
        public string? Category { get; set; }
        public decimal Quantity { get; set; }
        public string Unit { get; set; } = null!;
        public decimal? MinimumStock { get; set; }
    }

    public class UpdateSupplyDto
    {
        public int FarmId { get; set; }
        public string Name { get; set; } = null!;
        public string? Category { get; set; }
        public decimal Quantity { get; set; }
        public string Unit { get; set; } = null!;
        public decimal? MinimumStock { get; set; }
    }
}
