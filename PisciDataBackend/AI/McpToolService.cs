using System.Text.Json;
using System.Text.Json.Nodes;
using ModelContextProtocol.Client;
using Microsoft.Extensions.Options;

namespace PisciDataBackend.AI
{
    
    

    public sealed class McpOptions
    {
        public string ServerDll { get; set; } = "";
    }

    public sealed class McpToolService : IAsyncDisposable
    {
        private readonly McpOptions _options;
        private readonly ILogger<McpToolService> _logger;
        private readonly SemaphoreSlim _initLock = new(1, 1);

        private McpClient? _client;
        private IList<McpClientTool>? _tools;

        public McpToolService(
            IOptions<McpOptions> options,
            ILogger<McpToolService> logger)
        {
            _options = options.Value;
            _logger = logger;
        }

        private async Task EnsureConnectedAsync(
            CancellationToken cancellationToken = default)
        {
            if (_client is not null)
                return;

            await _initLock.WaitAsync(cancellationToken);

            try
            {
                if (_client is not null)
                    return;

                if (string.IsNullOrWhiteSpace(_options.ServerDll) ||
                    !File.Exists(_options.ServerDll))
                {
                    throw new FileNotFoundException(
                        "The configured PisciDataMCP DLL was not found.",
                        _options.ServerDll);
                }

                var transport = new StdioClientTransport(
                    new StdioClientTransportOptions
                    {
                        Name = "PisciDataMCP",
                        Command = "dotnet",
                        Arguments = [_options.ServerDll]
                    });

                var client = await McpClient.CreateAsync(
                    transport,
                    cancellationToken: cancellationToken);

                try
                {
                    var tools = await client.ListToolsAsync(
                        cancellationToken: cancellationToken);

                    _client = client;
                    _tools = tools;

                    _logger.LogInformation(
                        "Connected to PisciDataMCP. Discovered {Count} tools: {Names}",
                        tools.Count,
                        string.Join(", ", tools.Select(t => t.Name)));
                }
                catch
                {
                    await client.DisposeAsync();
                    throw;
                }
            }
            finally
            {
                _initLock.Release();
            }
        }

        public async Task<JsonArray> GetOllamaToolsAsync(
            CancellationToken cancellationToken = default)
        {
            await EnsureConnectedAsync(cancellationToken);

            var result = new JsonArray();

            foreach (var tool in _tools!)
            {
                // The MCP SDK exposes each tool's input JSON schema.
                var schema = JsonSerializer.SerializeToNode(tool.JsonSchema)
                             ?? new JsonObject
                             {
                                 ["type"] = "object",
                                 ["properties"] = new JsonObject()
                             };

                result.Add(new JsonObject
                {
                    ["type"] = "function",
                    ["function"] = new JsonObject
                    {
                        ["name"] = tool.Name,
                        ["description"] = tool.Description ?? "",
                        ["parameters"] = schema
                    }
                });
            }

            return result;
        }

        public async Task<string> CallToolAsync(
            string name,
            JsonObject arguments,
            CancellationToken cancellationToken = default)
        {
            await EnsureConnectedAsync(cancellationToken);

            var tool = _tools!.FirstOrDefault(
                t => string.Equals(t.Name, name, StringComparison.Ordinal));

            if (tool is null)
            {
                throw new InvalidOperationException(
                    $"Ollama requested an unknown MCP tool: {name}");
            }

            var args =
                JsonSerializer.Deserialize<Dictionary<string, object?>>(
                    arguments.ToJsonString())
                ?? new Dictionary<string, object?>();

            var result = await _client!.CallToolAsync(
                name,
                args,
                cancellationToken: cancellationToken);

            // Return the complete result to Ollama, including structured
            // content where the MCP server provides it.
            return JsonSerializer.Serialize(result);
        }

        public async ValueTask DisposeAsync()
        {
            if (_client is not null)
            {
                await _client.DisposeAsync();
                _client = null;
            }

            _initLock.Dispose();
        }
    }
}
