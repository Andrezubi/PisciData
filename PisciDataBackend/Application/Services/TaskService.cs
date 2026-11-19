using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Repositories;
using TaskEntity = PisciDataBackend.Domain.Models.Task;

namespace PisciDataBackend.Application.Services
{
    public class TaskService
    {
        private readonly TaskRepository _taskRepository;

        public TaskService(TaskRepository taskRepository)
        {
            _taskRepository = taskRepository;
        }

        public async Task<IEnumerable<TaskDto>> GetAllAsync(int userId)
        {
            var tasks = await _taskRepository.GetByOwnerAsync(userId);
            return tasks.Select(MapToDto);
        }

        public async Task<IEnumerable<TaskDto>?> GetByFarmAsync(int farmId, int userId)
        {
            if (!await _taskRepository.FarmBelongsToOwnerAsync(farmId, userId))
                return null;

            var tasks = await _taskRepository.GetByFarmForOwnerAsync(farmId, userId);
            return tasks.Select(MapToDto);
        }

        public async Task<(TaskDto? Dto, List<string> Errors)> CreateAsync(CreateTaskDto dto, int userId)
        {
            var errors = TaskValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            if (!await _taskRepository.FarmBelongsToOwnerAsync(dto.FarmId, userId))
                return (null, new List<string> { "La granja no existe o no pertenece al usuario." });

            if (dto.PondId.HasValue && !await _taskRepository.PondBelongsToFarmAsync(dto.PondId.Value, dto.FarmId))
                return (null, new List<string> { "El estanque no pertenece a la granja seleccionada." });

            var entity = new TaskEntity
            {
                FarmId = dto.FarmId,
                PondId = dto.PondId,
                Title = dto.Title.Trim(),
                Description = string.IsNullOrWhiteSpace(dto.Description) ? null : dto.Description.Trim(),
                ScheduledDate = dto.ScheduledDate ?? DateTime.UtcNow,
                Priority = NormalizePriority(dto.Priority),
                Completed = false,
                CreatedAt = DateTime.UtcNow,
                IsActive = true,
                UserId = userId
            };

            await _taskRepository.AddAsync(entity);
            var created = await _taskRepository.GetByIdForOwnerAsync(entity.Id, userId);
            return (created == null ? MapToDto(entity) : MapToDto(created), errors);
        }

        public async Task<(TaskDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateTaskDto dto, int userId)
        {
            var errors = TaskValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var entity = await _taskRepository.GetByIdForOwnerAsync(id, userId);
            if (entity == null)
                return (null, errors);

            if (dto.PondId.HasValue && !await _taskRepository.PondBelongsToFarmAsync(dto.PondId.Value, entity.FarmId))
                return (null, new List<string> { "El estanque no pertenece a la granja de la tarea." });

            entity.PondId = dto.PondId;
            entity.Title = dto.Title.Trim();
            entity.Description = string.IsNullOrWhiteSpace(dto.Description) ? null : dto.Description.Trim();
            entity.ScheduledDate = dto.ScheduledDate ?? entity.ScheduledDate;
            entity.Priority = NormalizePriority(dto.Priority);
            entity.Completed = dto.Completed;
            entity.CompletedDate = dto.Completed ? (entity.CompletedDate ?? DateTime.UtcNow) : null;
            entity.UpdatedAt = DateTime.UtcNow;

            await _taskRepository.UpdateAsync(entity);
            var updated = await _taskRepository.GetByIdForOwnerAsync(id, userId);
            return (updated == null ? MapToDto(entity) : MapToDto(updated), errors);
        }

        public async Task<TaskDto?> ToggleAsync(int id, int userId)
        {
            var entity = await _taskRepository.GetByIdForOwnerAsync(id, userId);
            if (entity == null)
                return null;

            entity.Completed = !entity.Completed;
            entity.CompletedDate = entity.Completed ? DateTime.UtcNow : null;
            entity.UpdatedAt = DateTime.UtcNow;

            await _taskRepository.UpdateAsync(entity);
            return MapToDto(entity);
        }

        public async Task<bool> DeleteAsync(int id, int userId)
        {
            var entity = await _taskRepository.GetByIdForOwnerAsync(id, userId);
            if (entity == null)
                return false;

            await _taskRepository.SoftDeleteAsync(entity);
            return true;
        }

        private static string NormalizePriority(string? priority)
        {
            return (priority?.Trim().ToLowerInvariant()) switch
            {
                "alta" or "high" => "Alta",
                "baja" or "low" => "Baja",
                _ => "Normal"
            };
        }

        private static TaskDto MapToDto(TaskEntity t) => new()
        {
            Id = t.Id,
            FarmId = t.FarmId,
            PondId = t.PondId,
            PondName = t.Pond != null ? $"{t.Pond.Code} · {t.Pond.Name}" : "General",
            Title = t.Title,
            Description = t.Description,
            ScheduledDate = t.ScheduledDate,
            CompletedDate = t.CompletedDate,
            Priority = t.Priority,
            Completed = t.Completed,
            CreatedAt = t.CreatedAt
        };
    }
}

