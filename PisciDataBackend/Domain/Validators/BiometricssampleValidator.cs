using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class BiometricssampleValidator
    {
        public static List<string> Validate(CreateBiometricssampleDto dto) => ValidateCommon(dto.BiometricsId, dto.SampleNumber, dto.FishCount, dto.BucketWaterWeightKg, dto.BucketWithFishWeightKg, dto.TotalFishWeightKg, dto.AverageWeightGrams);

        public static List<string> Validate(UpdateBiometricssampleDto dto) => ValidateCommon(dto.BiometricsId, dto.SampleNumber, dto.FishCount, dto.BucketWaterWeightKg, dto.BucketWithFishWeightKg, dto.TotalFishWeightKg, dto.AverageWeightGrams);

        private static List<string> ValidateCommon(int biometricsId, int sampleNumber, int fishCount, decimal? bucketWaterWeightKg, decimal? bucketWithFishWeightKg, decimal? totalFishWeightKg, decimal? averageWeightGrams)
        {
            var errors = new List<string>();

            if (biometricsId <= 0)
                errors.Add("BiometricsId is required and must be greater than 0.");

            if (sampleNumber <= 0)
                errors.Add("SampleNumber must be greater than 0.");

            if (fishCount < 0)
                errors.Add("FishCount must be greater than or equal to 0.");

            if (bucketWaterWeightKg.HasValue && bucketWaterWeightKg < 0)
                errors.Add("BucketWaterWeightKg must be greater than or equal to 0.");

            if (bucketWithFishWeightKg.HasValue && bucketWithFishWeightKg < 0)
                errors.Add("BucketWithFishWeightKg must be greater than or equal to 0.");

            if (totalFishWeightKg.HasValue && totalFishWeightKg < 0)
                errors.Add("TotalFishWeightKg must be greater than or equal to 0.");

            if (averageWeightGrams.HasValue && averageWeightGrams < 0)
                errors.Add("AverageWeightGrams must be greater than or equal to 0.");

            return errors;
        }
    }
}
