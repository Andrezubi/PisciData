using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;
using System.Security.Claims;

namespace PisciDataBackend.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class AiConversationsController : ControllerBase
    {
        private readonly AiChatService _chatService;

        public AiConversationsController(AiChatService chatService)
        {
            _chatService = chatService;
        }

        // obtiene el id del usuario desde el jwt
        private int? GetCurrentUserId()
        {
            var claim = User.FindFirst(ClaimTypes.NameIdentifier) ?? User.FindFirst("UserId") ?? User.FindFirst("sub");
            if (claim != null && int.TryParse(claim.Value, out int userId))
                return userId;

            return null;
        }

        // GET: api/AiConversations (lista las conversaciones del usuario)
        [HttpGet]
        public async Task<ActionResult<IEnumerable<AiconversationDto>>> GetMyConversations()
        {
            var userId = GetCurrentUserId();
            if (userId == null) return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var chats = await _chatService.GetUserConversationsAsync(userId.Value);
            return Ok(chats);
        }

        // POST: api/AiConversations (crea una conversacion nueva)
        [HttpPost]
        public async Task<ActionResult<AiconversationDto>> CreateConversation([FromBody] CreateAiconversationDto dto)
        {
            var userId = GetCurrentUserId();
            if (userId == null) return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var created = await _chatService.CreateConversationAsync(userId.Value, dto);
            return CreatedAtAction(nameof(GetMyConversations), new { id = created.Id }, created);
        }

        // GET: api/AiConversations/{id}/messages (obtiene el historial del chat)
        [HttpGet("{id}/messages")]
        public async Task<ActionResult<IEnumerable<AimessageDto>>> GetMessages(int id)
        {
            var userId = GetCurrentUserId();
            if (userId == null) return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var messages = await _chatService.GetMessagesAsync(id, userId.Value);
            if (messages == null) return NotFound(new { message = "Conversación no encontrada o no pertenece al usuario." });

            return Ok(messages);
        }

        // POST: api/AiConversations/{id}/messages (envia un mensaje y obtiene respuesta de la ia)
        [HttpPost("{id}/messages")]
        public async Task<ActionResult<AimessageDto>> SendMessage(int id, [FromBody] SendAiMessageDto dto, CancellationToken cancellationToken)
        {
            var userId = GetCurrentUserId();
            if (userId == null) return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (responseMsg, error) = await _chatService.SendMessageAsync(id, userId.Value, dto, cancellationToken);
            if (error != null)
            {
                if (error == "Conversación no encontrada.")
                    return NotFound(new { message = error });

                if (error == "El mensaje no puede estar vacío.")
                    return BadRequest(new { message = error });

                return StatusCode(502, new { message = error });
            }

            return Ok(responseMsg);
        }
    }
}
