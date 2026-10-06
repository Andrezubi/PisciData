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
            else if (!AllowedRoles.Contains(dto.Role))
                errors.Add($"Role must be one of: {string.Join(", ", AllowedRoles)}.");

            return errors;
        }
    }
}
