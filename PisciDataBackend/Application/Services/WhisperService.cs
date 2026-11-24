using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text;
using System.Text.Json.Nodes;
using System.Text.RegularExpressions;
using Microsoft.Extensions.Options;
using Whisper.net;
using PisciDataBackend.AI;

namespace PisciDataBackend.Application.Services
{
    public class WhisperService : IWhisperService, IDisposable
    {
        private readonly WhisperOptions _options;
        private readonly OllamaOptions _ollamaOptions;
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly ILogger<WhisperService> _logger;
        private readonly IWebHostEnvironment _environment;
        private readonly SemaphoreSlim _semaphore = new(1, 1);
        private WhisperFactory? _factory;
        private bool _disposed;

        public WhisperService(
            IOptions<WhisperOptions> options,
            IOptions<OllamaOptions> ollamaOptions,
            IHttpClientFactory httpClientFactory,
            ILogger<WhisperService> logger,
            IWebHostEnvironment environment)
        {
            _options = options.Value;
            _ollamaOptions = ollamaOptions.Value;
            _httpClientFactory = httpClientFactory;
            _logger = logger;
            _environment = environment;
        }

        private string ResolveModelPath()
        {
            return Path.IsPathRooted(_options.ModelPath)
                ? _options.ModelPath
                : Path.Combine(_environment.ContentRootPath, _options.ModelPath);
        }

        private WhisperFactory GetOrCreateFactory(string resolvedPath)
        {
            if (_factory != null) return _factory;

            lock (_semaphore)
            {
                if (_factory != null) return _factory;

                _logger.LogInformation("Initializing WhisperFactory with model: {Path}", resolvedPath);
                _factory = WhisperFactory.FromPath(resolvedPath);
                return _factory;
            }
        }

        public async Task<string> TranscribeAsync(Stream audioStream, string? language = null, CancellationToken cancellationToken = default)
        {
            var targetLanguage = string.IsNullOrWhiteSpace(language) ? _options.Language : language;
            var resolvedPath = ResolveModelPath();

            // Si no existe el archivo .bin local (ej. despliegue en Railway) y hay ApiKey configurada, usar Whisper en la nube
            if (!File.Exists(resolvedPath) && !string.IsNullOrWhiteSpace(_ollamaOptions.ApiKey))
            {
                return await TranscribeViaCloudApiAsync(audioStream, targetLanguage, cancellationToken);
            }

            if (!File.Exists(resolvedPath))
            {
                _logger.LogError("Whisper model not found at path: {Path}", resolvedPath);
                throw new FileNotFoundException($"Whisper model file was not found at '{resolvedPath}'. Please verify the ggml model is installed.", resolvedPath);
            }

            await _semaphore.WaitAsync(cancellationToken);
            try
            {
                var factory = GetOrCreateFactory(resolvedPath);

                using var processor = factory.CreateBuilder()
                    .WithLanguage(targetLanguage)
                    .WithNoContext()
                    .WithSingleSegment()
                    .Build();

                var sb = new StringBuilder();
                await foreach (var segment in processor.ProcessAsync(audioStream, cancellationToken))
                {
                    sb.Append(segment.Text);
                }

                var result = CleanHallucinations(sb.ToString());
                _logger.LogInformation("Audio transcribed locally ({Length} chars): {Text}", result.Length, result);
                return result;
            }
            finally
            {
                _semaphore.Release();
            }
        }

        private async Task<string> TranscribeViaCloudApiAsync(
            Stream audioStream,
            string targetLanguage,
            CancellationToken cancellationToken)
        {
            var endpoint = $"{_ollamaOptions.BaseUrl.TrimEnd('/')}/audio/transcriptions";
            using var client = _httpClientFactory.CreateClient();
            client.Timeout = TimeSpan.FromSeconds(60);

            using var form = new MultipartFormDataContent();
            using var streamContent = new StreamContent(audioStream);
            streamContent.Headers.ContentType = new MediaTypeHeaderValue("audio/wav");
            form.Add(streamContent, "file", "audio.wav");
            form.Add(new StringContent("whisper-large-v3-turbo"), "model");
            form.Add(new StringContent(targetLanguage), "language");

            using var request = new HttpRequestMessage(HttpMethod.Post, endpoint)
            {
                Content = form
            };
            request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", _ollamaOptions.ApiKey!.Trim());

            using var response = await client.SendAsync(request, cancellationToken);
            if (!response.IsSuccessStatusCode)
            {
                var err = await response.Content.ReadAsStringAsync(cancellationToken);
                throw new InvalidOperationException($"Proveedor de voz respondió HTTP {(int)response.StatusCode}: {err}");
            }

            var json = await response.Content.ReadFromJsonAsync<JsonObject>(cancellationToken: cancellationToken);
            var rawText = json?["text"]?.GetValue<string>() ?? string.Empty;
            var cleaned = CleanHallucinations(rawText);
            _logger.LogInformation("Audio transcribed via cloud API ({Length} chars): {Text}", cleaned.Length, cleaned);
            return cleaned;
        }

        private static string CleanHallucinations(string input)
        {
            var result = input.Trim();
            result = Regex.Replace(result, @"[\[\(]\s*[Mm][uú]sica\s*[\]\)]", "").Trim();
            result = Regex.Replace(result, @"[\[\(]\s*[Mm]usic\s*[\]\)]", "").Trim();
            result = Regex.Replace(result, @"[\[\(]\s*[Aa]plausos\s*[\]\)]", "").Trim();
            result = Regex.Replace(result, @"[\[\(]\s*[Ss]ilencio\s*[\]\)]", "").Trim();
            result = Regex.Replace(result, @"[\[\(]\s*[Rr]isas?\s*[\]\)]", "").Trim();
            result = Regex.Replace(result, @"[\[\(]\s*[Ii]naudible\s*[\]\)]", "").Trim();
            return result;
        }

        public void Dispose()
        {
            if (_disposed) return;
            _disposed = true;
            _factory?.Dispose();
            _semaphore.Dispose();
        }
    }
}
