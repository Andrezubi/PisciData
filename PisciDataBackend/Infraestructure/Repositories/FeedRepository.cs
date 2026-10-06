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

        public override async Task DeleteAsync(Feed entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
