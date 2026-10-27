namespace PisciDataBackend.Application.DTOs
{
    // datos de una conversación para mostrar en la lista de chats del usuario
    public class AiconversationDto
    {
        public int Id { get; set; }
        public int OwnerUserId { get; set; }
        public string? Title { get; set; }
        public DateTime StartDate { get; set; }
        public DateTime? LastMessageDate { get; set; }
        public DateTime CreatedAt { get; set; }
    }

    //datos requeridos para crear una nueva conversación vacía
    public class CreateAiconversationDto
    {
        public string? Title { get; set; }
    }

    // datos de un mensaje individual (usuario o ia) para pintar en el chat
    public class AimessageDto
    {
        public int Id { get; set; }
        public int AiconversationId { get; set; }
        public string Role { get; set; } = null!; // "user" o "assistant"
        public string? Content { get; set; }
        public DateTime MessageDate { get; set; }
    }

    // texto que escribe y envía el usuario desde la app
    public class SendAiMessageDto
    {
        public string Content { get; set; } = null!;
    }


    // estructura que se envía en el cuerpo del post hacia ollama /api/chat
    public class OllamaChatRequest
    {
        public string Model { get; set; } = null!;
        public List<OllamaMessageItem> Messages { get; set; } = new();
        public bool Stream { get; set; } = false;
    }

    // mensaje individual dentro del historial que se envía o recibe de ollama
    public class OllamaMessageItem
    {
        public string Role { get; set; } = null!; // "user" o "assistant"
        public string Content { get; set; } = null!;
        public string? Thinking { get; set; }
    }

    // respuesta final que devuelve ollama con el mensaje generado por el modelo
    public class OllamaChatResponse
    {
        public string? Model { get; set; }
        public OllamaMessageItem? Message { get; set; }
        public bool Done { get; set; }
    }
}
