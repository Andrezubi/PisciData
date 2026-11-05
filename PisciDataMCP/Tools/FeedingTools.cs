using ModelContextProtocol.Protocol;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;
using System.ComponentModel;

namespace PisciDataMCP.Tools;

/// <summary>
/// Herramientas MCP para la consulta de eventos de Alimentación piscícola.
/// Permite al modelo de IA auditar el consumo diario de alimento, comportamiento de los peces y calcular el Factor de Conversión Alimenticia (FCR).
/// </summary>
public class FeedingTools
{
    private readonly PisciDataApiClient _apiClient;

    public FeedingTools(PisciDataApiClient apiClient)
    {
        _apiClient = apiClient;
    }

    private static string? GetAuthorization(
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = context.Params?
            .Meta?["authorization"]?
            .GetValue<string>();

        if (string.IsNullOrWhiteSpace(authorization) ||
            !authorization.StartsWith(
                "Bearer ", StringComparison.OrdinalIgnoreCase))
        {
            return null;
        }

        return authorization;
    }

    [McpServerTool]
    [Description("Obtiene la lista de todos los eventos de alimentación registrados en el sistema.")]
    public async Task<string> GetFeedings(RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync("Feedings", authorization);
    }

    [McpServerTool]
    [Description("Obtiene el detalle de un evento de alimentación específico por su ID (fecha, hora, cantidad en kg, número de ración y comportamiento observado).")]
    public async Task<string> GetFeedingById(
        [Description("Identificador numérico único del registro de alimentación")] int id,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Feedings/{id}", authorization);
    }
}
