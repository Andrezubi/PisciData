using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class SupplyService
    {
        private readonly SupplyRepository _supplyRepository;

        public SupplyService(SupplyRepository supplyRepository)
        {
            _supplyRepository = supplyRepository;
        }

        public async Task<IEnumerable<SupplyDto>> GetAllAsync()
        {
            var supplies = await _supplyRepository.GetAllAsync();
            return supplies.Select(MapToDto);
        }

        public async Task<SupplyDto?> GetByIdAsync(int id)
        {
            var supply = await _supplyRepository.GetByIdAsync(id);
            return supply == null ? null : MapToDto(supply);
        }

        public async Task<(SupplyDto? Dto, List<string> Errors)> CreateAsync(CreateSupplyDto dto)
        {
            var errors = SupplyValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var supply = new Supply
            {
                FarmId = dto.FarmId,
                Name = dto.Name,
                Category = dto.Category,
                Quantity = dto.Quantity,
                Unit = dto.Unit,
                MinimumStock = dto.MinimumStock,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _supplyRepository.AddAsync(supply);
            return (MapToDto(supply), errors);
        }

        public async Task<(SupplyDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateSupplyDto dto)
        {
            var errors = SupplyValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var supply = await _supplyRepository.GetByIdAsync(id);
            if (supply == null)
                return (null, errors);

            supply.FarmId = dto.FarmId;
            supply.Name = dto.Name;
            supply.Category = dto.Category;
            supply.Quantity = dto.Quantity;
            supply.Unit = dto.Unit;
            supply.MinimumStock = dto.MinimumStock;
            supply.UpdatedAt = DateTime.UtcNow;

            await _supplyRepository.UpdateAsync(supply);
            return (MapToDto(supply), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var supply = await _supplyRepository.GetByIdAsync(id);
            if (supply == null)
                return false;

            await _supplyRepository.DeleteAsync(supply);
            return true;
        }

        private static SupplyDto MapToDto(Supply supply)
        {
            return new SupplyDto
            {
                Id = supply.Id,
                FarmId = supply.FarmId,
                Name = supply.Name,
                Category = supply.Category,
                Quantity = supply.Quantity,
                Unit = supply.Unit,
                MinimumStock = supply.MinimumStock,
                CreatedAt = supply.CreatedAt,
                UpdatedAt = supply.UpdatedAt,
                IsActive = supply.IsActive
            };
        }
    }
}
