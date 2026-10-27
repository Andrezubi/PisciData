using System.Net.Http.Json;
using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Infraestructure.Clients
{
    // cliente http tipado para comunicarse con la api de ollama
    public class OllamaApiClient
    {
        private readonly HttpClient _httpClient;
        private readonly string _model;
        private const string ChatEndpoint = "api/chat";

        public OllamaApiClient(HttpClient httpClient, IConfiguration config)
        {
            _httpClient = httpClient;
            _model = config["Ollama:Model"] ?? "qwen3.5:4b";
        }

        // envia el historial de mensajes a ollama y retorna la respuesta o el error
        public async Task<(string? Content, string? Error)> ChatAsync(
            List<OllamaMessageItem> messages,
            CancellationToken cancellationToken = default)
        {
            var request = new OllamaChatRequest
            {
                Model = _model,
                Messages = messages,
                Stream = false
            };

            try
            {
                var response = await _httpClient.PostAsJsonAsync(ChatEndpoint, request, cancellationToken);
                if (!response.IsSuccessStatusCode)
                    return (null, $"Ollama respondió con error HTTP {(int)response.StatusCode}.");

                var result = await response.Content.ReadFromJsonAsync<OllamaChatResponse>(cancellationToken: cancellationToken);
                if (string.IsNullOrWhiteSpace(result?.Message?.Content))
                    return (null, "Ollama devolvió una respuesta vacía.");

                return (result.Message.Content, null);
            }
            catch (OperationCanceledException)
            {
                return (null, "La solicitud a Ollama fue cancelada o excedió el tiempo límite.");
            }
            catch (HttpRequestException)
            {
                return (null, "No se pudo conectar con Ollama. Verifica que el servicio esté encendido.");
            }
            catch (Exception ex)
            {
                return (null, $"Error inesperado al consultar el modelo: {ex.Message}");
            }
        }
    }
}
