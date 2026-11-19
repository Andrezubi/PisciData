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

        public async Task<IEnumerable<FeedDto>> GetAllAsync(int userId)
        {
            var feeds = await _feedRepository.GetByOwnerAsync(userId);
            return feeds.Select(MapToDto);
        }

        public async Task<FeedDto?> GetByIdAsync(int id, int userId)
        {
            var feed = await _feedRepository.GetByIdForOwnerAsync(id, userId);
            return feed == null ? null : MapToDto(feed);
        }

        public async Task<(FeedDto? Dto, List<string> Errors)> CreateAsync(
            CreateFeedDto dto, int userId)
        {
            var errors = FeedValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            if (!await _feedRepository.FarmBelongsToOwnerAsync(dto.FarmId, userId))
                return (null, new List<string> { "La granja no existe o no pertenece al usuario." });

            var feed = new Feed
            {
                FarmId = dto.FarmId,
                Brand = dto.Brand.Trim(),
                ProductName = dto.ProductName,
                Phase = dto.Phase,
                ProteinPercentage = dto.ProteinPercentage,
                PelletSizeMm = dto.PelletSizeMm,
                StockKg = dto.StockKg,
                MinimumStockKg = dto.MinimumStockKg,
                CreatedAt = DateTime.UtcNow,
                IsActive = true,
                UserId = userId
            };

            await _feedRepository.AddAsync(feed);
            return (MapToDto(feed), errors);
        }

        public async Task<(FeedDto? Dto, List<string> Errors)> UpdateAsync(
            int id, UpdateFeedDto dto, int userId)
        {
            var errors = FeedValidator.Validate(dto);
            if (errors.Count > 0)
                return (null, errors);

            var feed = await _feedRepository.GetByIdForOwnerAsync(id, userId);
            if (feed == null)
                return (null, errors);

            if (!await _feedRepository.FarmBelongsToOwnerAsync(dto.FarmId, userId))
                return (null, new List<string> { "La granja no existe o no pertenece al usuario." });

            feed.FarmId = dto.FarmId;
            feed.Brand = dto.Brand.Trim();
            feed.ProductName = dto.ProductName;
            feed.Phase = dto.Phase;
            feed.ProteinPercentage = dto.ProteinPercentage;
            feed.PelletSizeMm = dto.PelletSizeMm;
            feed.StockKg = dto.StockKg;
            feed.MinimumStockKg = dto.MinimumStockKg;
            feed.UpdatedAt = DateTime.UtcNow;
            feed.UserId = userId;

            await _feedRepository.UpdateAsync(feed);
            return (MapToDto(feed), errors);
        }

        public async Task<bool> DeleteAsync(int id, int userId)
        {
            var feed = await _feedRepository.GetByIdForOwnerAsync(id, userId);
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
                StockStatus = GetStockStatus(feed.StockKg, feed.MinimumStockKg),
                CreatedAt = feed.CreatedAt,
                UpdatedAt = feed.UpdatedAt,
                IsActive = feed.IsActive
            };
        }

        private static string GetStockStatus(decimal? stock, decimal? minimumStock)
        {
            if (!stock.HasValue || !minimumStock.HasValue)
                return "NO DISPONIBLE";

            return stock.Value <= minimumStock.Value ? "BAJO" : "DISPONIBLE";
        }
    }
}
