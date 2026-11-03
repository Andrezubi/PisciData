namespace PisciDataBackend.AI
{
    public sealed class OllamaOptions
    {
        public string BaseUrl { get; set; } = "http://localhost:11434/";

        public string Model { get; set; } = "piscidata-ai";

        public int MaxToolIterations { get; set; } = 5;
    }
}
