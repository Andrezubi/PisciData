using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class WaterQualityRepository : BaseRepository<Waterquality>
    {
        public WaterQualityRepository(PiscidatadbContext context) : base(context)
        {
        }

        public async Task<List<Waterquality>> GetByFarmOwnerAsync(
            int farmId, int ownerUserId, DateOnly? from, DateOnly? to)
        {
            return await BuildOwnedQuery(ownerUserId)
                .Where(item => item.ProductionCycle.Pond.FarmId == farmId
                    && (!from.HasValue || item.MeasurementDate >= from.Value)
                    && (!to.HasValue || item.MeasurementDate <= to.Value))
                .OrderBy(item => item.MeasurementDate)
                .ThenBy(item => item.MeasurementTime)
                .ThenBy(item => item.Id)
                .ToListAsync();
        }

        public async Task<List<Waterquality>> GetByPondOwnerAsync(
            int pondId, int ownerUserId, DateOnly? from, DateOnly? to)
        {
            return await BuildOwnedQuery(ownerUserId)
                .Where(item => item.ProductionCycle.PondId == pondId
                    && (!from.HasValue || item.MeasurementDate >= from.Value)
                    && (!to.HasValue || item.MeasurementDate <= to.Value))
                .OrderBy(item => item.MeasurementDate)
                .ThenBy(item => item.MeasurementTime)
                .ThenBy(item => item.Id)
                .ToListAsync();
        }

        public async Task<Waterquality?> GetLatestByPondOwnerAsync(int pondId, int ownerUserId)
        {
            return await BuildOwnedQuery(ownerUserId)
                .Where(item => item.ProductionCycle.PondId == pondId)
                .OrderByDescending(item => item.MeasurementDate)
                .ThenByDescending(item => item.MeasurementTime)
                .ThenByDescending(item => item.Id)
                .FirstOrDefaultAsync();
        }

        public async Task<Waterquality?> GetOwnedByIdAsync(int id, int ownerUserId)
        {
            return await BuildOwnedQuery(ownerUserId)
                .FirstOrDefaultAsync(item => item.Id == id);
        }

        public async Task<Waterquality?> GetTrackedOwnedByIdAsync(int id, int ownerUserId)
        {
            return await _context.Waterqualities
                .AsSplitQuery()
                .Where(item => item.Id == id
                    && item.IsActive != false
                    && item.ProductionCycle.IsActive != false
                    && item.ProductionCycle.Pond.IsActive != false
                    && item.ProductionCycle.Pond.Farm.IsActive != false
                    && item.ProductionCycle.Pond.Farm.OwnerUserId == ownerUserId)
                .Include(item => item.ProductionCycle)
                    .ThenInclude(cycle => cycle.Pond)
                    .ThenInclude(pond => pond.Farm)
                .Include(item => item.ProductionCycle)
                    .ThenInclude(cycle => cycle.Species)
                .FirstOrDefaultAsync();
        }

        public async Task<Pond?> GetPondForOwnerAsync(int pondId, int ownerUserId)
        {
            return await _context.Ponds
                .AsNoTracking()
                .Include(pond => pond.Farm)
                .FirstOrDefaultAsync(pond => pond.Id == pondId
                    && pond.IsActive != false
                    && pond.Farm.IsActive != false
                    && pond.Farm.OwnerUserId == ownerUserId);
        }

        public async Task<Productioncycle?> GetActiveCycleForPondOwnerAsync(
            int pondId, int? productionCycleId, int ownerUserId)
        {
            var query = _context.Productioncycles
                .AsNoTracking()
                .Include(cycle => cycle.Pond)
                    .ThenInclude(pond => pond.Farm)
                .Include(cycle => cycle.Species)
                .Where(cycle => cycle.PondId == pondId
                    && cycle.IsActive != false
                    && cycle.Pond.IsActive != false
                    && cycle.Pond.Farm.IsActive != false
                    && cycle.Pond.Farm.OwnerUserId == ownerUserId);

            if (productionCycleId.HasValue)
            {
                return await query.FirstOrDefaultAsync(cycle => cycle.Id == productionCycleId.Value);
            }

            return await query
                .OrderByDescending(cycle => cycle.StartDate)
                .ThenByDescending(cycle => cycle.Id)
                .FirstOrDefaultAsync();
        }

        public async Task<List<Waterqualityreference>> GetActiveReferencesAsync()
        {
            return await _context.Waterqualityreferences
                .AsNoTracking()
                .Where(reference => reference.IsActive != false)
                .OrderBy(reference => reference.Parameter)
                .ThenBy(reference => reference.SpeciesId == null)
                .ThenByDescending(reference => reference.Id)
                .ToListAsync();
        }

        public override async Task DeleteAsync(Waterquality entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }

        private IQueryable<Waterquality> BuildOwnedQuery(int ownerUserId)
        {
            return _context.Waterqualities
                .AsNoTracking()
                .AsSplitQuery()
                .Where(item => item.IsActive != false
                    && item.ProductionCycle.IsActive != false
                    && item.ProductionCycle.Pond.IsActive != false
                    && item.ProductionCycle.Pond.Farm.IsActive != false
                    && item.ProductionCycle.Pond.Farm.OwnerUserId == ownerUserId)
                .Include(item => item.ProductionCycle)
                    .ThenInclude(cycle => cycle.Pond)
                    .ThenInclude(pond => pond.Farm)
                .Include(item => item.ProductionCycle)
                    .ThenInclude(cycle => cycle.Species);
        }
    }
}
