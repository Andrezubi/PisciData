using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class PondValidator
    {
        public static List<string> Validate(CreatePondDto dto)
        {
            var errors = new List<string>();

            if (dto.FarmId <= 0)
                errors.Add("FarmId is required and must be greater than 0.");

            if (string.IsNullOrWhiteSpace(dto.Code))
                errors.Add("Code is required.");
            else if (dto.Code.Length > 50)
                errors.Add("El código no puede superar los 50 caracteres.");

            if (dto.Name?.Length > 100)
                errors.Add("El nombre no puede superar los 100 caracteres.");

            if (dto.Shape?.Length > 50)
                errors.Add("La forma no puede superar los 50 caracteres.");

            if (dto.Description?.Length > 500)
                errors.Add("La descripción no puede superar los 500 caracteres.");

            if (dto.Width.HasValue && dto.Width < 0)
                errors.Add("Width must be greater than or equal to 0.");

            if (dto.Length.HasValue && dto.Length < 0)
                errors.Add("Length must be greater than or equal to 0.");

            if (dto.Diameter.HasValue && dto.Diameter < 0)
                errors.Add("Diameter must be greater than or equal to 0.");

            if (dto.Depth.HasValue && dto.Depth < 0)
                errors.Add("Depth must be greater than or equal to 0.");

            if (dto.Area.HasValue && dto.Area < 0)
                errors.Add("Area must be greater than or equal to 0.");

            return errors;
        }

        public static List<string> Validate(UpdatePondDto dto)
        {
            var errors = new List<string>();

            if (dto.FarmId <= 0)
                errors.Add("FarmId is required and must be greater than 0.");

            if (string.IsNullOrWhiteSpace(dto.Code))
                errors.Add("Code is required.");
            else if (dto.Code.Length > 50)
                errors.Add("El código no puede superar los 50 caracteres.");

            if (dto.Name?.Length > 100)
                errors.Add("El nombre no puede superar los 100 caracteres.");

            if (dto.Shape?.Length > 50)
                errors.Add("La forma no puede superar los 50 caracteres.");

            if (dto.Description?.Length > 500)
                errors.Add("La descripción no puede superar los 500 caracteres.");

            if (dto.Width.HasValue && dto.Width < 0)
                errors.Add("Width must be greater than or equal to 0.");

            if (dto.Length.HasValue && dto.Length < 0)
                errors.Add("Length must be greater than or equal to 0.");

            if (dto.Diameter.HasValue && dto.Diameter < 0)
                errors.Add("Diameter must be greater than or equal to 0.");

            if (dto.Depth.HasValue && dto.Depth < 0)
                errors.Add("Depth must be greater than or equal to 0.");

            if (dto.Area.HasValue && dto.Area < 0)
                errors.Add("Area must be greater than or equal to 0.");

            return errors;
        }
    }
}
