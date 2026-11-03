using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class FeedsController : ControllerBase
    {
        private readonly FeedService _feedService;

        public FeedsController(FeedService feedService)
        {
            _feedService = feedService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<FeedDto>>> GetAll()
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var feeds = await _feedService.GetAllAsync(userId);
            return Ok(feeds);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<FeedDto>> GetById(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var feed = await _feedService.GetByIdAsync(id, userId);
            if (feed == null)
                return NotFound();

            return Ok(feed);
        }

        [HttpPost]
        public async Task<ActionResult<FeedDto>> Create([FromBody] CreateFeedDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (created, errors) = await _feedService.CreateAsync(dto, userId);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<FeedDto>> Update(int id, [FromBody] UpdateFeedDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (updated, errors) = await _feedService.UpdateAsync(id, dto, userId);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var deleted = await _feedService.DeleteAsync(id, userId);
            if (!deleted)
                return NotFound();

            return NoContent();
        }

        private bool TryGetUserId(out int userId)
        {
            userId = 0;
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier)
                ?? User.FindFirst("UserId")
                ?? User.FindFirst("sub");

            return userIdClaim != null && int.TryParse(userIdClaim.Value, out userId);
        }
    }
}
