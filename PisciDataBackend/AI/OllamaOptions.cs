namespace PisciDataBackend.AI
{
    public sealed class OllamaOptions
    {
        public string BaseUrl { get; set; } = "http://localhost:11434/";

        public string Model { get; set; } = "piscidata-ai";

        public int MaxToolIterations { get; set; } = 5;

        // Si se configura por variable de entorno (ej. Groq en Railway), usa el endpoint OpenAI-compatible
        public string? ApiKey { get; set; }
    }
}
