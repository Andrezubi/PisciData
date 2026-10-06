using Task = System.Threading.Tasks.Task;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Domain.Validators;
using PisciDataBackend.Infraestructure.Persistence;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    public class FeedService
    {
        private readonly FeedRepository _feedRepository;

        public FeedService(FeedRepository feedRepository)
        {
            _feedRepository = feedRepository;
        }

        public async Task<IEnumerable<FeedDto>> GetAllAsync()
        {
            var feeds = await _feedRepository.GetAllAsync();
            return feeds.Select(MapToDto);
        }

        public async Task<FeedDto?> GetByIdAsync(int id)
        {
            var feed = await _feedRepository.GetByIdAsync(id);
            return feed == null ? null : MapToDto(feed);
        }

        public async Task<(FeedDto? Dto, List<string> Errors)> CreateAsync(CreateFeedDto dto)
        {
            var errors = FeedValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var feed = new Feed
            {
                FarmId = dto.FarmId,
                Brand = dto.Brand,
                ProductName = dto.ProductName,
                Phase = dto.Phase,
                ProteinPercentage = dto.ProteinPercentage,
                PelletSizeMm = dto.PelletSizeMm,
                StockKg = dto.StockKg,
                MinimumStockKg = dto.MinimumStockKg,
                CreatedAt = DateTime.UtcNow,
                IsActive = true
            };

            await _feedRepository.AddAsync(feed);
            return (MapToDto(feed), errors);
        }

        public async Task<(FeedDto? Dto, List<string> Errors)> UpdateAsync(int id, UpdateFeedDto dto)
        {
            var errors = FeedValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var feed = await _feedRepository.GetByIdAsync(id);
            if (feed == null)
                return (null, errors);

            feed.FarmId = dto.FarmId;
            feed.Brand = dto.Brand;
            feed.ProductName = dto.ProductName;
            feed.Phase = dto.Phase;
            feed.ProteinPercentage = dto.ProteinPercentage;
            feed.PelletSizeMm = dto.PelletSizeMm;
            feed.StockKg = dto.StockKg;
            feed.MinimumStockKg = dto.MinimumStockKg;
            feed.UpdatedAt = DateTime.UtcNow;

            await _feedRepository.UpdateAsync(feed);
            return (MapToDto(feed), errors);
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var feed = await _feedRepository.GetByIdAsync(id);
            if (feed == null)
                return false;

            await _feedRepository.DeleteAsync(feed);
            return true;
        }

        private static FeedDto MapToDto(Feed feed)
        {
            return new FeedDto
            {
                Id = feed.Id,
                FarmId = feed.FarmId,
                Brand = feed.Brand,
                ProductName = feed.ProductName,
                Phase = feed.Phase,
                ProteinPercentage = feed.ProteinPercentage,
                PelletSizeMm = feed.PelletSizeMm,
                StockKg = feed.StockKg,
                MinimumStockKg = feed.MinimumStockKg,
                CreatedAt = feed.CreatedAt,
                UpdatedAt = feed.UpdatedAt,
                IsActive = feed.IsActive
            };
        }
    }
}
