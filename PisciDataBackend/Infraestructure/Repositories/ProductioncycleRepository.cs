using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class ProductioncycleRepository : BaseRepository<Productioncycle>
    {
        public ProductioncycleRepository(PiscidatadbContext context) : base(context)
        {
        }

        public override async Task<IEnumerable<Productioncycle>> GetAllAsync()
        {
            return await _dbSet.Where(pc => pc.IsActive != false).ToListAsync();
        }

    public async Task<List<Productioncycle>> GetByFarmOwnerAsync(int farmId, int ownerUserId)
    {
        return await _dbSet
            .AsNoTracking()
            .AsSplitQuery()
            .Where(pc => pc.IsActive != false
                && pc.Pond.FarmId == farmId
                && pc.Pond.Farm.OwnerUserId == ownerUserId
                && pc.Pond.Farm.IsActive != false
                && pc.Pond.IsActive != false)
            .Include(pc => pc.Pond)
                .ThenInclude(pond => pond.Farm)
            .Include(pc => pc.Species)
            .Include(pc => pc.Harvests)
            .Include(pc => pc.Mortalities)
            .Include(pc => pc.Biometrics)
            .ToListAsync();
    }

    public async Task<Productioncycle?> GetOwnedByIdAsync(int id, int ownerUserId)
    {
        return await _dbSet
            .AsSplitQuery()
            .Where(pc => pc.Id == id
                && pc.IsActive != false
                && pc.Pond.Farm.OwnerUserId == ownerUserId
                && pc.Pond.Farm.IsActive != false
                && pc.Pond.IsActive != false)
            .Include(pc => pc.Pond)
                .ThenInclude(pond => pond.Farm)
            .Include(pc => pc.Species)
            .Include(pc => pc.Harvests)
            .Include(pc => pc.Mortalities)
            .Include(pc => pc.Biometrics)
            .FirstOrDefaultAsync();
    }

    public async Task<bool> PondBelongsToOwnerAsync(int pondId, int ownerUserId)
    {
        return await _context.Ponds.AnyAsync(pond => pond.Id == pondId
            && pond.IsActive != false
            && pond.Farm.IsActive != false
            && pond.Farm.OwnerUserId == ownerUserId);
    }

    public async Task<bool> SpeciesExistsAsync(int speciesId)
    {
        return await _context.Species.AnyAsync(species => species.Id == speciesId && species.IsActive != false);
    }

    public async Task<List<Feedingbyage>> GetActiveFeedingByAgeAsync()
    {
        return await _context.Feedingbyages
            .AsNoTracking()
            .Where(item => item.IsActive != false && item.ApproximatedFishWeightGrams.HasValue)
            .OrderBy(item => item.SpeciesId == null)
            .ThenBy(item => item.MaximumAgeDays - item.MinimumAgeDays)
            .ThenBy(item => item.MinimumAgeDays)
            .ThenBy(item => item.Id)
            .ToListAsync();
    }

        public override async Task DeleteAsync(Productioncycle entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
