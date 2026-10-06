using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class ProductioncycleService
    {
        private readonly ProductioncycleRepository _productioncycleRepository;

        public ProductioncycleService(ProductioncycleRepository productioncycleRepository)
        {
            _productioncycleRepository = productioncycleRepository;
        }

        public async Task<IEnumerable<ProductioncycleDto>> GetAllAsync()
        {
            var productioncycles = await _productioncycleRepository.GetAllAsync();
            return productioncycles.Select(MapToDto);
        }

        public async Task<ProductioncycleDto?> GetByIdAsync(int id)
        {
            var productioncycle = await _productioncycleRepository.GetByIdAsync(id);
            return productioncycle == null ? null : MapToDto(productioncycle);
        }

        public async Task<(ProductioncycleDto? Dto, List<string> Errors)> CreateAsync(CreateProductioncycleDto dto)
        {
            var errors = ProductioncycleValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var productioncycle = new Productioncycle
            {
                PondId = dto.PondId,
                SpeciesId = dto.SpeciesId,
                StartDate = dto.StartDate,
                EndDate = dto.EndDate,
                InitialFishCount = dto.InitialFishCount,
                InitialAverageWeightGrams = dto.InitialAverageWeightGrams,
                InitialAgeDays = dto.InitialAgeDays,
                Observations = dto.Observations,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _productioncycleRepository.AddAsync(productioncycle);
            return (MapToDto(productioncycle), errors);
        }

        public async Task<(ProductioncycleDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateProductioncycleDto dto)
        {
            var errors = ProductioncycleValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var productioncycle = await _productioncycleRepository.GetByIdAsync(id);
            if (productioncycle == null)
                return (null, errors);

            productioncycle.PondId = dto.PondId;
            productioncycle.SpeciesId = dto.SpeciesId;
            productioncycle.StartDate = dto.StartDate;
            productioncycle.EndDate = dto.EndDate;
            productioncycle.InitialFishCount = dto.InitialFishCount;
            productioncycle.InitialAverageWeightGrams = dto.InitialAverageWeightGrams;
            productioncycle.InitialAgeDays = dto.InitialAgeDays;
            productioncycle.Observations = dto.Observations;
            productioncycle.UpdatedAt = DateTime.UtcNow;

            await _productioncycleRepository.UpdateAsync(productioncycle);
            return (MapToDto(productioncycle), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var productioncycle = await _productioncycleRepository.GetByIdAsync(id);
            if (productioncycle == null)
                return false;

            await _productioncycleRepository.DeleteAsync(productioncycle);
            return true;
        }

        private static ProductioncycleDto MapToDto(Productioncycle productioncycle)
        {
            return new ProductioncycleDto
            {
                Id = productioncycle.Id,
                PondId = productioncycle.PondId,
                SpeciesId = productioncycle.SpeciesId,
                StartDate = productioncycle.StartDate,
                EndDate = productioncycle.EndDate,
                InitialFishCount = productioncycle.InitialFishCount,
                InitialAverageWeightGrams = productioncycle.InitialAverageWeightGrams,
                InitialAgeDays = productioncycle.InitialAgeDays,
                Observations = productioncycle.Observations,
                CreatedAt = productioncycle.CreatedAt,
                UpdatedAt = productioncycle.UpdatedAt,
                IsActive = productioncycle.IsActive
            };
        }
    }
}
