using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class FarmValidator
    {
        public static List<string> Validate(CreateFarmDto dto)
        {
            var errors = new List<string>();

            if (dto.OwnerUserId <= 0)
                errors.Add("OwnerUserId is required and must be greater than 0.");

            if (string.IsNullOrWhiteSpace(dto.Name))
                errors.Add("Name is required.");
            else if (dto.Name.Length > 150)
                errors.Add("Name must not exceed 150 characters.");

            if (dto.Latitude.HasValue && (dto.Latitude < -90 || dto.Latitude > 90))
                errors.Add("Latitude must be between -90 and 90.");

            if (dto.Longitude.HasValue && (dto.Longitude < -180 || dto.Longitude > 180))
                errors.Add("Longitude must be between -180 and 180.");

            return errors;
        }

        public static List<string> Validate(UpdateFarmDto dto)
        {
            var errors = new List<string>();

            if (dto.OwnerUserId <= 0)
                errors.Add("OwnerUserId is required and must be greater than 0.");

            if (string.IsNullOrWhiteSpace(dto.Name))
                errors.Add("Name is required.");
            else if (dto.Name.Length > 150)
                errors.Add("Name must not exceed 150 characters.");

            if (dto.Latitude.HasValue && (dto.Latitude < -90 || dto.Latitude > 90))
                errors.Add("Latitude must be between -90 and 90.");

            if (dto.Longitude.HasValue && (dto.Longitude < -180 || dto.Longitude > 180))
                errors.Add("Longitude must be between -180 and 180.");

            return errors;
        }
    }
}
