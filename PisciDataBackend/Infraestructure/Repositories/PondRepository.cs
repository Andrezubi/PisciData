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

        public override async Task DeleteAsync(Pond entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
