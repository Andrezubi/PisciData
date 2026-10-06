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
}
