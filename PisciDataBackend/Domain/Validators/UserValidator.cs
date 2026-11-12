using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class UserValidator
    {
        private static readonly string[] AllowedRoles = { "Admin", "Technician", "Worker" };

        public static List<string> Validate(CreateUserDto dto)
        {
            var errors = new List<string>();

            if (string.IsNullOrWhiteSpace(dto.FirstName))
                errors.Add("FirstName is required.");

            if (string.IsNullOrWhiteSpace(dto.LastName))
                errors.Add("LastName is required.");

            if (string.IsNullOrWhiteSpace(dto.Phone))
                errors.Add("Phone is required.");

            if (string.IsNullOrWhiteSpace(dto.Password) || dto.Password.Length < 6)
                errors.Add("Password is required and must be at least 6 characters long.");

            if (string.IsNullOrWhiteSpace(dto.Role))
                errors.Add("Role is required.");
            else if (!AllowedRoles.Contains(dto.Role))
                errors.Add($"Role must be one of: {string.Join(", ", AllowedRoles)}.");

            return errors;
        }

        public static List<string> Validate(UpdateUserDto dto)
        {
            var errors = new List<string>();

            if (string.IsNullOrWhiteSpace(dto.FirstName))
                errors.Add("FirstName is required.");

            if (string.IsNullOrWhiteSpace(dto.LastName))
                errors.Add("LastName is required.");

            if (string.IsNullOrWhiteSpace(dto.Phone))
                errors.Add("Phone is required.");

            if (string.IsNullOrWhiteSpace(dto.Role))
                errors.Add("Role is required.");
            //else if (!AllowedRoles.Contains(dto.Role))
            //    errors.Add($"Role must be one of: {string.Join(", ", AllowedRoles)}.");

            return errors;
        }

        public static List<string> Validate(UpdateUserProfileDto dto)
        {
            var errors = new List<string>();

            if (string.IsNullOrWhiteSpace(dto.FirstName))
                errors.Add("El nombre es obligatorio.");
            else if (dto.FirstName.Length > 100)
                errors.Add("El nombre no puede superar los 100 caracteres.");

            if (string.IsNullOrWhiteSpace(dto.LastName))
                errors.Add("El apellido es obligatorio.");
            else if (dto.LastName.Length > 100)
                errors.Add("El apellido no puede superar los 100 caracteres.");

            if (string.IsNullOrWhiteSpace(dto.Phone))
                errors.Add("El teléfono es obligatorio.");
            else if (dto.Phone.Length > 30)
                errors.Add("El teléfono no puede superar los 30 caracteres.");

            return errors;
        }
    }
}
