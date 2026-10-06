using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class FeedingValidator
    {
        public static List<string> Validate(CreateFeedingDto dto) => ValidateCommon(dto.ProductionCycleId, dto.FeedId, dto.FeedingDate, dto.QuantityKg, dto.MealNumber);

        public static List<string> Validate(UpdateFeedingDto dto) => ValidateCommon(dto.ProductionCycleId, dto.FeedId, dto.FeedingDate, dto.QuantityKg, dto.MealNumber);

        private static List<string> ValidateCommon(int productionCycleId, int feedId, DateOnly feedingDate, decimal quantityKg, int? mealNumber)
        {
            var errors = new List<string>();

            if (productionCycleId <= 0)
                errors.Add("ProductionCycleId is required and must be greater than 0.");

            if (feedId <= 0)
                errors.Add("FeedId is required and must be greater than 0.");

            if (feedingDate == default)
                errors.Add("FeedingDate is required.");

            if (quantityKg <= 0)
                errors.Add("QuantityKg must be greater than 0.");

            if (mealNumber.HasValue && mealNumber <= 0)
                errors.Add("MealNumber must be greater than 0.");

            return errors;
        }
    }
}
