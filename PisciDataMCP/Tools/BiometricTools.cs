using System.ComponentModel;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;

namespace PisciDataMCP.Tools;

/// <summary>
/// Herramientas MCP para la consulta de Muestreos Biométricos.
/// Permite al modelo de IA analizar el crecimiento real de los peces, biomasa y ración diaria calculada.
/// </summary>
public class BiometricTools
{
    private readonly PisciDataApiClient _apiClient;

    public BiometricTools(PisciDataApiClient apiClient)
    {
        _apiClient = apiClient;
    }

    [McpServerTool]
    [Description("Obtiene el historial de todos los muestreos biométricos registrados en el sistema.")]
    public async Task<string> GetBiometrics()
    {
        return await _apiClient.GetAsync("Biometrics");
    }

    [McpServerTool]
    [Description("Obtiene los detalles de un muestreo biométrico específico por ID (peso promedio en gramos, biomasa total estimada en kg, ración recomendada y estado de salud).")]
    public async Task<string> GetBiometricById(
        [Description("Identificador numérico único del registro biométrico")] int id)
    {
        return await _apiClient.GetAsync($"Biometrics/{id}");
    }
}
