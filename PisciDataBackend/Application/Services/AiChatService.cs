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

        private const string SystemPrompt =
            "Eres el asistente inteligente de PisciData, un sistema para la gestión de piscigranjas y acuicultura. " +
            "Responde siempre en español, de forma breve, clara y profesional. " +
            "Ayudas al productor con buenas prácticas de crianza, alimentación y cuidado de sus estanques.";

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
                    Content = SystemPrompt
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
            var (assistantContent, ollamaError) = await _ollamaClient.ChatAsync(ollamaMessages, cancellationToken);
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
