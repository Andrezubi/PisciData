namespace PisciDataBackend.AI
{
    public sealed record AIChatRequest(
        string Message,
        string? ConversationId = null);

    public sealed record AIChatResponse(
        string Reply,
        string? ConversationId = null);
}
