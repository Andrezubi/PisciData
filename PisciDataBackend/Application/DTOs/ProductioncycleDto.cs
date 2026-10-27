namespace PisciDataBackend.Application.DTOs
{
    public class ProductioncycleDto
    {
        public int Id { get; set; }
        public int PondId { get; set; }
        public int SpeciesId { get; set; }
        public DateOnly StartDate { get; set; }
        public DateOnly? EndDate { get; set; }
        public int? InitialFishCount { get; set; }
        public decimal? InitialAverageWeightGrams { get; set; }
        public int? InitialAgeDays { get; set; }
        public string? Observations { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public bool? IsActive { get; set; }
        public int FarmId { get; set; }
        public string FarmName { get; set; } = null!;
        public bool? FarmIsActive { get; set; }
        public string PondCode { get; set; } = null!;
        public string? PondName { get; set; }
        public string SpeciesName { get; set; } = null!;
        public int? CurrentFishCount { get; set; }
        public decimal? CurrentAverageWeightGrams { get; set; }
        public int? CurrentAgeDays { get; set; }
        public string Status { get; set; } = null!;
    }

    public class CreateProductioncycleDto
    {
        public int PondId { get; set; }
        public int SpeciesId { get; set; }
        public DateOnly StartDate { get; set; }
        public DateOnly? EndDate { get; set; }
        public int? InitialFishCount { get; set; }
        public decimal? InitialAverageWeightGrams { get; set; }
        public int? InitialAgeDays { get; set; }
        public string? Observations { get; set; }
    }

    public class UpdateProductioncycleDto
    {
        public int PondId { get; set; }
        public int SpeciesId { get; set; }
        public DateOnly StartDate { get; set; }
        public DateOnly? EndDate { get; set; }
        public int? InitialFishCount { get; set; }
        public decimal? InitialAverageWeightGrams { get; set; }
        public int? InitialAgeDays { get; set; }
        public string? Observations { get; set; }
    }
}
