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
            return await _dbSet.Where(x => x.IsActive != false).ToListAsync();
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
