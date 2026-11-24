using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Text.RegularExpressions;
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
        private readonly ILogger<OllamaApiClient> _logger;

        private const string ChatEndpoint = "api/chat";
        private const string OpenAiChatEndpoint = "chat/completions";

        private static readonly string[] CloudFallbackModels =
        [
            "qwen/qwen3.8-27b",
            "openai/gpt-oss-120b",
            "openai/gpt-oss-20b"
        ];

        public OllamaApiClient(
            HttpClient httpClient,
            McpToolService mcpToolService,
            IOptions<OllamaOptions> options,
            ILogger<OllamaApiClient> logger)
        {
            _httpClient = httpClient;
            _mcpToolService = mcpToolService;
            _options = options.Value;
            _logger = logger;
        }

        public async Task<(string? Content, string? Error)> ChatAsync(
            List<OllamaMessageItem> conversationMessages,
            string authorizationHeader,
            CancellationToken cancellationToken = default)
        {
            if (!string.IsNullOrWhiteSpace(_options.ApiKey))
            {
                return await ChatOpenAiCompatibleAsync(
                    conversationMessages,
                    authorizationHeader,
                    cancellationToken);
            }

            try
            {
                var messages = new JsonArray();
                foreach (var item in conversationMessages)
                {
                    messages.Add(new JsonObject
                    {
                        ["role"] = item.Role,
                        ["content"] = item.Content ?? string.Empty
                    });
                }

                var tools = await _mcpToolService.GetOllamaToolsAsync(cancellationToken);

                for (var iteration = 0; iteration <= _options.MaxToolIterations; iteration++)
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
                        var errorBody = await response.Content.ReadAsStringAsync(cancellationToken);
                        return (null, $"Ollama respondió con error HTTP {(int)response.StatusCode}: {errorBody}");
                    }

                    var responseJson = await response.Content.ReadFromJsonAsync<JsonObject>(cancellationToken: cancellationToken);
                    var assistantMessage = responseJson?["message"] as JsonObject;
                    if (assistantMessage is null)
                    {
                        return (null, "Ollama devolvió una respuesta inválida.");
                    }

                    messages.Add(assistantMessage.DeepClone());
                    var toolCalls = assistantMessage["tool_calls"] as JsonArray;

                    if (toolCalls is null || toolCalls.Count == 0)
                    {
                        var content = StripThinkTags(assistantMessage["content"]?.GetValue<string>());
                        if (string.IsNullOrWhiteSpace(content))
                        {
                            return (null, "Ollama devolvió una respuesta vacía.");
                        }
                        return (content, null);
                    }

                    if (iteration == _options.MaxToolIterations)
                    {
                        return (null, "Se alcanzó el límite de iteraciones de herramientas MCP.");
                    }

                    foreach (var toolCall in toolCalls)
                    {
                        var function = toolCall?["function"] as JsonObject;
                        var name = function?["name"]?.GetValue<string>();
                        if (string.IsNullOrWhiteSpace(name))
                        {
                            return (null, "Ollama solicitó una herramienta sin nombre.");
                        }

                        var arguments = function?["arguments"] as JsonObject ?? new JsonObject();
                        var toolResult = await _mcpToolService.CallToolAsync(
                            name,
                            arguments,
                            authorizationHeader,
                            cancellationToken);

                        messages.Add(new JsonObject
                        {
                            ["role"] = "tool",
                            ["content"] = toolResult
                        });
                    }
                }

                return (null, "Ollama no pudo completar la conversación.");
            }
            catch (OperationCanceledException)
            {
                return (null, "La solicitud a Ollama fue cancelada o excedió el tiempo límite.");
            }
            catch (HttpRequestException)
            {
                return (null, "No se pudo conectar con Ollama. Verifica que el servicio esté encendido.");
            }
            catch (JsonException ex)
            {
                return (null, $"Error al procesar la respuesta de Ollama: {ex.Message}");
            }
            catch (Exception ex)
            {
                return (null, $"Error al consultar Ollama o ejecutar una herramienta MCP: {ex.Message}");
            }
        }

        private async Task<(string? Content, string? Error)> ChatOpenAiCompatibleAsync(
            List<OllamaMessageItem> conversationMessages,
            string authorizationHeader,
            CancellationToken cancellationToken)
        {
            try
            {
                // Mantener system prompt + últimos 8 mensajes para optimizar presupuesto de tokens por minuto
                var messages = new JsonArray();
                var systemItem = conversationMessages.FirstOrDefault(m => m.Role.Equals("system", StringComparison.OrdinalIgnoreCase));
                if (systemItem is not null)
                {
                    messages.Add(new JsonObject
                    {
                        ["role"] = "system",
                        ["content"] = systemItem.Content ?? string.Empty
                    });
                }

                foreach (var item in conversationMessages
                    .Where(m => !m.Role.Equals("system", StringComparison.OrdinalIgnoreCase))
                    .TakeLast(8))
                {
                    messages.Add(new JsonObject
                    {
                        ["role"] = item.Role.ToLowerInvariant(),
                        ["content"] = item.Content ?? string.Empty
                    });
                }

                JsonArray? compactTools = null;
                try
                {
                    var rawTools = await _mcpToolService.GetOllamaToolsAsync(cancellationToken);
                    compactTools = CompactToolsForCloud(rawTools);
                }
                catch (Exception ex)
                {
                    _logger.LogWarning(ex, "MCP tools no disponibles en modo nube; continuando sin tools.");
                }

                var candidateModels = BuildCandidateModels(_options.Model);
                var currentModelIndex = 0;

                for (var iteration = 0; iteration <= _options.MaxToolIterations; iteration++)
                {
                    HttpResponseMessage? response = null;
                    string lastErrorBody = string.Empty;
                    int lastStatusCode = 0;

                    while (currentModelIndex < candidateModels.Count)
                    {
                        var activeModel = candidateModels[currentModelIndex];
                        var payload = new JsonObject
                        {
                            ["model"] = activeModel,
                            ["messages"] = messages.DeepClone(),
                            ["temperature"] = 0.3
                        };

                        if (compactTools is not null && compactTools.Count > 0)
                        {
                            payload["tools"] = compactTools.DeepClone();
                            payload["tool_choice"] = "auto";
                        }

                        using var request = new HttpRequestMessage(HttpMethod.Post, OpenAiChatEndpoint)
                        {
                            Content = JsonContent.Create(payload)
                        };
                        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", _options.ApiKey!.Trim());

                        response = await _httpClient.SendAsync(request, cancellationToken);
                        if (response.IsSuccessStatusCode)
                        {
                            break;
                        }

                        lastStatusCode = (int)response.StatusCode;
                        lastErrorBody = await response.Content.ReadAsStringAsync(cancellationToken);
                        response.Dispose();
                        response = null;

                        // Si el modelo alcanzó cuota TPM (413/429) o fue retirado (404), rotar al siguiente modelo activo
                        if (lastStatusCode is 413 or 429 or 404 && currentModelIndex + 1 < candidateModels.Count)
                        {
                            _logger.LogWarning("Modelo {Model} respondió {Status}; rotando a {NextModel}",
                                activeModel, lastStatusCode, candidateModels[currentModelIndex + 1]);
                            currentModelIndex++;
                            continue;
                        }

                        return (null, $"El proveedor IA respondió con HTTP {lastStatusCode}: {lastErrorBody}");
                    }

                    if (response is null)
                    {
                        return (null, $"Límite temporal de tokens alcanzado ({lastStatusCode}). Espera unos segundos e intenta de nuevo.");
                    }

                    JsonObject? json;
                    using (response)
                    {
                        json = await response.Content.ReadFromJsonAsync<JsonObject>(cancellationToken: cancellationToken);
                    }

                    var assistantMessage = json?["choices"]?[0]?["message"] as JsonObject;
                    if (assistantMessage is null)
                    {
                        return (null, "El proveedor IA devolvió una respuesta inválida.");
                    }

                    var toolCalls = assistantMessage["tool_calls"] as JsonArray;
                    if (toolCalls is null || toolCalls.Count == 0)
                    {
                        var content = StripThinkTags(assistantMessage["content"]?.GetValue<string>());
                        if (string.IsNullOrWhiteSpace(content))
                        {
                            return (null, "El proveedor IA devolvió una respuesta vacía.");
                        }
                        return (content, null);
                    }

                    if (iteration == _options.MaxToolIterations)
                    {
                        return (null, "Se alcanzó el límite de iteraciones de herramientas MCP.");
                    }

                    var assistantHistoryMsg = new JsonObject
                    {
                        ["role"] = "assistant",
                        ["content"] = assistantMessage["content"]?.DeepClone(),
                        ["tool_calls"] = toolCalls.DeepClone()
                    };
                    messages.Add(assistantHistoryMsg);

                    foreach (var toolCall in toolCalls)
                    {
                        var callId = toolCall?["id"]?.GetValue<string>() ?? Guid.NewGuid().ToString("N");
                        var function = toolCall?["function"] as JsonObject;
                        var name = function?["name"]?.GetValue<string>();
                        if (string.IsNullOrWhiteSpace(name))
                        {
                            continue;
                        }

                        var argumentsObj = ParseArgumentsNode(function?["arguments"]);
                        string toolResult;
                        try
                        {
                            var rawToolResult = await _mcpToolService.CallToolAsync(
                                name,
                                argumentsObj,
                                authorizationHeader,
                                cancellationToken);
                            toolResult = CompactMcpResult(rawToolResult);
                        }
                        catch (Exception toolEx)
                        {
                            _logger.LogWarning(toolEx, "Fallo al ejecutar herramienta MCP {ToolName}", name);
                            toolResult = JsonSerializer.Serialize(new { error = toolEx.Message });
                        }

                        messages.Add(new JsonObject
                        {
                            ["role"] = "tool",
                            ["tool_call_id"] = callId,
                            ["content"] = toolResult
                        });
                    }
                }

                return (null, "El asistente no pudo completar la consulta tras múltiples herramientas.");
            }
            catch (OperationCanceledException)
            {
                return (null, "La solicitud al proveedor IA excedió el tiempo límite.");
            }
            catch (Exception ex)
            {
                return (null, $"Error al conectar con el proveedor IA: {ex.Message}");
            }
        }

        private static List<string> BuildCandidateModels(string configuredModel)
        {
            var list = new List<string>();
            if (!string.IsNullOrWhiteSpace(configuredModel))
                list.Add(configuredModel.Trim());

            foreach (var m in CloudFallbackModels)
            {
                if (!list.Contains(m, StringComparer.OrdinalIgnoreCase))
                    list.Add(m);
            }
            return list;
        }

        private static JsonArray CompactToolsForCloud(JsonArray rawTools)
        {
            var compact = new JsonArray();
            foreach (var node in rawTools)
            {
                if (node is not JsonObject toolObj) continue;
                var fn = toolObj["function"] as JsonObject;
                if (fn is null) continue;

                var desc = fn["description"]?.GetValue<string>() ?? "";
                if (desc.Length > 110)
                    desc = desc[..110].TrimEnd() + ".";

                var parameters = (fn["parameters"]?.DeepClone() as JsonObject) ?? new JsonObject { ["type"] = "object" };
                if (parameters["properties"] is JsonObject props)
                {
                    foreach (var kv in props)
                    {
                        if (kv.Value is JsonObject propObj && propObj["description"] is JsonValue dVal &&
                            dVal.TryGetValue<string>(out var pDesc) && pDesc.Length > 60)
                        {
                            propObj["description"] = pDesc[..60].TrimEnd();
                        }
                    }
                }

                compact.Add(new JsonObject
                {
                    ["type"] = "function",
                    ["function"] = new JsonObject
                    {
                        ["name"] = fn["name"]?.GetValue<string>() ?? "",
                        ["description"] = desc,
                        ["parameters"] = parameters
                    }
                });
            }
            return compact;
        }

        private static string CompactMcpResult(string rawResult)
        {
            try
            {
                var root = JsonNode.Parse(rawResult) as JsonObject;
                if (root?["content"] is JsonArray contentArr && contentArr.Count > 0)
                {
                    var texts = contentArr
                        .OfType<JsonObject>()
                        .Select(c => c["text"]?.GetValue<string>())
                        .Where(t => !string.IsNullOrWhiteSpace(t));
                    var joined = string.Join("\n", texts);
                    if (!string.IsNullOrWhiteSpace(joined))
                        return joined.Length > 3500 ? joined[..3500] : joined;
                }
            }
            catch
            {
                // Conservar rawResult si no es JSON envolvente
            }

            return rawResult.Length > 3500 ? rawResult[..3500] : rawResult;
        }

        private static JsonObject ParseArgumentsNode(JsonNode? rawArgs)
        {
            if (rawArgs is JsonObject obj)
                return (JsonObject)obj.DeepClone();

            if (rawArgs is JsonValue val && val.TryGetValue<string>(out var jsonStr) && !string.IsNullOrWhiteSpace(jsonStr))
            {
                try
                {
                    if (JsonNode.Parse(jsonStr) is JsonObject parsed)
                        return parsed;
                }
                catch
                {
                    // Ignorar JSON malformado
                }
            }

            return new JsonObject();
        }

        private static string StripThinkTags(string? raw)
        {
            if (string.IsNullOrWhiteSpace(raw))
                return string.Empty;

            var cleaned = Regex.Replace(raw, @"<think>[\s\S]*?</think>", "", RegexOptions.IgnoreCase);
            return cleaned.Trim();
        }
    }
}
