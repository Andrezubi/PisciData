using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;
using TaskEntity = PisciDataBackend.Domain.Models.Task;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class TaskRepository : BaseRepository<TaskEntity>
    {
        public TaskRepository(PiscidatadbContext context) : base(context) { }

        public async Task<IEnumerable<TaskEntity>> GetByOwnerAsync(int userId)
        {
            return await _dbSet
                .Include(t => t.Pond)
                .Include(t => t.Farm)
                .Where(t => (t.IsActive == null || t.IsActive == true) && t.Farm.OwnerUserId == userId)
                .OrderBy(t => t.Completed)
                .ThenBy(t => t.ScheduledDate ?? DateTime.MaxValue)
                .ThenByDescending(t => t.CreatedAt)
                .ToListAsync();
        }

        public async Task<IEnumerable<TaskEntity>> GetByFarmForOwnerAsync(int farmId, int userId)
        {
            return await _dbSet
                .Include(t => t.Pond)
                .Include(t => t.Farm)
                .Where(t => (t.IsActive == null || t.IsActive == true)
                    && t.FarmId == farmId
                    && t.Farm.OwnerUserId == userId)
                .OrderBy(t => t.Completed)
                .ThenBy(t => t.ScheduledDate ?? DateTime.MaxValue)
                .ThenByDescending(t => t.CreatedAt)
                .ToListAsync();
        }

        public async Task<TaskEntity?> GetByIdForOwnerAsync(int id, int userId)
        {
            return await _dbSet
                .Include(t => t.Pond)
                .Include(t => t.Farm)
                .FirstOrDefaultAsync(t => t.Id == id
                    && (t.IsActive == null || t.IsActive == true)
                    && t.Farm.OwnerUserId == userId);
        }

        public async Task<bool> FarmBelongsToOwnerAsync(int farmId, int userId)
        {
            return await _context.Farms.AnyAsync(f => f.Id == farmId && f.OwnerUserId == userId && f.IsActive == true);
        }

        public async Task<bool> PondBelongsToFarmAsync(int pondId, int farmId)
        {
            return await _context.Ponds.AnyAsync(p => p.Id == pondId && p.FarmId == farmId && (p.IsActive == null || p.IsActive == true));
        }

        public async System.Threading.Tasks.Task SoftDeleteAsync(TaskEntity entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
