using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FeedingsController : ControllerBase
    {
        private readonly FeedingService _feedingService;

        public FeedingsController(FeedingService feedingService)
        {
            _feedingService = feedingService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<FeedingDto>>> GetAll()
        {
            var feedings = await _feedingService.GetAllAsync();
            return Ok(feedings);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<FeedingDto>> GetById(int id)
        {
            var feeding = await _feedingService.GetByIdAsync(id);
            if (feeding == null)
                return NotFound();

            return Ok(feeding);
        }

        [HttpPost]
        public async Task<ActionResult<FeedingDto>> Create([FromBody] CreateFeedingDto dto)
        {
            var (created, errors) = await _feedingService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<FeedingDto>> Update(int id, [FromBody] UpdateFeedingDto dto)
        {
            var (updated, errors) = await _feedingService.UpdateAsync(id, dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _feedingService.DeleteAsync(id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}
