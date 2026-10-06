using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class BiometricRepository : BaseRepository<Biometric>
    {
        public BiometricRepository(PiscidatadbContext context) : base(context)
        {
        }

        public override async Task<IEnumerable<Biometric>> GetAllAsync()
        {
            return await _dbSet.Where(x => x.IsActive != false).ToListAsync();
        }

        public override async Task DeleteAsync(Biometric entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
