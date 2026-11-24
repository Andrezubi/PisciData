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

    [McpServerTool]
    [Description("Obtiene el historial de alimentaciones registradas para un ciclo productivo específico, incluyendo detalles del alimento (marca, producto, pellet y proteína), raciones y horas.")]
    public async Task<string> GetFeedingsByCycle(
        [Description("Identificador numérico del ciclo de producción")] int cycleId,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Feedings/cycle/{cycleId}", authorization);
    }

    [McpServerTool]
    [Description("Calcula y devuelve la recomendación o sugerencia nutricional ideal para un ciclo productivo (raciones diarias en kg, tomas por día, tamaño de pellet o polvo y % de proteína), basada en biometría (peso) o en la edad de los peces.")]
    public async Task<string> GetFeedingRecommendation(
        [Description("Identificador numérico del ciclo de producción")] int cycleId,
        [Description("Fecha opcional para el cálculo en formato YYYY-MM-DD. Si se omite, se usa la fecha actual.")] string? date,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        var query = string.IsNullOrWhiteSpace(date) ? "" : $"?date={Uri.EscapeDataString(date.Trim())}";
        return await _apiClient.GetAsync($"Feedings/recommendation/{cycleId}{query}", authorization);
    }

    [McpServerTool]
    [Description("Registra un nuevo evento de alimentación para un ciclo productivo.")]
    public async Task<string> CreateFeeding(
        [Description("ID del ciclo de producción a alimentar")] int productionCycleId,
        [Description("ID del tipo de alimento a suministrar (de la tabla Feed)")] int feedId,
        [Description("Cantidad de alimento suministrado en kilogramos (permite 0 o más)")] decimal quantityKg,
        [Description("Fecha de alimentación en formato YYYY-MM-DD (opcional, por defecto fecha de hoy)")] string? feedingDate,
        [Description("Hora de alimentación en formato HH:mm (opcional, por defecto hora actual)")] string? feedingTime,
        [Description("Número de comida del día (opcional, si es null el backend lo calcula automáticamente)")] int? mealNumber,
        [Description("Comportamiento observado de los peces, ej: 'Voraces', 'Consumo normal' (opcional)")] string? behavior,
        [Description("Observaciones adicionales (opcional)")] string? observations,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        var date = string.IsNullOrWhiteSpace(feedingDate)
            ? DateTime.UtcNow.ToString("yyyy-MM-dd")
            : feedingDate.Trim();

        var time = string.IsNullOrWhiteSpace(feedingTime)
            ? DateTime.UtcNow.ToString("HH:mm:ss")
            : (feedingTime.Trim().Length == 5 ? $"{feedingTime.Trim()}:00" : feedingTime.Trim());

        var payload = System.Text.Json.JsonSerializer.Serialize(new
        {
            productionCycleId,
            feedId,
            feedingDate = date,
            feedingTime = time,
            quantityKg,
            mealNumber,
            behavior,
            observations
        });

        return await _apiClient.PostAsync("Feedings", payload, authorization);
    }
}
