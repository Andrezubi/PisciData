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

        public override async Task DeleteAsync(Productioncycle entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
