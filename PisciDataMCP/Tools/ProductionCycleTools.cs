using System.ComponentModel;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;

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

    [McpServerTool]
    [Description("Obtiene la lista de todos los ciclos de producción (lotes de siembra) registrados en el sistema.")]
    public async Task<string> GetProductionCycles()
    {
        return await _apiClient.GetAsync("Productioncycles");
    }

    [McpServerTool]
    [Description("Obtiene los datos detallados de un ciclo de producción específico por su ID (fecha de siembra, conteo inicial de peces, peso promedio inicial y estanque asignado).")]
    public async Task<string> GetProductionCycleById(
        [Description("Identificador numérico único del ciclo de producción")] int id)
    {
        return await _apiClient.GetAsync($"Productioncycles/{id}");
    }
}
