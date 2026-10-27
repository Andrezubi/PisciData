using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;
using Task = System.Threading.Tasks.Task;

namespace PisciDataBackend.Infraestructure.Repositories
{
    // repositorio para gestionar las conversaciones de ia en mysql
    public class AiconversationRepository : BaseRepository<Aiconversation>
    {
        public AiconversationRepository(PiscidatadbContext context) : base(context)
        {
        }

        // obtiene solo las conversaciones activas del usuario autenticado
        public async Task<IEnumerable<Aiconversation>> GetByOwnerUserIdAsync(int userId)
        {
            return await _dbSet
                .Where(c => c.IsActive != false && c.OwnerUserId == userId)
                .OrderByDescending(c => c.LastMessageDate ?? c.CreatedAt)
                .ToListAsync();
        }

        // obtiene una conversacion por su id verificando que le pertenezca al usuario
        public async Task<Aiconversation?> GetByIdAndUserAsync(int id, int userId)
        {
            return await _dbSet
                .FirstOrDefaultAsync(c => c.Id == id && c.OwnerUserId == userId && c.IsActive != false);
        }

        // eliminacion logica para no borrar fisicamente de la base de datos
        public override async Task DeleteAsync(Aiconversation entity)
        {
            entity.IsActive = false;
            entity.UpdatedAt = DateTime.UtcNow;
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }
    }
}
