using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Domain.Validators
{
    public static class TaskValidator
    {
        private static readonly HashSet<string> ValidPriorities = new(StringComparer.OrdinalIgnoreCase)
        {
            "Alta", "High", "Normal", "Media", "Medium", "Baja", "Low"
        };

        public static List<string> Validate(CreateTaskDto dto)
        {
            var errors = new List<string>();
            if (dto.FarmId <= 0)
                errors.Add("La granja es obligatoria.");
            if (string.IsNullOrWhiteSpace(dto.Title))
                errors.Add("El título de la tarea es obligatorio.");
            else if (dto.Title.Trim().Length > 200)
                errors.Add("El título no puede superar los 200 caracteres.");
            if (!string.IsNullOrWhiteSpace(dto.Priority) && !ValidPriorities.Contains(dto.Priority.Trim()))
                errors.Add("Prioridad no válida (use Alta, Normal o Baja).");
            return errors;
        }

        public static List<string> Validate(UpdateTaskDto dto)
        {
            var errors = new List<string>();
            if (string.IsNullOrWhiteSpace(dto.Title))
                errors.Add("El título de la tarea es obligatorio.");
            else if (dto.Title.Trim().Length > 200)
                errors.Add("El título no puede superar los 200 caracteres.");
            if (!string.IsNullOrWhiteSpace(dto.Priority) && !ValidPriorities.Contains(dto.Priority.Trim()))
                errors.Add("Prioridad no válida (use Alta, Normal o Baja).");
            return errors;
        }
    }
}

