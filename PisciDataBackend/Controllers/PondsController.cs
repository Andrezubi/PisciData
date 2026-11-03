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
    public class PondsController : ControllerBase
    {
        private readonly PondService _pondService;

        public PondsController(PondService pondService)
        {
            _pondService = pondService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<PondDto>>> GetAll()
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var ponds = await _pondService.GetAllAsync(userId);
            return Ok(ponds);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<PondDto>> GetById(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var pond = await _pondService.GetByIdAsync(id, userId);
            if (pond == null)
                return NotFound();

            return Ok(pond);
        }

        [HttpPost]
        public async Task<ActionResult<PondDto>> Create([FromBody] CreatePondDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (created, errors) = await _pondService.CreateAsync(dto, userId);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<PondDto>> Update(int id, [FromBody] UpdatePondDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (updated, errors) = await _pondService.UpdateAsync(id, dto, userId);
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

            var deleted = await _pondService.DeleteAsync(id, userId);
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
