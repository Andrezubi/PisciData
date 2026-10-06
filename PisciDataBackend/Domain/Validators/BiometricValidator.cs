using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class BiometricValidator
    {
        public static List<string> Validate(CreateBiometricDto dto) => ValidateCommon(dto.ProductionCycleId, dto.MeasurementDate, dto.AverageWeightGrams, dto.BiomassKg, dto.CalculatedFeedKg);

        public static List<string> Validate(UpdateBiometricDto dto) => ValidateCommon(dto.ProductionCycleId, dto.MeasurementDate, dto.AverageWeightGrams, dto.BiomassKg, dto.CalculatedFeedKg);

        private static List<string> ValidateCommon(int productionCycleId, DateOnly measurementDate, decimal? averageWeightGrams, decimal? biomassKg, decimal? calculatedFeedKg)
        {
            var errors = new List<string>();

            if (productionCycleId <= 0)
                errors.Add("ProductionCycleId is required and must be greater than 0.");

            if (measurementDate == default)
                errors.Add("MeasurementDate is required.");

            if (averageWeightGrams.HasValue && averageWeightGrams < 0)
                errors.Add("AverageWeightGrams must be greater than or equal to 0.");

            if (biomassKg.HasValue && biomassKg < 0)
                errors.Add("BiomassKg must be greater than or equal to 0.");

            if (calculatedFeedKg.HasValue && calculatedFeedKg < 0)
                errors.Add("CalculatedFeedKg must be greater than or equal to 0.");

            return errors;
        }
    }
}
