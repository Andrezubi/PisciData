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

        public async Task<IEnumerable<SupplyDto>> GetAllAsync(int userId)
        {
            var supplies = await _supplyRepository.GetByOwnerAsync(userId);
            return supplies.Select(MapToDto);
        }

        public async Task<SupplyDto?> GetByIdAsync(int id, int userId)
        {
            var supply = await _supplyRepository.GetByIdForOwnerAsync(id, userId);
            return supply == null ? null : MapToDto(supply);
        }

        public async Task<(SupplyDto? Dto, List<string> Errors)> CreateAsync(
            CreateSupplyDto dto, int userId)
        {
            var errors = SupplyValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            if (!await _supplyRepository.FarmBelongsToOwnerAsync(dto.FarmId, userId))
                return (null, new List<string> { "La granja no existe o no pertenece al usuario." });

            var supply = new Supply
            {
                FarmId = dto.FarmId,
                Name = dto.Name.Trim(),
                Category = dto.Category,
                Quantity = dto.Quantity,
                Unit = dto.Unit.Trim(),
                MinimumStock = dto.MinimumStock,
                CreatedAt = DateTime.UtcNow,
                IsActive = true,
                UserId = userId
            };

            await _supplyRepository.AddAsync(supply);
            return (MapToDto(supply), errors);
        }

        public async Task<(SupplyDto? Dto, List<string> Errors)> UpdateAsync(
            int id, UpdateSupplyDto dto, int userId)
        {
            var errors = SupplyValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var supply = await _supplyRepository.GetByIdForOwnerAsync(id, userId);
            if (supply == null)
                return (null, errors);

            if (!await _supplyRepository.FarmBelongsToOwnerAsync(dto.FarmId, userId))
                return (null, new List<string> { "La granja no existe o no pertenece al usuario." });

            supply.FarmId = dto.FarmId;
            supply.Name = dto.Name.Trim();
            supply.Category = dto.Category;
            supply.Quantity = dto.Quantity;
            supply.Unit = dto.Unit.Trim();
            supply.MinimumStock = dto.MinimumStock;
            supply.UpdatedAt = DateTime.UtcNow;
            supply.UserId = userId;

            await _supplyRepository.UpdateAsync(supply);
            return (MapToDto(supply), errors);
        }

        public async Task<bool> DeleteAsync(int id, int userId)
        {
            var supply = await _supplyRepository.GetByIdForOwnerAsync(id, userId);
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
                StockStatus = GetStockStatus(supply.Quantity, supply.MinimumStock),
                CreatedAt = supply.CreatedAt,
                UpdatedAt = supply.UpdatedAt,
                IsActive = supply.IsActive
            };
        }

        private static string GetStockStatus(decimal quantity, decimal? minimumStock)
        {
            if (!minimumStock.HasValue)
                return "NO DISPONIBLE";

            return quantity <= minimumStock.Value ? "BAJO" : "DISPONIBLE";
        }
    }
}
