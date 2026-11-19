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
        private readonly IWhisperService _whisperService;

        public AiConversationsController(AiChatService chatService, IWhisperService whisperService)
        {
            _chatService = chatService;
            _whisperService = whisperService;
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

            if (userId == null)
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var authorizationHeader = Request.Headers.Authorization.ToString();

            if (string.IsNullOrWhiteSpace(authorizationHeader))
                return Unauthorized(new { message = "No se encontró el token de autenticación." });

            var (responseMsg, error) = await _chatService.SendMessageAsync(
                id,
                userId.Value,
                dto,
                authorizationHeader,
                cancellationToken);

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

        // POST: api/AiConversations/transcribe (transcribe audio en español usando whisper.cpp)
        [HttpPost("transcribe")]
        [RequestSizeLimit(30 * 1024 * 1024)]
        public async Task<ActionResult<TranscriptionResponseDto>> Transcribe(
            [FromQuery] string? language = "es",
            CancellationToken cancellationToken = default)
        {
            var userId = GetCurrentUserId();
            if (userId == null) return Unauthorized(new { message = "No se pudo identificar al usuario." });

            Stream? audioStream = null;
            MemoryStream? memoryStream = null;

            if (Request.HasFormContentType && Request.Form.Files.Count > 0)
            {
                var file = Request.Form.Files[0];
                memoryStream = new MemoryStream();
                await file.CopyToAsync(memoryStream, cancellationToken);
                memoryStream.Position = 0;
                audioStream = memoryStream;
            }
            else if (Request.Body != null)
            {
                memoryStream = new MemoryStream();
                await Request.Body.CopyToAsync(memoryStream, cancellationToken);
                memoryStream.Position = 0;
                if (memoryStream.Length > 0)
                {
                    audioStream = memoryStream;
                }
            }

            if (audioStream == null || audioStream.Length == 0)
            {
                return BadRequest(new { message = "No se recibió archivo o flujo de audio válido." });
            }

            try
            {
                var targetLanguage = string.IsNullOrWhiteSpace(language) ? "es" : language;
                var text = await _whisperService.TranscribeAsync(audioStream, targetLanguage, cancellationToken);
                return Ok(new TranscriptionResponseDto
                {
                    Text = text,
                    Language = targetLanguage
                });
            }
            catch (FileNotFoundException ex)
            {
                return StatusCode(500, new { message = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = $"Error en transcripción Whisper: {ex.Message}" });
            }
            finally
            {
                memoryStream?.Dispose();
            }
        }
    }
}
