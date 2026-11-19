using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class SupplyValidator
    {
        public static List<string> Validate(CreateSupplyDto dto) => ValidateCommon(dto.FarmId, dto.Name, dto.Category, dto.Quantity, dto.Unit, dto.MinimumStock);

        public static List<string> Validate(UpdateSupplyDto dto) => ValidateCommon(dto.FarmId, dto.Name, dto.Category, dto.Quantity, dto.Unit, dto.MinimumStock);

        private static List<string> ValidateCommon(int farmId, string name, string? category, decimal quantity, string unit, decimal? minimumStock)
        {
            var errors = new List<string>();

            if (farmId <= 0)
                errors.Add("FarmId is required and must be greater than 0.");

            if (string.IsNullOrWhiteSpace(name))
                errors.Add("Name is required.");
            else if (name.Length > 150)
                errors.Add("El nombre no puede superar los 150 caracteres.");

            if (category?.Length > 100)
                errors.Add("La categoría no puede superar los 100 caracteres.");

            if (quantity < 0)
                errors.Add("Quantity must be greater than or equal to 0.");

            if (string.IsNullOrWhiteSpace(unit))
                errors.Add("Unit is required.");
            else if (unit.Length > 30)
                errors.Add("La unidad no puede superar los 30 caracteres.");

            if (minimumStock.HasValue && minimumStock < 0)
                errors.Add("MinimumStock must be greater than or equal to 0.");

            return errors;
        }
    }
}
