using ModelContextProtocol.Protocol;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;
using System.ComponentModel;

namespace PisciDataMCP.Tools;

/// <summary>
/// Herramientas MCP para la consulta de Estanques piscícolas.
/// Permite al modelo de IA conocer la infraestructura física, dimensiones y capacidades hidráulicas.
/// </summary>
public class PondTools
{
    private readonly PisciDataApiClient _apiClient;

    public PondTools(PisciDataApiClient apiClient)
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
    [Description("Obtiene la lista de todos los estanques registrados en el sistema, incluyendo sus dimensiones, área y código. ademas de los parametros de calidad agua")]
    public async Task<string> GetPonds(RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync("Ponds", authorization);
    }

    [McpServerTool]
    [Description("Obtiene los datos detallados de un estanque específico (dimensiones, profundidad, forma) mediante su ID. ademas de  los parametros de calidad de agua")]
    public async Task<string> GetPondById(
        [Description("Identificador numérico único del estanque")] int id,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Ponds/{id}", authorization);
    }
}
