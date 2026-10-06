using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
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
            var feeds = await _feedService.GetAllAsync();
            return Ok(feeds);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<FeedDto>> GetById(int id)
        {
            var feed = await _feedService.GetByIdAsync(id);
            if (feed == null)
                return NotFound();

            return Ok(feed);
        }

        [HttpPost]
        public async Task<ActionResult<FeedDto>> Create([FromBody] CreateFeedDto dto)
        {
            var (created, errors) = await _feedService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<FeedDto>> Update(int id, [FromBody] UpdateFeedDto dto)
        {
            var (updated, errors) = await _feedService.UpdateAsync(id, dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _feedService.DeleteAsync(id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}
