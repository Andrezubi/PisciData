using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class SupplyValidator
    {
        public static List<string> Validate(CreateSupplyDto dto) => ValidateCommon(dto.FarmId, dto.Name, dto.Quantity, dto.Unit, dto.MinimumStock);

        public static List<string> Validate(UpdateSupplyDto dto) => ValidateCommon(dto.FarmId, dto.Name, dto.Quantity, dto.Unit, dto.MinimumStock);

        private static List<string> ValidateCommon(int farmId, string name, decimal quantity, string unit, decimal? minimumStock)
        {
            var errors = new List<string>();

            if (farmId <= 0)
                errors.Add("FarmId is required and must be greater than 0.");

            if (string.IsNullOrWhiteSpace(name))
                errors.Add("Name is required.");

            if (quantity < 0)
                errors.Add("Quantity must be greater than or equal to 0.");

            if (string.IsNullOrWhiteSpace(unit))
                errors.Add("Unit is required.");

            if (minimumStock.HasValue && minimumStock < 0)
                errors.Add("MinimumStock must be greater than or equal to 0.");

            return errors;
        }
    }
}
