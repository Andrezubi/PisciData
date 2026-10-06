using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class ProductioncycleValidator
    {
        public static List<string> Validate(CreateProductioncycleDto dto)
        {
            var errors = new List<string>();

            if (dto.PondId <= 0)
                errors.Add("PondId is required and must be greater than 0.");

            if (dto.SpeciesId <= 0)
                errors.Add("SpeciesId is required and must be greater than 0.");

            if (dto.StartDate == default)
                errors.Add("StartDate is required.");

            if (dto.EndDate.HasValue && dto.EndDate < dto.StartDate)
                errors.Add("EndDate must be greater than or equal to StartDate.");

            if (dto.InitialFishCount.HasValue && dto.InitialFishCount < 0)
                errors.Add("InitialFishCount must be greater than or equal to 0.");

            if (dto.InitialAverageWeightGrams.HasValue && dto.InitialAverageWeightGrams < 0)
                errors.Add("InitialAverageWeightGrams must be greater than or equal to 0.");

            if (dto.InitialAgeDays.HasValue && dto.InitialAgeDays < 0)
                errors.Add("InitialAgeDays must be greater than or equal to 0.");

            return errors;
        }

        public static List<string> Validate(UpdateProductioncycleDto dto)
        {
            var errors = new List<string>();

            if (dto.PondId <= 0)
                errors.Add("PondId is required and must be greater than 0.");

            if (dto.SpeciesId <= 0)
                errors.Add("SpeciesId is required and must be greater than 0.");

            if (dto.StartDate == default)
                errors.Add("StartDate is required.");

            if (dto.EndDate.HasValue && dto.EndDate < dto.StartDate)
                errors.Add("EndDate must be greater than or equal to StartDate.");

            if (dto.InitialFishCount.HasValue && dto.InitialFishCount < 0)
                errors.Add("InitialFishCount must be greater than or equal to 0.");

            if (dto.InitialAverageWeightGrams.HasValue && dto.InitialAverageWeightGrams < 0)
                errors.Add("InitialAverageWeightGrams must be greater than or equal to 0.");

            if (dto.InitialAgeDays.HasValue && dto.InitialAgeDays < 0)
                errors.Add("InitialAgeDays must be greater than or equal to 0.");

            return errors;
        }
    }
}
