using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class BiometricService
    {
        private readonly BiometricRepository _biometricRepository;

        public BiometricService(BiometricRepository biometricRepository)
        {
            _biometricRepository = biometricRepository;
        }

        public async Task<IEnumerable<BiometricDto>> GetAllAsync()
        {
            var biometrics = await _biometricRepository.GetAllAsync();
            return biometrics.Select(MapToDto);
        }

        public async Task<BiometricDto?> GetByIdAsync(int id)
        {
            var biometric = await _biometricRepository.GetByIdAsync(id);
            return biometric == null ? null : MapToDto(biometric);
        }

        public async Task<(BiometricDto? Dto, List<string> Errors)> CreateAsync(CreateBiometricDto dto)
        {
            var errors = BiometricValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var biometric = new Biometric
            {
                ProductionCycleId = dto.ProductionCycleId,
                MeasurementDate = dto.MeasurementDate,
                AverageWeightGrams = dto.AverageWeightGrams,
                BiomassKg = dto.BiomassKg,
                CalculatedFeedKg = dto.CalculatedFeedKg,
                HealthStatus = dto.HealthStatus,
                Observations = dto.Observations,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _biometricRepository.AddAsync(biometric);
            return (MapToDto(biometric), errors);
        }

        public async Task<(BiometricDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateBiometricDto dto)
        {
            var errors = BiometricValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var biometric = await _biometricRepository.GetByIdAsync(id);
            if (biometric == null)
                return (null, errors);

            biometric.ProductionCycleId = dto.ProductionCycleId;
            biometric.MeasurementDate = dto.MeasurementDate;
            biometric.AverageWeightGrams = dto.AverageWeightGrams;
            biometric.BiomassKg = dto.BiomassKg;
            biometric.CalculatedFeedKg = dto.CalculatedFeedKg;
            biometric.HealthStatus = dto.HealthStatus;
            biometric.Observations = dto.Observations;
            biometric.UpdatedAt = DateTime.UtcNow;

            await _biometricRepository.UpdateAsync(biometric);
            return (MapToDto(biometric), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var biometric = await _biometricRepository.GetByIdAsync(id);
            if (biometric == null)
                return false;

            await _biometricRepository.DeleteAsync(biometric);
            return true;
        }

        private static BiometricDto MapToDto(Biometric biometric)
        {
            return new BiometricDto
            {
                Id = biometric.Id,
                ProductionCycleId = biometric.ProductionCycleId,
                MeasurementDate = biometric.MeasurementDate,
                AverageWeightGrams = biometric.AverageWeightGrams,
                BiomassKg = biometric.BiomassKg,
                CalculatedFeedKg = biometric.CalculatedFeedKg,
                HealthStatus = biometric.HealthStatus,
                Observations = biometric.Observations,
                CreatedAt = biometric.CreatedAt,
                UpdatedAt = biometric.UpdatedAt,
                IsActive = biometric.IsActive
            };
        }
    }
}
