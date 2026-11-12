using System.Text;
using System.Text.RegularExpressions;
using Microsoft.Extensions.Options;
using Whisper.net;
using PisciDataBackend.AI;

namespace PisciDataBackend.Application.Services
{
    public class WhisperService : IWhisperService, IDisposable
    {
        private readonly WhisperOptions _options;
        private readonly ILogger<WhisperService> _logger;
        private readonly IWebHostEnvironment _environment;
        private readonly SemaphoreSlim _semaphore = new(1, 1);
        private WhisperFactory? _factory;
        private bool _disposed;

        public WhisperService(
            IOptions<WhisperOptions> options,
            ILogger<WhisperService> logger,
            IWebHostEnvironment environment)
        {
            _options = options.Value;
            _logger = logger;
            _environment = environment;
        }

        private WhisperFactory GetOrCreateFactory()
        {
            if (_factory != null) return _factory;

            lock (_semaphore)
            {
                if (_factory != null) return _factory;

                var resolvedPath = Path.IsPathRooted(_options.ModelPath)
                    ? _options.ModelPath
                    : Path.Combine(_environment.ContentRootPath, _options.ModelPath);

                if (!File.Exists(resolvedPath))
                {
                    _logger.LogError("Whisper model not found at path: {Path}", resolvedPath);
                    throw new FileNotFoundException($"Whisper model file was not found at '{resolvedPath}'. Please verify the ggml model is installed.", resolvedPath);
                }

                _logger.LogInformation("Initializing WhisperFactory with model: {Path}", resolvedPath);
                _factory = WhisperFactory.FromPath(resolvedPath);
                return _factory;
            }
        }

        public async Task<string> TranscribeAsync(Stream audioStream, string? language = null, CancellationToken cancellationToken = default)
        {
            await _semaphore.WaitAsync(cancellationToken);
            try
            {
                var factory = GetOrCreateFactory();
                var targetLanguage = string.IsNullOrWhiteSpace(language) ? _options.Language : language;

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

                var result = sb.ToString().Trim();

                // Strip Whisper hallucinated special tokens like [Música], [Music], [Aplausos], (música), etc.
                result = Regex.Replace(result, @"[\[\(]\s*[Mm][uú]sica\s*[\]\)]", "").Trim();
                result = Regex.Replace(result, @"[\[\(]\s*[Mm]usic\s*[\]\)]", "").Trim();
                result = Regex.Replace(result, @"[\[\(]\s*[Aa]plausos\s*[\]\)]", "").Trim();
                result = Regex.Replace(result, @"[\[\(]\s*[Ss]ilencio\s*[\]\)]", "").Trim();
                result = Regex.Replace(result, @"[\[\(]\s*[Rr]isas?\s*[\]\)]", "").Trim();
                result = Regex.Replace(result, @"[\[\(]\s*[Ii]naudible\s*[\]\)]", "").Trim();

                _logger.LogInformation("Audio transcribed ({Length} chars): {Text}", result.Length, result);
                return result;
            }
            finally
            {
                _semaphore.Release();
            }
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
