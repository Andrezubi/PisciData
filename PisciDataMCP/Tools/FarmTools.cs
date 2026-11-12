using ModelContextProtocol.Protocol;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;
using System.ComponentModel;

namespace PisciDataMCP.Tools;

/// <summary>
/// Conjunto de herramientas MCP para la gestión y consulta de Granjas Piscícolas.
/// Permite al modelo de IA explorar las instalaciones registradas en el sistema.
/// </summary>
/// 
public class FarmTools
{
    private readonly PisciDataApiClient _apiClient;

    public FarmTools(PisciDataApiClient apiClient)
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
    [Description("Obtiene la lista de granjas piscícolas activas del usuario autenticado.")]
    public async Task<string> GetFarms(
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync("Farms", authorization);
    }

    [McpServerTool]
    [Description("Obtiene los detalles de una granja a partir de su identificador.")]
    public async Task<string> GetFarmById(
        [Description("Identificador numérico de la granja")] int id,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Farms/{id}", authorization);
    }
}
