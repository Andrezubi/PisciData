using ModelContextProtocol.Protocol;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;
using System.ComponentModel;
using System.Text.Json;

namespace PisciDataMCP.Tools;

/// <summary>
/// Herramientas MCP para la gestión de Insumos y Suministros generales (Supplies).
/// Permite al modelo de IA auditar inventarios de químicos, cal, fertilizantes, combustibles y equipos.
/// </summary>
public class SupplyTools
{
    private readonly PisciDataApiClient _apiClient;

    public SupplyTools(PisciDataApiClient apiClient)
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
    [Description("Obtiene la lista de todos los insumos y suministros registrados en el inventario (químicos, combustibles, fertilizantes, equipos, cantidades y estado de stock).")]
    public async Task<string> GetSupplies(RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync("Supplies", authorization);
    }

    [McpServerTool]
    [Description("Obtiene el detalle de un insumo o suministro específico por su ID.")]
    public async Task<string> GetSupplyById(
        [Description("Identificador numérico único del insumo")] int id,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Supplies/{id}", authorization);
    }

    [McpServerTool]
    [Description("Registra un nuevo insumo o suministro general en el inventario de una granja.")]
    public async Task<string> CreateSupply(
        [Description("ID de la granja a la que pertenece el insumo")] int farmId,
        [Description("Nombre descriptivo del insumo (ej. Cal viva, Probióticos, Diésel)")] string name,
        [Description("Categoría del insumo: QUIMICO, COMBUSTIBLE, EQUIPO u OTRA (opcional)")] string? category,
        [Description("Cantidad en existencia")] decimal quantity,
        [Description("Unidad de medida (ej. kg, L, sacos, unidades)")] string unit,
        [Description("Stock mínimo de seguridad para alertas (opcional)")] decimal? minimumStock,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        var payload = JsonSerializer.Serialize(new
        {
            farmId,
            name,
            category,
            quantity,
            unit,
            minimumStock
        });

        return await _apiClient.PostAsync("Supplies", payload, authorization);
    }
}
