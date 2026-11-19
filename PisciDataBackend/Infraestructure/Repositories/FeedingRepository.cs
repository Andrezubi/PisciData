using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class FeedingRepository : BaseRepository<Feeding>
    {
        public FeedingRepository(PiscidatadbContext context) : base(context)
        {
        }

        public override async Task<IEnumerable<Feeding>> GetAllAsync()
        {
            return await _dbSet
                .Include(x => x.Feed)
                .Where(x => x.IsActive != false)
                .OrderByDescending(x => x.FeedingDate)
                .ThenByDescending(x => x.FeedingTime)
                .ToListAsync();
        }

        public async Task<List<Feeding>> GetByProductionCycleIdAsync(int cycleId)
        {
            return await _dbSet
                .AsNoTracking()
                .Include(x => x.Feed)
                .Where(x => x.ProductionCycleId == cycleId && x.IsActive != false)
                .OrderByDescending(x => x.FeedingDate)
                .ThenByDescending(x => x.FeedingTime)
                .ToListAsync();
        }

        public async Task<Feeding?> GetByIdWithFeedAsync(int id)
        {
            return await _dbSet
                .Include(x => x.Feed)
                .FirstOrDefaultAsync(x => x.Id == id && x.IsActive != false);
        }

        public async Task<int> GetMealCountForDateAsync(int cycleId, DateOnly date)
        {
            return await _dbSet
                .CountAsync(x => x.ProductionCycleId == cycleId 
                    && x.FeedingDate == date 
                    && x.IsActive != false);
        }

        public async Task<Productioncycle?> GetCycleForRecommendationAsync(int cycleId)
        {
            return await _context.Productioncycles
                .AsNoTracking()
                .AsSplitQuery()
                .Include(pc => pc.Pond)
                    .ThenInclude(p => p.Farm)
                .Include(pc => pc.Species)
                .Include(pc => pc.Biometrics)
                .Include(pc => pc.Mortalities)
                .Include(pc => pc.Harvests)
                .FirstOrDefaultAsync(pc => pc.Id == cycleId && pc.IsActive != false);
        }

        public async Task<List<Feedingbyweight>> GetFeedingByWeightListAsync(int? speciesId)
        {
            return await _context.Feedingbyweights
                .AsNoTracking()
                .Where(x => x.IsActive != false && (x.SpeciesId == speciesId || x.SpeciesId == null))
                .OrderBy(x => x.SpeciesId == null)
                .ThenBy(x => x.MinimumWeightGrams)
                .ToListAsync();
        }

        public async Task<List<Feedingbyage>> GetFeedingByAgeListAsync(int? speciesId)
        {
            return await _context.Feedingbyages
                .AsNoTracking()
                .Where(x => x.IsActive != false && (x.SpeciesId == speciesId || x.SpeciesId == null))
                .OrderBy(x => x.SpeciesId == null)
                .ThenBy(x => x.MinimumAgeDays)
                .ToListAsync();
        }

        public override async Task DeleteAsync(Feeding entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
