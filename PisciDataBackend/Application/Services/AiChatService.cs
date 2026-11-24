using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Constants;
using PisciDataBackend.Domain.Models;
using PisciDataBackend.Infraestructure.Clients;
using PisciDataBackend.Infraestructure.Repositories;

namespace PisciDataBackend.Application.Services
{
    // orquesta los casos de uso del chat entre mysql y el cliente de ollama
    public class AiChatService
    {
        private readonly AiconversationRepository _conversationRepo;
        private readonly AimessageRepository _messageRepo;
        private readonly OllamaApiClient _ollamaClient;
        private const int MaxHistoryMessages = 20;

        private static string BuildSystemPrompt()
        {
            return $"Eres PisciData, el asistente inteligente y operador para la gestión de piscigranjas y acuicultura. " +
                   $"Fecha actual del sistema: {DateTime.UtcNow:yyyy-MM-dd}. " +
                   $"REGLAS FUNDAMENTALES OBLIGATORIAS:\n" +
                   $"1. DIRECTO AL GRANO: Responde únicamente y con precisión a lo que el usuario pregunta. Sé muy conciso, directo y breve. No incluyas saludos largos, introducciones innecesarias, obviedades, rodeos ni despedidas de relleno. Si te piden un dato, entrégalo de inmediato.\n" +
                   $"2. DATOS FRESCOS EN TIEMPO REAL: SIEMPRE que el usuario consulte o mencione recursos del sistema (granjas, estanques, ciclos de producción, peces, alimentación, biometrías, insumos o alimentos), DEBES invocar la herramienta MCP correspondiente para obtener la información real y actualizada. NUNCA respondas con listas o datos de mensajes anteriores del chat ni inventes registros.\n" +
                   $"3. SUGERENCIAS DE ALIMENTACIÓN / COMIDA: ESTÁ TERMINANTEMENTE PROHIBIDO inventar o calcular raciones, cantidades en kg, número de tomas o porcentajes de proteína por tu cuenta. Para cualquier sugerencia o recomendación de comida, DEBES usar OBLIGATORIAMENTE la herramienta `GetFeedingRecommendation`. Si el usuario menciona un estanque, primero obtén su ciclo productivo activo con `GetProductionCycles` o `GetPonds` para identificar el `cycleId` y luego invoca `GetFeedingRecommendation(cycleId)`. Muestra exactamente los datos arrojados por el sistema; si no hay ciclo o datos disponibles, dilo con sencillez en vez de inventar.\n" +
                   $"4. FORMATO MARKDOWN PERMITIDO: ESTÁ ESTRICTAMENTE PROHIBIDO usar encabezados de nivel 1 (`#`) y nivel 2 (`##`). SÍ está permitido y se recomienda usar encabezados de nivel 3 (`###`), texto en negrita (`**`), viñetas (-) y tablas Markdown (`| Columna | ... |`) cuando faciliten la visualización compacta y ordenada de los datos.\n" +
                   $"5. IDIOMA: Responde siempre en español, de forma clara, directa y profesional.";
        }

        public AiChatService(
            AiconversationRepository conversationRepo,
            AimessageRepository messageRepo,
            OllamaApiClient ollamaClient)
        {
            _conversationRepo = conversationRepo;
            _messageRepo = messageRepo;
            _ollamaClient = ollamaClient;
        }

        // listar conversaciones del usuario autenticado
        public async Task<IEnumerable<AiconversationDto>> GetUserConversationsAsync(int userId)
        {
            var chats = await _conversationRepo.GetByOwnerUserIdAsync(userId);
            return chats.Select(c => new AiconversationDto
            {
                Id = c.Id,
                OwnerUserId = c.OwnerUserId,
                Title = c.Title,
                StartDate = c.StartDate,
                LastMessageDate = c.LastMessageDate,
                CreatedAt = c.CreatedAt
            });
        }

