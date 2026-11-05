using ModelContextProtocol.Protocol;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;
using System.ComponentModel;

namespace PisciDataMCP.Tools;

/// <summary>
/// Herramientas MCP para la gestión y consulta de Ciclos de Producción acuícola.
/// Proporciona los datos iniciales de siembra (conteo de alevines, biomasa inicial, fechas) necesarios para simulación y seguimiento.
/// </summary>
public class ProductionCycleTools
{
    private readonly PisciDataApiClient _apiClient;

    public ProductionCycleTools(PisciDataApiClient apiClient)
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
    [Description("Obtiene la lista de todos los ciclos de producción (lotes de siembra) registrados en el sistema.")]
    public async Task<string> GetProductionCycles(RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync("Productioncycles", authorization);
    }

    [McpServerTool]
    [Description("Obtiene los datos detallados de un ciclo de producción específico por su ID (fecha de siembra, conteo inicial de peces, peso promedio inicial y estanque asignado).")]
    public async Task<string> GetProductionCycleById(
        [Description("Identificador numérico único del ciclo de producción")] int id,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Productioncycles/{id}", authorization);
    }


    [McpServerTool]
    [Description("Obtiene todos los ciclos de producción de una granja específica. Solo devuelve los ciclos a los que tiene acceso el usuario autenticado.")]
    public async Task<string> GetProductionCyclesByFarm(
    [Description("Identificador numérico de la granja")] int farmId,
    RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync(
            $"Productioncycle/farm/{farmId}",
            authorization);
    }

}

