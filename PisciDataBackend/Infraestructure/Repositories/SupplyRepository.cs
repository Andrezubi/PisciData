using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class SupplyRepository : BaseRepository<Supply>
    {
        public SupplyRepository(PiscidatadbContext context) : base(context)
        {
        }

        public override async Task<IEnumerable<Supply>> GetAllAsync()
        {
            return await _dbSet.Where(x => x.IsActive != false).ToListAsync();
        }

        public async Task<List<Supply>> GetByOwnerAsync(int userId)
        {
            return await _dbSet
                .AsNoTracking()
                .Where(supply => supply.IsActive != false
                    && supply.Farm.IsActive != false
                    && supply.Farm.OwnerUserId == userId)
                .ToListAsync();
        }

        public async Task<Supply?> GetByIdForOwnerAsync(int id, int userId)
        {
            return await _dbSet
                .Where(supply => supply.Id == id
                    && supply.IsActive != false
                    && supply.Farm.IsActive != false
                    && supply.Farm.OwnerUserId == userId)
                .FirstOrDefaultAsync();
        }

        public async Task<bool> FarmBelongsToOwnerAsync(int farmId, int userId)
        {
            return await _context.Farms.AnyAsync(farm => farm.Id == farmId
                && farm.IsActive != false
                && farm.OwnerUserId == userId);
        }

        public override async Task DeleteAsync(Supply entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
