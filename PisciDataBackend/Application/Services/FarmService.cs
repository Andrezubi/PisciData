using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class FarmService
    {
        private readonly FarmRepository _farmRepository;

        public FarmService(FarmRepository farmRepository)
        {
            _farmRepository = farmRepository;
        }

        public async Task<IEnumerable<FarmDto>> GetAllAsync()
        {
            var farms = await _farmRepository.GetAllAsync();
            return farms.Select(MapToDto);
        }

        public async Task<FarmDto?> GetByIdAsync(int id)
        {
            var farm = await _farmRepository.GetByIdAsync(id);
            return farm == null ? null : MapToDto(farm);
        }

        public async Task<(FarmDto? Dto, List<string> Errors)> CreateAsync(CreateFarmDto dto)
        {
            var errors = FarmValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var farm = new Farm
            {
                OwnerUserId = dto.OwnerUserId,
                Name = dto.Name,
                Description = dto.Description,
                Address = dto.Address,
                Latitude = dto.Latitude,
                Longitude = dto.Longitude,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _farmRepository.AddAsync(farm);
            return (MapToDto(farm), errors);
        }

        public async Task<(FarmDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateFarmDto dto)
        {
            var errors = FarmValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var farm = await _farmRepository.GetByIdAsync(id);
            if (farm == null)
                return (null, errors);

            farm.OwnerUserId = dto.OwnerUserId;
            farm.Name = dto.Name;
            farm.Description = dto.Description;
            farm.Address = dto.Address;
            farm.Latitude = dto.Latitude;
            farm.Longitude = dto.Longitude;
            farm.UpdatedAt = DateTime.UtcNow;

            await _farmRepository.UpdateAsync(farm);
            return (MapToDto(farm), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var farm = await _farmRepository.GetByIdAsync(id);
            if (farm == null)
                return false;

            await _farmRepository.DeleteAsync(farm);
            return true;
        }

        private static FarmDto MapToDto(Farm farm)
        {
            return new FarmDto
            {
                Id = farm.Id,
                OwnerUserId = farm.OwnerUserId,
                Name = farm.Name,
                Description = farm.Description,
                Address = farm.Address,
                Latitude = farm.Latitude,
                Longitude = farm.Longitude,
                CreatedAt = farm.CreatedAt,
                UpdatedAt = farm.UpdatedAt,
                IsActive = farm.IsActive
            };
        }
    }
}
