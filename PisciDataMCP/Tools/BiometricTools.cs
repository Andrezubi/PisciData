using ModelContextProtocol.Protocol;
using ModelContextProtocol.Server;
using PisciDataMCP.Clients;
using System.ComponentModel;

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
    [Description("Obtiene el historial de todos los muestreos biométricos registrados en el sistema.")]
    public async Task<string> GetBiometrics(RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync("Biometrics",authorization);
    }

    [McpServerTool]
    [Description("Obtiene los detalles de un muestreo biométrico específico por ID (peso promedio en gramos, biomasa total estimada en kg, ración recomendada y estado de salud).")]
    public async Task<string> GetBiometricById(
        [Description("Identificador numérico único del registro biométrico")] int id,
        RequestContext<CallToolRequestParams> context)
    {
        var authorization = GetAuthorization(context);

        if (authorization is null)
            return "{\"error\":\"No se recibió la autenticación de la sesión.\"}";

        return await _apiClient.GetAsync($"Biometrics/{id}", authorization);
    }
}
