namespace PisciDataBackend.Application.DTOs
{
    public class FeedingDto
    {
        public int Id { get; set; }
        public int ProductionCycleId { get; set; }
        public int FeedId { get; set; }
        public DateOnly FeedingDate { get; set; }
        public TimeOnly FeedingTime { get; set; }
        public decimal QuantityKg { get; set; }
        public int? MealNumber { get; set; }
        public string? Behavior { get; set; }
        public string? Observations { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public bool? IsActive { get; set; }
    }

    public class CreateFeedingDto
    {
        public int ProductionCycleId { get; set; }
        public int FeedId { get; set; }
        public DateOnly FeedingDate { get; set; }
        public TimeOnly FeedingTime { get; set; }
        public decimal QuantityKg { get; set; }
        public int? MealNumber { get; set; }
        public string? Behavior { get; set; }
        public string? Observations { get; set; }
    }

    public class UpdateFeedingDto
    {
        public int ProductionCycleId { get; set; }
        public int FeedId { get; set; }
        public DateOnly FeedingDate { get; set; }
        public TimeOnly FeedingTime { get; set; }
        public decimal QuantityKg { get; set; }
        public int? MealNumber { get; set; }
        public string? Behavior { get; set; }
        public string? Observations { get; set; }
    }

    public class FeedingDetailDto
    {
        public int Id { get; set; }
        public int ProductionCycleId { get; set; }
        public int FeedId { get; set; }
        public string FeedBrand { get; set; } = string.Empty;
        public string? FeedProductName { get; set; }
        public decimal? FeedPelletSizeMm { get; set; }
        public decimal? FeedProteinPercentage { get; set; }
        public string? FeedPhase { get; set; }
        public DateOnly FeedingDate { get; set; }
        public TimeOnly FeedingTime { get; set; }
        public decimal QuantityKg { get; set; }
        public int? MealNumber { get; set; }
        public string? Behavior { get; set; }
        public string? Observations { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public bool? IsActive { get; set; }
    }

    public class FeedingRecommendationDto
    {
        public string Method { get; set; } = "BY_WEIGHT"; // "BY_WEIGHT" or "BY_AGE"
        public bool HasBiometrics { get; set; }
        public int ProductionCycleId { get; set; }
        public int CurrentFishCount { get; set; }
        public decimal? AverageWeightGrams { get; set; }
        public decimal? CurrentBiomassKg { get; set; }
        public int CurrentAgeDays { get; set; }
        public decimal? FeedingRatePercentage { get; set; }
        public decimal DailyTotalKg { get; set; }
        public int DailyMeals { get; set; }
        public decimal PortionPerMealKg { get; set; }
        public string? IdealFeedPhase { get; set; }
        public decimal? IdealPelletSizeMinMm { get; set; }
        public decimal? IdealPelletSizeMaxMm { get; set; }
        public bool IsPowder { get; set; }
        public decimal? IdealProteinMinPercentage { get; set; }
        public decimal? IdealProteinMaxPercentage { get; set; }
        public string? Source { get; set; }
        public FeedingRecommendationDto? AlternativeAgeRecommendation { get; set; }
    }
}
