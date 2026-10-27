using System.ComponentModel;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;

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



    [McpServerTool]
    [Description("Obtiene la lista completa de todas las granjas Piscícolas activas registradas en PisciData.")]
    public async Task<string> GetFarms()
    {
        return await _apiClient.GetAsync("Farms");
    }




    [McpServerTool]
    [Description("Obtiene la informacion detallada de una granja especifica a partir de su identificador unico (ID).")]
    public async Task<string> GetFarmById(
        [Description("Identificador numerico unico de la granja (ej: 1, 2)")] int id)
    {
        return await _apiClient.GetAsync($"Farms/{id}");
    }
}
