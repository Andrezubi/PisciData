using System.ComponentModel;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;

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

    [McpServerTool]
    [Description("Obtiene la lista de todos los eventos de alimentación registrados en el sistema.")]
    public async Task<string> GetFeedings()
    {
        return await _apiClient.GetAsync("Feedings");
    }

    [McpServerTool]
    [Description("Obtiene el detalle de un evento de alimentación específico por su ID (fecha, hora, cantidad en kg, número de ración y comportamiento observado).")]
    public async Task<string> GetFeedingById(
        [Description("Identificador numérico único del registro de alimentación")] int id)
    {
        return await _apiClient.GetAsync($"Feedings/{id}");
    }
}
