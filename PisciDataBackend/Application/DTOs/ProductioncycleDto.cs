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
