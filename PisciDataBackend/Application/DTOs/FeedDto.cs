namespace PisciDataBackend.Application.DTOs
{
    public class FeedDto
    {
        public int Id { get; set; }
        public int FarmId { get; set; }
        public string Brand { get; set; } = null!;
        public string? ProductName { get; set; }
        public string? Phase { get; set; }
        public decimal? ProteinPercentage { get; set; }
        public decimal? PelletSizeMm { get; set; }
        public decimal? StockKg { get; set; }
        public decimal? MinimumStockKg { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public bool? IsActive { get; set; }
    }

    public class CreateFeedDto
    {
        public int FarmId { get; set; }
        public string Brand { get; set; } = null!;
        public string? ProductName { get; set; }
        public string? Phase { get; set; }
        public decimal? ProteinPercentage { get; set; }
        public decimal? PelletSizeMm { get; set; }
        public decimal? StockKg { get; set; }
        public decimal? MinimumStockKg { get; set; }
    }

    public class UpdateFeedDto
    {
        public int FarmId { get; set; }
        public string Brand { get; set; } = null!;
        public string? ProductName { get; set; }
        public string? Phase { get; set; }
        public decimal? ProteinPercentage { get; set; }
        public decimal? PelletSizeMm { get; set; }
        public decimal? StockKg { get; set; }
        public decimal? MinimumStockKg { get; set; }
    }
}
