using System.Net;
using System.Text;
using Microsoft.Extensions.Logging;

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

    /// <summary>
    /// Ejecuta una peticion GET a un endpoint del backend y retorna la respuesta JSON cruda o un error legible.
    /// </summary>
    /// <param name="endpoint">Ruta relativa del recurso (ej: "Farms", "Ponds/1").</param>
    /// <returns>Cadena JSON estructurada con los datos o mensaje explicativo si fallo.</returns>
    public async Task<string> GetAsync(string endpoint)
    {
        try
        {
            _logger.LogInformation("Enviando GET a: {Endpoint}", endpoint);

            var response = await _httpClient.GetAsync(endpoint);

            if (response.IsSuccessStatusCode)
            {
                return await response.Content.ReadAsStringAsync();
            }

            if (response.StatusCode == HttpStatusCode.NotFound)
            {
                return $"{{\"error\": \"Recurso no encontrado en '{endpoint}'. Verifica el ID solicitado.\"}}";
            }

            var errorBody = await response.Content.ReadAsStringAsync();
            return $"{{\"error\": \"La API respondió con código {(int)response.StatusCode}: {errorBody}\"}}";
        }
        catch (HttpRequestException ex)
        {
            _logger.LogError(ex, "Falla de conectividad con el backend en GET {Endpoint}", endpoint);
            return "{\"error\": \"No se pudo conectar con PisciDataBackend. Asegúrate de que la API esté encendida en http://localhost:5007.\"}";
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error inesperado al consultar {Endpoint}", endpoint);
            return $"{{\"error\": \"Error interno en el cliente MCP: {ex.Message}\"}}";
        }
    }

    /// <summary>
    /// Ejecuta una peticion POST enviando un cuerpo JSON al backend.
    /// </summary>
    /// <param name="endpoint">Ruta relativa del recurso (ej: "Farms", "Ponds").</param>
    /// <param name="jsonPayload">Cuerpo en formato JSON con los datos del nuevo recurso.</param>
    /// <returns>Respuesta de la API con el recurso creado o los errores de validacion.</returns>
    public async Task<string> PostAsync(string endpoint, string jsonPayload)
    {
        try
        {
            _logger.LogInformation("Enviando POST a: {Endpoint}", endpoint);

            var content = new StringContent(jsonPayload, Encoding.UTF8, "application/json");
            var response = await _httpClient.PostAsync(endpoint, content);
            var responseBody = await response.Content.ReadAsStringAsync();

            if (response.IsSuccessStatusCode)
            {
                return responseBody;
            }

            if (response.StatusCode == HttpStatusCode.BadRequest)
            {
                return $"{{\"error\": \"Validación fallida en backend: {responseBody}\"}}";
            }

            return $"{{\"error\": \"La API respondió con código {(int)response.StatusCode}: {responseBody}\"}}";
        }
        catch (HttpRequestException ex)
        {
            _logger.LogError(ex, "Falla de conectividad con el backend en POST {Endpoint}", endpoint);
            return "{\"error\": \"No se pudo conectar con PisciDataBackend al intentar crear el recurso.\"}";
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error inesperado en POST {Endpoint}", endpoint);
            return $"{{\"error\": \"Error interno en el cliente MCP: {ex.Message}\"}}";
        }
    }
}
