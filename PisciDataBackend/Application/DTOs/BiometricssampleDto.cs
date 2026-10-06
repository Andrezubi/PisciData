namespace PisciDataBackend.Application.DTOs
{
    public class BiometricssampleDto
    {
        public int Id { get; set; }
        public int BiometricsId { get; set; }
        public int SampleNumber { get; set; }
        public decimal? BucketWaterWeightKg { get; set; }
        public decimal? BucketWithFishWeightKg { get; set; }
        public int FishCount { get; set; }
        public decimal? TotalFishWeightKg { get; set; }
        public decimal? AverageWeightGrams { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public bool? IsActive { get; set; }
    }

    public class CreateBiometricssampleDto
    {
        public int BiometricsId { get; set; }
        public int SampleNumber { get; set; }
        public decimal? BucketWaterWeightKg { get; set; }
        public decimal? BucketWithFishWeightKg { get; set; }
        public int FishCount { get; set; }
        public decimal? TotalFishWeightKg { get; set; }
        public decimal? AverageWeightGrams { get; set; }
    }

    public class UpdateBiometricssampleDto
    {
        public int BiometricsId { get; set; }
        public int SampleNumber { get; set; }
        public decimal? BucketWaterWeightKg { get; set; }
        public decimal? BucketWithFishWeightKg { get; set; }
        public int FishCount { get; set; }
        public decimal? TotalFishWeightKg { get; set; }
        public decimal? AverageWeightGrams { get; set; }
    }
}
