using System.Collections.Concurrent;
using System.Text;
using System.Text.Json;
using System.Text.Json.Nodes;
using ModelContextProtocol.Client;

// ---------------------------------------------------------------------------
// PisciDataChat: orquestador entre la UI/app móvil, el LLM local (llama-server)
// y el servidor MCP (PisciDataMCP, lanzado como subproceso por stdio).
//
//   UI/App --POST /api/chat--> [este proceso] --OpenAI tools--> Qwen (llama-server)
//                                   |--JSON-RPC stdio--> PisciDataMCP --HTTP--> Backend
// ---------------------------------------------------------------------------

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddCors(o => o.AddDefaultPolicy(p => p.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()));
builder.Services.AddHttpClient("llm", c => c.Timeout = TimeSpan.FromMinutes(5));

var app = builder.Build();
app.UseCors();
app.UseDefaultFiles();
app.UseStaticFiles();

var cfg = app.Configuration;
var llmBase = cfg["Llm:BaseUrl"]!.TrimEnd('/');
var llmModel = cfg["Llm:Model"] ?? "qwen";
var maxIter = int.Parse(cfg["Llm:MaxToolIterations"] ?? "5");
var maxResult = int.Parse(cfg["Llm:MaxToolResultChars"] ?? "6000");
var logger = app.Logger;

// --- 1. Conexión al MCP por stdio -----------------------------------------
var mcpArgs = cfg.GetSection("Mcp:Arguments").Get<string[]>() ?? [];
mcpArgs = mcpArgs.Select(a => Path.IsPathRooted(a) ? a : Path.GetFullPath(Path.Combine(app.Environment.ContentRootPath, a))).ToArray();

McpClient? mcp = null;
IList<McpClientTool> mcpTools = [];
JsonArray toolsForLlm = [];

async Task ConnectMcpAsync()
{
    var transport = new StdioClientTransport(new StdioClientTransportOptions
    {
        Name = "PisciDataMCP",
        Command = cfg["Mcp:Command"] ?? "dotnet",
        Arguments = mcpArgs,
    });

    var dllPath = Path.GetFullPath(
    Path.Combine(
        app.Environment.ContentRootPath,
        mcpArgs[0]
    )
);

    logger.LogInformation("ContentRoot: {Path}", app.Environment.ContentRootPath);
    logger.LogInformation("MCP DLL: {Path}", dllPath);
    logger.LogInformation("DLL exists: {Exists}", File.Exists(dllPath));

    mcp = await McpClient.CreateAsync(transport);
    mcpTools = await mcp.ListToolsAsync();
    toolsForLlm = new JsonArray(mcpTools.Select(t => (JsonNode)new JsonObject
    {
        ["type"] = "function",
        ["function"] = new JsonObject
        {
            ["name"] = t.Name,
            ["description"] = t.Description,
            ["parameters"] = JsonNode.Parse(t.JsonSchema.GetRawText()),
        },
    }).ToArray());
    logger.LogInformation("MCP conectado. {Count} herramientas: {Names}", mcpTools.Count, string.Join(", ", mcpTools.Select(t => t.Name)));
}

await ConnectMcpAsync();

// --- 2. Historial por sesión ------------------------------------------------
const string SystemPrompt =
    "Eres el asistente de PisciData, un sistema de gestión de piscigranjas. " +
    "Responde siempre en español, de forma breve y clara. " +
    "Para datos de granjas, estanques, ciclos productivos, biometrías o alimentación " +
    "DEBES usar las herramientas disponibles; nunca inventes datos. " +
    "Si una herramienta devuelve un error, explícalo con sencillez al usuario.";

var sessions = new ConcurrentDictionary<string, List<JsonNode>>();

// --- 3. Endpoints -----------------------------------------------------------
app.MapGet("/api/health", async (IHttpClientFactory f) =>
{
    var llmOk = false;
    try { llmOk = (await f.CreateClient("llm").GetAsync(llmBase.Replace("/v1", "") + "/health")).IsSuccessStatusCode; } catch { }
    return Results.Ok(new { mcp = mcp is not null, tools = mcpTools.Select(t => t.Name), llm = llmOk });
});

