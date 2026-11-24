using ModelContextProtocol.Protocol;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;
using System.ComponentModel;
using System.Text.Json;

namespace PisciDataMCP.Tools;

/// <summary>
/// Herramientas MCP para la gestión e inventario de Alimentos piscícolas (Feeds).
/// Permite al modelo de IA consultar tipos de alimento disponibles, marcas, niveles de proteína, tamaños de pellet y stock en kg.
/// </summary>
public class FeedTools
{
    private readonly PisciDataApiClient _apiClient;

    public FeedTools(PisciDataApiClient apiClient)
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
    [Description("Obtiene la lista de todos los alimentos piscícolas registrados en el inventario del usuario (marca, nombre comercial, fase, % proteína, pellet en mm y stock en kg).")]
    public async Task<string> GetFeeds(RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync("Feeds", authorization);
    }

    [McpServerTool]
    [Description("Obtiene el detalle de un alimento específico del inventario por su ID.")]
    public async Task<string> GetFeedById(
        [Description("Identificador numérico único del alimento")] int id,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Feeds/{id}", authorization);
    }

    [McpServerTool]
    [Description("Registra un nuevo tipo o lote de alimento en el inventario de una granja.")]
    public async Task<string> CreateFeed(
        [Description("ID de la granja a la que pertenece el alimento")] int farmId,
        [Description("Marca del fabricante (ej. Nicovita, Purina)")] string brand,
        [Description("Nombre comercial del producto (opcional, ej. Inicio, Crecimiento)")] string? productName,
        [Description("Fase de alimentación (opcional, ej. F0, F1, F2, F3)")] string? phase,
        [Description("Porcentaje de proteína cruda (opcional, ej. 40.0)")] decimal? proteinPercentage,
        [Description("Tamaño de pellet en milímetros (opcional, null si es en polvo)")] decimal? pelletSizeMm,
        [Description("Stock inicial disponible en kilogramos (opcional, ej. 500.0)")] decimal? stockKg,
        [Description("Stock mínimo de seguridad para alertas de inventario (opcional, ej. 100.0)")] decimal? minimumStockKg,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        var payload = JsonSerializer.Serialize(new
        {
            farmId,
            brand,
            productName,
            phase,
            proteinPercentage,
            pelletSizeMm,
            stockKg,
            minimumStockKg
        });

        return await _apiClient.PostAsync("Feeds", payload, authorization);
    }
}
