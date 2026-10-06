using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class FeedingService
    {
        private readonly FeedingRepository _feedingRepository;

        public FeedingService(FeedingRepository feedingRepository)
        {
            _feedingRepository = feedingRepository;
        }

        public async Task<IEnumerable<FeedingDto>> GetAllAsync()
        {
            var feedings = await _feedingRepository.GetAllAsync();
            return feedings.Select(MapToDto);
        }

        public async Task<FeedingDto?> GetByIdAsync(int id)
        {
            var feeding = await _feedingRepository.GetByIdAsync(id);
            return feeding == null ? null : MapToDto(feeding);
        }

        public async Task<(FeedingDto? Dto, List<string> Errors)> CreateAsync(CreateFeedingDto dto)
        {
            var errors = FeedingValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var feeding = new Feeding
            {
                ProductionCycleId = dto.ProductionCycleId,
                FeedId = dto.FeedId,
                FeedingDate = dto.FeedingDate,
                FeedingTime = dto.FeedingTime,
                QuantityKg = dto.QuantityKg,
                MealNumber = dto.MealNumber,
                Behavior = dto.Behavior,
                Observations = dto.Observations,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _feedingRepository.AddAsync(feeding);
            return (MapToDto(feeding), errors);
        }

        public async Task<(FeedingDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateFeedingDto dto)
        {
            var errors = FeedingValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var feeding = await _feedingRepository.GetByIdAsync(id);
            if (feeding == null)
                return (null, errors);

            feeding.ProductionCycleId = dto.ProductionCycleId;
            feeding.FeedId = dto.FeedId;
            feeding.FeedingDate = dto.FeedingDate;
            feeding.FeedingTime = dto.FeedingTime;
            feeding.QuantityKg = dto.QuantityKg;
            feeding.MealNumber = dto.MealNumber;
            feeding.Behavior = dto.Behavior;
            feeding.Observations = dto.Observations;
            feeding.UpdatedAt = DateTime.UtcNow;

            await _feedingRepository.UpdateAsync(feeding);
            return (MapToDto(feeding), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var feeding = await _feedingRepository.GetByIdAsync(id);
            if (feeding == null)
                return false;

            await _feedingRepository.DeleteAsync(feeding);
            return true;
        }

        private static FeedingDto MapToDto(Feeding feeding)
        {
            return new FeedingDto
            {
                Id = feeding.Id,
                ProductionCycleId = feeding.ProductionCycleId,
                FeedId = feeding.FeedId,
                FeedingDate = feeding.FeedingDate,
                FeedingTime = feeding.FeedingTime,
                QuantityKg = feeding.QuantityKg,
                MealNumber = feeding.MealNumber,
                Behavior = feeding.Behavior,
                Observations = feeding.Observations,
                CreatedAt = feeding.CreatedAt,
                UpdatedAt = feeding.UpdatedAt,
                IsActive = feeding.IsActive
            };
        }
    }
}
