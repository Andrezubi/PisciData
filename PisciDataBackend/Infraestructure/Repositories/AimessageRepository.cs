using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Persistence;

namespace PisciDataBackend.Infraestructure.Repositories
{
    // repositorio para gestionar los mensajes individuales del chat
    public class AimessageRepository : BaseRepository<Aimessage>
    {
        public AimessageRepository(PiscidatadbContext context) : base(context)
        {
        }

        // obtiene todos los mensajes activos de una conversacion ordenados cronologicamente
        public async Task<IEnumerable<Aimessage>> GetMessagesByConversationIdAsync(int conversationId)
        {
            return await _dbSet
                .Where(m => m.AiconversationId == conversationId && m.IsActive != false)
                .OrderBy(m => m.MessageDate)
                .ToListAsync();
        }
    }
}
