using Task = System.Threading.Tasks.Task;
using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    public class UserRepository : BaseRepository<User>
    {
        public UserRepository(PiscidatadbContext context) : base(context)
        {
        }

        public async Task<User?> GetByPhoneAsync(string phone)
        {
            return await _dbSet.FirstOrDefaultAsync(u => u.Phone == phone && u.IsActive != false);
        }

        public override async Task<IEnumerable<User>> GetAllAsync()
        {
            return await _dbSet.Where(u => u.IsActive != false).ToListAsync();
        }

        public override async Task<User?> GetByIdAsync(int id)
        {
            return await _dbSet.FirstOrDefaultAsync(u => u.Id == id && u.IsActive != false);
        }

        public override async Task DeleteAsync(User entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