app.MapPost("/api/chat", async (ChatRequest req, IHttpClientFactory f) =>
{
    if (string.IsNullOrWhiteSpace(req.Message)) return Results.BadRequest(new { error = "message vacío" });

    var sid = string.IsNullOrWhiteSpace(req.SessionId) ? Guid.NewGuid().ToString("N") : req.SessionId!;
    var history = sessions.GetOrAdd(sid, _ => [new JsonObject { ["role"] = "system", ["content"] = SystemPrompt }]);
    history.Add(new JsonObject { ["role"] = "user", ["content"] = req.Message });

    var http = f.CreateClient("llm");
    var trace = new List<object>();

    try
    {
        for (var i = 0; i < maxIter; i++)
        {
            var body = new JsonObject
            {
                ["model"] = llmModel,
                ["messages"] = new JsonArray(history.Select(m => m.DeepClone()).ToArray()),
                ["tools"] = toolsForLlm.DeepClone(),
                ["temperature"] = 0.3,
            };
            using var resp = await http.PostAsync($"{llmBase}/chat/completions",
                new StringContent(body.ToJsonString(), Encoding.UTF8, "application/json"));
            var raw = await resp.Content.ReadAsStringAsync();
            if (!resp.IsSuccessStatusCode)
                return Results.Json(new { error = $"LLM respondió {(int)resp.StatusCode}: {raw}" }, statusCode: 502);

            var msg = JsonNode.Parse(raw)!["choices"]![0]!["message"]!.AsObject();
            var calls = msg["tool_calls"]?.AsArray();

            if (calls is null || calls.Count == 0)
            {
                var text = (msg["content"]?.GetValue<string>() ?? "").Trim();
                history.Add(new JsonObject { ["role"] = "assistant", ["content"] = text });
                return Results.Ok(new { sessionId = sid, reply = text, toolCalls = trace });
            }

            // El modelo pidió herramientas: ejecutarlas vía MCP y devolver resultados.
            history.Add(msg.DeepClone());
            foreach (var call in calls)
            {
                var id = call!["id"]?.GetValue<string>() ?? Guid.NewGuid().ToString("N");
                var name = call["function"]!["name"]!.GetValue<string>();
                var argsText = call["function"]!["arguments"]?.GetValue<string>() ?? "{}";
                string result;
                try
                {
                    var args = JsonSerializer.Deserialize<Dictionary<string, object?>>(argsText) ?? [];
                    var r = await mcp!.CallToolAsync(name, args);
                    result = string.Join("\n", r.Content.OfType<ModelContextProtocol.Protocol.TextContentBlock>().Select(c => c.Text));
                }
                catch (Exception ex) { result = $"{{\"error\":\"{ex.Message.Replace("\"", "'")}\"}}"; }

                logger.LogInformation("Tool {Name}({Args}) -> {Len} chars", name, argsText, result.Length);
                trace.Add(new { name, arguments = argsText, result = result.Length > 800 ? result[..800] + "…" : result });
                if (result.Length > maxResult) result = result[..maxResult] + "…[truncado]";
                history.Add(new JsonObject { ["role"] = "tool", ["tool_call_id"] = id, ["content"] = result });
            }
        }
        return Results.Ok(new { sessionId = sid, reply = "No pude completar la consulta (demasiadas llamadas a herramientas).", toolCalls = trace });
    }
    catch (HttpRequestException ex)
    {
        return Results.Json(new { error = $"No se pudo conectar con el LLM en {llmBase}: {ex.Message}" }, statusCode: 502);
    }
});

app.MapPost("/api/chat/reset", (ChatRequest req) =>
{
    if (req.SessionId is not null) sessions.TryRemove(req.SessionId, out _);
    return Results.NoContent();
});

app.Lifetime.ApplicationStopping.Register(() => mcp?.DisposeAsync().AsTask().Wait(2000));
app.Run();

record ChatRequest(string Message, string? SessionId);
