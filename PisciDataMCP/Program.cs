using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using PisciDataMCP.Clients;
using PisciDataMCP.Tools; 

var builder = Host.CreateApplicationBuilder(args);

// Configurar todos los logs para ir a stderr (stdout queda reservado exclusivamente para los mensajes JSON-RPC del protocolo MCP).
builder.Logging.AddConsole(o => o.LogToStandardErrorThreshold = LogLevel.Trace);

// 1. Configuracion del Cliente HTTP hacia la API de PisciData
var apiUrl = builder.Configuration["PisciDataApi:BaseUrl"] ?? "http://localhost:5007/api";

builder.Services.AddHttpClient<PisciDataApiClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl.TrimEnd('/') + "/");
    client.Timeout = TimeSpan.FromSeconds(15);
});

// 2. Registro del Servidor MCP y transporte stdio
builder.Services
    .AddMcpServer()
    .WithStdioServerTransport()
    .WithTools<FarmTools>()
    .WithTools<PondTools>()
    .WithTools<ProductionCycleTools>()
    .WithTools<BiometricTools>()
    .WithTools<FeedingTools>();

await builder.Build().RunAsync();
