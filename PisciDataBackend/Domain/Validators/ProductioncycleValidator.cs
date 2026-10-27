using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class ProductioncycleValidator
    {
        public static List<string> Validate(CreateProductioncycleDto dto)
        {
            var errors = new List<string>();

            if (dto.PondId <= 0)
                errors.Add("El estanque es obligatorio y debe ser mayor que cero.");

            if (dto.SpeciesId <= 0)
                errors.Add("La especie es obligatoria y debe ser mayor que cero.");

            if (dto.StartDate == default)
                errors.Add("La fecha de inicio es obligatoria.");

            if (dto.EndDate.HasValue && dto.EndDate < dto.StartDate)
                errors.Add("La fecha de finalización debe ser igual o posterior a la fecha de inicio.");

            if (dto.InitialFishCount.HasValue && dto.InitialFishCount < 0)
                errors.Add("La cantidad inicial de peces no puede ser negativa.");

            if (dto.InitialAverageWeightGrams.HasValue && dto.InitialAverageWeightGrams < 0)
                errors.Add("El peso promedio inicial no puede ser negativo.");

            if (dto.InitialAgeDays.HasValue && dto.InitialAgeDays < 0)
                errors.Add("La edad inicial no puede ser negativa.");

            return errors;
        }

        public static List<string> Validate(UpdateProductioncycleDto dto)
        {
            var errors = new List<string>();

            if (dto.PondId <= 0)
                errors.Add("El estanque es obligatorio y debe ser mayor que cero.");

            if (dto.SpeciesId <= 0)
                errors.Add("La especie es obligatoria y debe ser mayor que cero.");

            if (dto.StartDate == default)
                errors.Add("La fecha de inicio es obligatoria.");

            if (dto.EndDate.HasValue && dto.EndDate < dto.StartDate)
                errors.Add("La fecha de finalización debe ser igual o posterior a la fecha de inicio.");

            if (dto.InitialFishCount.HasValue && dto.InitialFishCount < 0)
                errors.Add("La cantidad inicial de peces no puede ser negativa.");

            if (dto.InitialAverageWeightGrams.HasValue && dto.InitialAverageWeightGrams < 0)
                errors.Add("El peso promedio inicial no puede ser negativo.");

            if (dto.InitialAgeDays.HasValue && dto.InitialAgeDays < 0)
                errors.Add("La edad inicial no puede ser negativa.");

            return errors;
        }
    }
}
