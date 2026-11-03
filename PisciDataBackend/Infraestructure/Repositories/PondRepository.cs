using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class PondRepository : BaseRepository<Pond>
    {
        public PondRepository(PiscidatadbContext context) : base(context)
        {
        }

        public override async Task<IEnumerable<Pond>> GetAllAsync()
        {
            return await _dbSet.Where(p => p.IsActive != false).ToListAsync();
        }

        public async Task<List<Pond>> GetByOwnerAsync(int userId)
        {
            return await _dbSet
                .AsNoTracking()
                .AsSplitQuery()
                .Where(pond => pond.IsActive != false
                    && pond.Farm.IsActive != false
                    && pond.Farm.OwnerUserId == userId)
                .Include(pond => pond.Farm)
                .Include(pond => pond.Productioncycles
                    .Where(cycle => cycle.IsActive != false))
                    .ThenInclude(cycle => cycle.Species)
                .Include(pond => pond.Productioncycles
                    .Where(cycle => cycle.IsActive != false))
                    .ThenInclude(cycle => cycle.Waterqualities
                        .Where(quality => quality.IsActive != false))
                .ToListAsync();
        }

        public async Task<Pond?> GetByIdForOwnerAsync(int id, int userId)
        {
            return await _dbSet
                .AsNoTracking()
                .AsSplitQuery()
                .Where(pond => pond.Id == id
                    && pond.IsActive != false
                    && pond.Farm.IsActive != false
                    && pond.Farm.OwnerUserId == userId)
                .Include(pond => pond.Farm)
                .Include(pond => pond.Productioncycles
                    .Where(cycle => cycle.IsActive != false))
                    .ThenInclude(cycle => cycle.Species)
                .Include(pond => pond.Productioncycles
                    .Where(cycle => cycle.IsActive != false))
                    .ThenInclude(cycle => cycle.Waterqualities
                        .Where(quality => quality.IsActive != false))
                .FirstOrDefaultAsync();
        }

        public async Task<bool> FarmBelongsToOwnerAsync(int farmId, int userId)
        {
            return await _context.Farms.AnyAsync(farm => farm.Id == farmId
                && farm.IsActive != false
                && farm.OwnerUserId == userId);
        }

        public async Task<List<Waterqualityreference>> GetActiveWaterQualityReferencesAsync()
        {
            return await _context.Waterqualityreferences
                .AsNoTracking()
                .Where(reference => reference.IsActive != false)
                .OrderBy(reference => reference.Parameter)
                .ThenBy(reference => reference.SpeciesId == null)
                .ThenByDescending(reference => reference.Id)
                .ToListAsync();
        }

        public override async Task DeleteAsync(Pond entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
