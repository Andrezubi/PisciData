using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class WaterQualityValidator
    {
        public static List<string> ValidateCreate(CreateWaterQualityDto dto)
        {
            var errors = new List<string>();

            if (dto.PondId <= 0)
                errors.Add("El estanque es obligatorio.");

            if (dto.MeasurementDate == default)
                errors.Add("La fecha de medición es obligatoria.");
            else if (dto.MeasurementDate > DateOnly.FromDateTime(DateTime.UtcNow))
                errors.Add("La fecha de medición no puede ser futura.");

            ValidateValues(dto.TransparencyCm, dto.TemperatureC, dto.Ph, dto.DissolvedOxygen, dto.Observations, errors);
            return errors;
        }

        public static List<string> ValidateUpdate(UpdateWaterQualityDto dto)
        {
            var errors = new List<string>();

            if (dto.MeasurementDate == default)
                errors.Add("La fecha de medición es obligatoria.");
            else if (dto.MeasurementDate > DateOnly.FromDateTime(DateTime.UtcNow))
                errors.Add("La fecha de medición no puede ser futura.");

            ValidateValues(dto.TransparencyCm, dto.TemperatureC, dto.Ph, dto.DissolvedOxygen, dto.Observations, errors);
            return errors;
        }

        private static void ValidateValues(
            decimal? transparencyCm,
            decimal? temperatureC,
            decimal? ph,
            decimal? dissolvedOxygen,
            string? observations,
            List<string> errors)
        {
            if (HasNegativeValue(transparencyCm))
                errors.Add("La transparencia no puede ser negativa.");
            if (HasNegativeValue(temperatureC))
                errors.Add("La temperatura no puede ser negativa.");
            if (HasNegativeValue(ph))
                errors.Add("El pH no puede ser negativo.");
            if (HasNegativeValue(dissolvedOxygen))
                errors.Add("El oxígeno disuelto no puede ser negativo.");

            if (!transparencyCm.HasValue
                && !temperatureC.HasValue
                && !ph.HasValue
                && !dissolvedOxygen.HasValue)
            {
                errors.Add("Debe registrar al menos un parámetro de calidad del agua.");
            }

            if (observations?.Length > 1000)
                errors.Add("Las observaciones no pueden superar los 1000 caracteres.");
        }

        private static bool HasNegativeValue(decimal? value)
        {
            return value.HasValue && value.Value < 0;
        }
    }
}
