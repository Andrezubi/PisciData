using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class FeedRepository : BaseRepository<Feed>
    {
        public FeedRepository(PiscidatadbContext context) : base(context)
        {
        }

        public override async Task<IEnumerable<Feed>> GetAllAsync()
        {
            return await _dbSet.Where(x => x.IsActive != false).ToListAsync();
        }

        public async Task<List<Feed>> GetByOwnerAsync(int userId)
        {
            return await _dbSet
                .AsNoTracking()
                .Where(feed => feed.IsActive != false
                    && feed.Farm.IsActive != false
                    && feed.Farm.OwnerUserId == userId)
                .ToListAsync();
        }

        public async Task<Feed?> GetByIdForOwnerAsync(int id, int userId)
        {
            return await _dbSet
                .Where(feed => feed.Id == id
                    && feed.IsActive != false
                    && feed.Farm.IsActive != false
                    && feed.Farm.OwnerUserId == userId)
                .FirstOrDefaultAsync();
        }

        public async Task<bool> FarmBelongsToOwnerAsync(int farmId, int userId)
        {
            return await _context.Farms.AnyAsync(farm => farm.Id == farmId
                && farm.IsActive != false
                && farm.OwnerUserId == userId);
        }

        public override async Task DeleteAsync(Feed entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
