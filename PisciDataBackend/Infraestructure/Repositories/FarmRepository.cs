using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class FarmRepository : BaseRepository<Farm>
    {
        public FarmRepository(PiscidatadbContext context) : base(context)
        {
        }

        public override async Task<IEnumerable<Farm>> GetAllAsync()
        {
            return await _dbSet.Where(f => f.IsActive != false).ToListAsync();
        }

        public async Task<IEnumerable<Farm>> GetFarmsByOwnerUserIdAsync(int userId)
        {
            return await _dbSet
                .Where(f => f.IsActive != false && f.OwnerUserId == userId)
                .ToListAsync();
        }

        public override async Task DeleteAsync(Farm entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
