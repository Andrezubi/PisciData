
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Nodes;
using Microsoft.Extensions.Options;
using PisciDataBackend.AI;
using PisciDataBackend.Application.DTOs;

namespace PisciDataBackend.Infraestructure.Clients
{
    public class OllamaApiClient
    {
        private readonly HttpClient _httpClient;
        private readonly McpToolService _mcpToolService;
        private readonly OllamaOptions _options;

        private const string ChatEndpoint = "api/chat";

        public OllamaApiClient(
            HttpClient httpClient,
            McpToolService mcpToolService,
            IOptions<OllamaOptions> options)
        {
            _httpClient = httpClient;
            _mcpToolService = mcpToolService;
            _options = options.Value;
        }

        public async Task<(string? Content, string? Error)> ChatAsync(
            List<OllamaMessageItem> conversationMessages,
            CancellationToken cancellationToken = default)
        {
            try
            {
                // Convert the existing conversation history to Ollama messages.
                var messages = new JsonArray();

                foreach (var item in conversationMessages)
                {
                    messages.Add(new JsonObject
                    {
                        ["role"] = item.Role,
                        ["content"] = item.Content ?? string.Empty
                    });
                }

                // Discover the available MCP tools.
                var tools = await _mcpToolService.GetOllamaToolsAsync(
                    cancellationToken);

                // Execute the Ollama <-> MCP tool-calling loop.
                for (var iteration = 0;
                     iteration <= _options.MaxToolIterations;
                     iteration++)
                {
                    var request = new JsonObject
                    {
                        ["model"] = _options.Model,
                        ["messages"] = messages.DeepClone(),
                        ["tools"] = tools.DeepClone(),
                        ["stream"] = false
                    };

                    using var response = await _httpClient.PostAsJsonAsync(
                        ChatEndpoint,
                        request,
                        cancellationToken);

                    if (!response.IsSuccessStatusCode)
                    {
                        var errorBody = await response.Content.ReadAsStringAsync(
                            cancellationToken);

                        return (null,
                            $"Ollama respondió con error HTTP " +
                            $"{(int)response.StatusCode}: {errorBody}");
                    }

                    var responseJson =
                        await response.Content.ReadFromJsonAsync<JsonObject>(
                            cancellationToken: cancellationToken);

                    var assistantMessage =
                        responseJson?["message"] as JsonObject;

                    if (assistantMessage is null)
                    {
                        return (null,
                            "Ollama devolvió una respuesta inválida.");
                    }

                    // Keep the assistant response, including its tool calls.
                    messages.Add(assistantMessage.DeepClone());

                    var toolCalls =
                        assistantMessage["tool_calls"] as JsonArray;

                    // No more tool calls: Ollama has produced its final answer.
                    if (toolCalls is null || toolCalls.Count == 0)
                    {
                        var content =
                            assistantMessage["content"]?.GetValue<string>();

                        if (string.IsNullOrWhiteSpace(content))
                        {
                            return (null,
                                "Ollama devolvió una respuesta vacía.");
                        }

                        return (content, null);
                    }

                    if (iteration == _options.MaxToolIterations)
                    {
                        return (null,
                            "Se alcanzó el límite de iteraciones de herramientas MCP.");
                    }

                    // Execute every tool requested by Ollama.
                    foreach (var toolCall in toolCalls)
                    {
                        var function =
                            toolCall?["function"] as JsonObject;

                        var name =
                            function?["name"]?.GetValue<string>();

                        if (string.IsNullOrWhiteSpace(name))
                        {
                            return (null,
                                "Ollama solicitó una herramienta sin nombre.");
                        }

                        var arguments =
                            function?["arguments"] as JsonObject
                            ?? new JsonObject();

                        var toolResult =
                            await _mcpToolService.CallToolAsync(
                                name,
                                arguments,
                                cancellationToken);

                        // Return the MCP result to Ollama.
                        messages.Add(new JsonObject
                        {
                            ["role"] = "tool",
                            ["content"] = toolResult
                        });
                    }
                }

                return (null,
                    "Ollama no pudo completar la conversación.");
            }
            catch (OperationCanceledException)
            {
                return (null,
                    "La solicitud a Ollama fue cancelada o excedió el tiempo límite.");
            }
            catch (HttpRequestException)
            {
                return (null,
                    "No se pudo conectar con Ollama. Verifica que el servicio esté encendido.");
            }
            catch (JsonException ex)
            {
                return (null,
                    $"Error al procesar la respuesta de Ollama: {ex.Message}");
            }
            catch (Exception ex)
            {
                return (null,
                    $"Error al consultar Ollama o ejecutar una herramienta MCP: {ex.Message}");
            }
        }
    }
}