        // crear una conversacion vacia
        public async Task<AiconversationDto> CreateConversationAsync(int userId, CreateAiconversationDto dto)
        {
            var chat = new Aiconversation
            {
                OwnerUserId = userId,
                Title = string.IsNullOrWhiteSpace(dto.Title) ? "Nueva conversación" : dto.Title.Trim(),
                StartDate = DateTime.UtcNow,
                IsActive = true
            };
            await _conversationRepo.AddAsync(chat);

            return new AiconversationDto
            {
                Id = chat.Id,
                OwnerUserId = chat.OwnerUserId,
                Title = chat.Title,
                StartDate = chat.StartDate,
                CreatedAt = chat.CreatedAt
            };
        }

        // obtener historial de mensajes de un chat
        public async Task<IEnumerable<AimessageDto>?> GetMessagesAsync(int conversationId, int userId)
        {
            var chat = await _conversationRepo.GetByIdAndUserAsync(conversationId, userId);
            if (chat == null) return null;

            var messages = await _messageRepo.GetMessagesByConversationIdAsync(conversationId);
            return messages.Select(m => new AimessageDto
            {
                Id = m.Id,
                AiconversationId = m.AiconversationId,
                Role = m.Role,
                Content = m.Content,
                MessageDate = m.MessageDate
            });
        }

        // envia mensaje, consulta ollama via cliente tipado y guarda respuesta
        public async Task<(AimessageDto? Dto, string? Error)> SendMessageAsync(
    int conversationId,
    int userId,
    SendAiMessageDto dto,
    string authorizationHeader,
    CancellationToken cancellationToken = default)
        {
            var chat = await _conversationRepo.GetByIdAndUserAsync(conversationId, userId);
            if (chat == null)
                return (null, "Conversación no encontrada.");

            if (string.IsNullOrWhiteSpace(dto.Content))
                return (null, "El mensaje no puede estar vacío.");

            var content = dto.Content.Trim();

            // 1. armar historial reciente para ollama desde mysql (solo lectura)
            var history = await _messageRepo.GetMessagesByConversationIdAsync(conversationId);

            var ollamaMessages = new List<OllamaMessageItem>
            {
                new OllamaMessageItem
                {
                    Role = MessageRoles.System,
                    Content = BuildSystemPrompt()
                }
            };

            ollamaMessages.AddRange(
                history
                    .TakeLast(MaxHistoryMessages)
                    .Select(m => new OllamaMessageItem
                    {
                        Role = m.Role,
                        Content = m.Content ?? string.Empty
                    })
            );

            // agregar el nuevo mensaje del usuario al payload en memoria
            ollamaMessages.Add(new OllamaMessageItem
            {
                Role = MessageRoles.User,
                Content = content
            });

            // 2. consultar a ollama a traves de su cliente tipado ANTES de tocar la base de datos

            var (assistantContent, ollamaError) =await _ollamaClient.ChatAsync(ollamaMessages,authorizationHeader,cancellationToken);
            if (ollamaError != null)
                return (null, ollamaError); // costo cero en db: si ollama falla, no se escribe ningun registro

            // 3. solo si ollama respondio con exito, persistimos ambos mensajes en mysql
            var now = DateTime.UtcNow;

            var userMsg = new Aimessage
            {
                AiconversationId = conversationId,
                Role = MessageRoles.User,
                Content = content,
                MessageType = MessageTypes.Text,
                MessageDate = now,
                IsActive = true,
                UserId = userId
            };
            await _messageRepo.AddAsync(userMsg);

            var aiMsg = new Aimessage
            {
                AiconversationId = conversationId,
                Role = MessageRoles.Assistant,
                Content = assistantContent!,
                MessageType = MessageTypes.Text,
                MessageDate = now,
                IsActive = true,
                UserId = userId
            };
            await _messageRepo.AddAsync(aiMsg);

            chat.LastMessageDate = now;
            await _conversationRepo.UpdateAsync(chat);

            return (new AimessageDto
            {
                Id = aiMsg.Id,
                AiconversationId = conversationId,
                Role = aiMsg.Role,
                Content = aiMsg.Content,
                MessageDate = aiMsg.MessageDate
            }, null);
        }
    }
}
