using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
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
        public async Task<ActionResult<IEnumerable<FeedingDetailDto>>> GetAll()
        {
            var feedings = await _feedingService.GetAllAsync();
            return Ok(feedings);
        }

        [HttpGet("cycle/{cycleId:int}")]
        public async Task<ActionResult<IEnumerable<FeedingDetailDto>>> GetByCycle(int cycleId)
        {
            var feedings = await _feedingService.GetByProductionCycleIdAsync(cycleId);
            return Ok(feedings);
        }

        [HttpGet("recommendation/{cycleId:int}")]
        public async Task<ActionResult<FeedingRecommendationDto>> GetRecommendation(int cycleId, [FromQuery] DateOnly? date)
        {
            var recommendation = await _feedingService.GetRecommendationAsync(cycleId, date);
            if (recommendation == null)
                return NotFound(new { message = "No se pudo generar recomendación para el ciclo especificado." });

            return Ok(recommendation);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<FeedingDetailDto>> GetById(int id)
        {
            var feeding = await _feedingService.GetByIdAsync(id);
            if (feeding == null)
                return NotFound();

            return Ok(feeding);
        }

        [HttpPost]
        public async Task<ActionResult<FeedingDetailDto>> Create([FromBody] CreateFeedingDto dto)
        {
            TryGetUserId(out var userId);
            var (created, errors) = await _feedingService.CreateAsync(dto, userId > 0 ? userId : null);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<FeedingDetailDto>> Update(int id, [FromBody] UpdateFeedingDto dto)
        {
            TryGetUserId(out var userId);
            var (updated, errors) = await _feedingService.UpdateAsync(id, dto, userId > 0 ? userId : null);
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
