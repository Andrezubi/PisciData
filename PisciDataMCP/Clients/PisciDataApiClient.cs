using Microsoft.Extensions.Logging;
using System.Net;
using System.Net.Http.Headers;
using System.Text;

namespace PisciDataMCP.Clients;

/// <summary>
/// Cliente HTTP tipado para consumir de forma segura y resiliente la API REST de PisciDataBackend.
/// Centraliza el manejo de peticiones, codigos de estado y errores hacia el backend.
/// </summary>
public class PisciDataApiClient
{
    private readonly HttpClient _httpClient;
    private readonly ILogger<PisciDataApiClient> _logger;

    public PisciDataApiClient(HttpClient httpClient, ILogger<PisciDataApiClient> logger)
    {
        _httpClient = httpClient;
        _logger = logger;
    }

    private static bool TrySetAuthorization(
        HttpRequestMessage request,
        string authorizationHeader)
    {
        if (!AuthenticationHeaderValue.TryParse(
                authorizationHeader, out var authorization))
        {
            return false;
        }

        if (!authorization.Scheme.Equals(
                "Bearer", StringComparison.OrdinalIgnoreCase))
        {
            return false;
        }

        request.Headers.Authorization = authorization;
        return true;
    }

    public async Task<string> GetAsync(
        string endpoint,
        string authorizationHeader)
    {
        try
        {
            using var request = new HttpRequestMessage(
                HttpMethod.Get, endpoint);

            if (!TrySetAuthorization(request, authorizationHeader))
                return "{\"error\":\"Token de autenticación inválido.\"}";

            _logger.LogInformation("Enviando GET a: {Endpoint}", endpoint);

            using var response = await _httpClient.SendAsync(request);
            var responseBody = await response.Content.ReadAsStringAsync();

            if (response.IsSuccessStatusCode)
                return responseBody;

            if (response.StatusCode == HttpStatusCode.NotFound)
                return $"{{\"error\":\"Recurso no encontrado en '{endpoint}'.\"}}";

            if (response.StatusCode == HttpStatusCode.Unauthorized)
                return "{\"error\":\"La sesión no está autenticada o el token ha expirado.\"}";

            if (response.StatusCode == HttpStatusCode.Forbidden)
                return "{\"error\":\"No tienes permiso para acceder a este recurso.\"}";

            return $"{{\"error\":\"La API respondió con código {(int)response.StatusCode}: {responseBody}\"}}";
        }
        catch (HttpRequestException ex)
        {
            _logger.LogError(ex,
                "Falla de conectividad con el backend en GET {Endpoint}", endpoint);

            return "{\"error\":\"No se pudo conectar con PisciDataBackend.\"}";
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error inesperado al consultar {Endpoint}", endpoint);

            return "{\"error\":\"Error interno en el cliente MCP.\"}";
        }
    }

    public async Task<string> PostAsync(
        string endpoint,
        string jsonPayload,
        string authorizationHeader)
    {
        try
        {
            using var request = new HttpRequestMessage(
                HttpMethod.Post, endpoint);

            if (!TrySetAuthorization(request, authorizationHeader))
                return "{\"error\":\"Token de autenticación inválido.\"}";

            request.Content = new StringContent(
                jsonPayload, Encoding.UTF8, "application/json");

            _logger.LogInformation("Enviando POST a: {Endpoint}", endpoint);

            using var response = await _httpClient.SendAsync(request);
            var responseBody = await response.Content.ReadAsStringAsync();

            if (response.IsSuccessStatusCode)
                return responseBody;

            if (response.StatusCode == HttpStatusCode.Unauthorized)
                return "{\"error\":\"La sesión no está autenticada o el token ha expirado.\"}";

            if (response.StatusCode == HttpStatusCode.Forbidden)
                return "{\"error\":\"No tienes permiso para realizar esta operación.\"}";

            if (response.StatusCode == HttpStatusCode.BadRequest)
                return $"{{\"error\":\"Validación fallida en backend: {responseBody}\"}}";

            return $"{{\"error\":\"La API respondió con código {(int)response.StatusCode}: {responseBody}\"}}";
        }
        catch (HttpRequestException ex)
        {
            _logger.LogError(ex,
                "Falla de conectividad con el backend en POST {Endpoint}", endpoint);

            return "{\"error\":\"No se pudo conectar con PisciDataBackend.\"}";
        }
        catch (Exception ex)
        {
            _logger.LogError(ex,
                "Error inesperado en POST {Endpoint}", endpoint);

            return "{\"error\":\"Error interno en el cliente MCP.\"}";
        }
    }
}
