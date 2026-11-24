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
    [Description("Obtiene la lista de todos los estanques del usuario con sus dimensiones, código, área y, si tienen un ciclo activo con mediciones, el último reporte de calidad de agua (LatestWaterQuality: temperatura, pH, oxígeno disuelto, transparencia y estado de parámetros).")]
    public async Task<string> GetPonds(RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync("Ponds", authorization);
    }

    [McpServerTool]
    [Description("Obtiene los datos detallados de un estanque específico por su ID (dimensiones, profundidad, forma) y su última medición de calidad de agua registrada.")]
    public async Task<string> GetPondById(
        [Description("Identificador numérico único del estanque")] int id,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Ponds/{id}", authorization);
    }

    [McpServerTool]
    [Description("Crea y registra un nuevo estanque en una granja.")]
    public async Task<string> CreatePond(
        [Description("ID de la granja a la que pertenece el estanque")] int farmId,
        [Description("Código único del estanque (ej. E-01, EST-02)")] string code,
        [Description("Nombre descriptivo del estanque (opcional)")] string? name,
        [Description("Forma del estanque: RECTANGULAR, CIRCULAR u OTRA (opcional)")] string? shape,
        [Description("Ancho en metros (opcional)")] decimal? width,
        [Description("Largo en metros (opcional)")] decimal? length,
        [Description("Diámetro en metros si es circular (opcional)")] decimal? diameter,
        [Description("Profundidad en metros (opcional)")] decimal? depth,
        [Description("Área en metros cuadrados (opcional)")] decimal? area,
        [Description("Descripción o notas (opcional)")] string? description,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        var payload = System.Text.Json.JsonSerializer.Serialize(new
        {
            farmId,
            code,
            name,
            shape,
            width,
            length,
            diameter,
            depth,
            area,
            description
        });

        return await _apiClient.PostAsync("Ponds", payload, authorization);
    }
}
