namespace PisciDataBackend.Application.Services
{
    public interface IWhisperService
    {
        Task<string> TranscribeAsync(Stream audioStream, string? language = null, CancellationToken cancellationToken = default);
    }
}
