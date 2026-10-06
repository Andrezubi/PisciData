using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class PondService
    {
        private readonly PondRepository _pondRepository;

        public PondService(PondRepository pondRepository)
        {
            _pondRepository = pondRepository;
        }

        public async Task<IEnumerable<PondDto>> GetAllAsync()
        {
            var ponds = await _pondRepository.GetAllAsync();
            return ponds.Select(MapToDto);
        }

        public async Task<PondDto?> GetByIdAsync(int id)
        {
            var pond = await _pondRepository.GetByIdAsync(id);
            return pond == null ? null : MapToDto(pond);
        }

        public async Task<(PondDto? Dto, List<string> Errors)> CreateAsync(CreatePondDto dto)
        {
            var errors = PondValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var pond = new Pond
            {
                FarmId = dto.FarmId,
                Code = dto.Code,
                Name = dto.Name,
                Shape = dto.Shape,
                Width = dto.Width,
                Length = dto.Length,
                Diameter = dto.Diameter,
                Depth = dto.Depth,
                Area = dto.Area,
                Description = dto.Description,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _pondRepository.AddAsync(pond);
            return (MapToDto(pond), errors);
        }

        public async Task<(PondDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdatePondDto dto)
        {
            var errors = PondValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var pond = await _pondRepository.GetByIdAsync(id);
            if (pond == null)
                return (null, errors);

            pond.FarmId = dto.FarmId;
            pond.Code = dto.Code;
            pond.Name = dto.Name;
            pond.Shape = dto.Shape;
            pond.Width = dto.Width;
            pond.Length = dto.Length;
            pond.Diameter = dto.Diameter;
            pond.Depth = dto.Depth;
            pond.Area = dto.Area;
            pond.Description = dto.Description;
            pond.UpdatedAt = DateTime.UtcNow;

            await _pondRepository.UpdateAsync(pond);
            return (MapToDto(pond), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var pond = await _pondRepository.GetByIdAsync(id);
            if (pond == null)
                return false;

            await _pondRepository.DeleteAsync(pond);
            return true;
        }

        private static PondDto MapToDto(Pond pond)
        {
            return new PondDto
            {
                Id = pond.Id,
                FarmId = pond.FarmId,
                Code = pond.Code,
                Name = pond.Name,
                Shape = pond.Shape,
                Width = pond.Width,
                Length = pond.Length,
                Diameter = pond.Diameter,
                Depth = pond.Depth,
                Area = pond.Area,
                Description = pond.Description,
                CreatedAt = pond.CreatedAt,
                UpdatedAt = pond.UpdatedAt,
                IsActive = pond.IsActive
            };
        }
    }
}
