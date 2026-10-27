using System.ComponentModel;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;

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

    [McpServerTool]
    [Description("Obtiene la lista de todos los estanques registrados en el sistema, incluyendo sus dimensiones, área y código.")]
    public async Task<string> GetPonds()
    {
        return await _apiClient.GetAsync("Ponds");
    }

    [McpServerTool]
    [Description("Obtiene los datos detallados de un estanque específico (dimensiones, profundidad, forma) mediante su ID.")]
    public async Task<string> GetPondById(
        [Description("Identificador numérico único del estanque")] int id)
    {
        return await _apiClient.GetAsync($"Ponds/{id}");
    }
}
