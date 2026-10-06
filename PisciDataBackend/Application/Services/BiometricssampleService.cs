using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class BiometricssampleService
    {
        private readonly BiometricssampleRepository _biometricssampleRepository;

        public BiometricssampleService(BiometricssampleRepository biometricssampleRepository)
        {
            _biometricssampleRepository = biometricssampleRepository;
        }

        public async Task<IEnumerable<BiometricssampleDto>> GetAllAsync()
        {
            var samples = await _biometricssampleRepository.GetAllAsync();
            return samples.Select(MapToDto);
        }

        public async Task<BiometricssampleDto?> GetByIdAsync(int id)
        {
            var sample = await _biometricssampleRepository.GetByIdAsync(id);
            return sample == null ? null : MapToDto(sample);
        }

        public async Task<(BiometricssampleDto? Dto, List<string> Errors)> CreateAsync(CreateBiometricssampleDto dto)
        {
            var errors = BiometricssampleValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var sample = new Biometricssample
            {
                BiometricsId = dto.BiometricsId,
                SampleNumber = dto.SampleNumber,
                BucketWaterWeightKg = dto.BucketWaterWeightKg,
                BucketWithFishWeightKg = dto.BucketWithFishWeightKg,
                FishCount = dto.FishCount,
                TotalFishWeightKg = dto.TotalFishWeightKg,
                AverageWeightGrams = dto.AverageWeightGrams,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _biometricssampleRepository.AddAsync(sample);
            return (MapToDto(sample), errors);
        }

        public async Task<(BiometricssampleDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateBiometricssampleDto dto)
        {
            var errors = BiometricssampleValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var sample = await _biometricssampleRepository.GetByIdAsync(id);
            if (sample == null)
                return (null, errors);

            sample.BiometricsId = dto.BiometricsId;
            sample.SampleNumber = dto.SampleNumber;
            sample.BucketWaterWeightKg = dto.BucketWaterWeightKg;
            sample.BucketWithFishWeightKg = dto.BucketWithFishWeightKg;
            sample.FishCount = dto.FishCount;
            sample.TotalFishWeightKg = dto.TotalFishWeightKg;
            sample.AverageWeightGrams = dto.AverageWeightGrams;
            sample.UpdatedAt = DateTime.UtcNow;

            await _biometricssampleRepository.UpdateAsync(sample);
            return (MapToDto(sample), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var sample = await _biometricssampleRepository.GetByIdAsync(id);
            if (sample == null)
                return false;

            await _biometricssampleRepository.DeleteAsync(sample);
            return true;
        }

        private static BiometricssampleDto MapToDto(Biometricssample sample)
        {
            return new BiometricssampleDto
            {
                Id = sample.Id,
                BiometricsId = sample.BiometricsId,
                SampleNumber = sample.SampleNumber,
                BucketWaterWeightKg = sample.BucketWaterWeightKg,
                BucketWithFishWeightKg = sample.BucketWithFishWeightKg,
                FishCount = sample.FishCount,
                TotalFishWeightKg = sample.TotalFishWeightKg,
                AverageWeightGrams = sample.AverageWeightGrams,
                CreatedAt = sample.CreatedAt,
                UpdatedAt = sample.UpdatedAt,
                IsActive = sample.IsActive
            };
        }
    }
}
