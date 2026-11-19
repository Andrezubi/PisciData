using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;
using System.Security.Claims;

namespace PisciDataBackend.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/[controller]")]
    public class FarmsController : ControllerBase
    {
        private readonly FarmService _farmService;

        public FarmsController(FarmService farmService)
        {
            _farmService = farmService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<FarmResponseDto>>> GetAll()
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var farms = await _farmService.GetFarmsByUserIdAsync(userId);
            return Ok(farms);
        }

        [HttpGet("my")]
        public async Task<ActionResult<IEnumerable<FarmResponseDto>>> GetMyFarms()
        {
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier) ?? User.FindFirst("UserId") ?? User.FindFirst("sub");
            if (userIdClaim == null || !int.TryParse(userIdClaim.Value, out int userId))
            {
                return Unauthorized(new { message = "No se pudo identificar al usuario." });
            }

            var farms = await _farmService.GetFarmsByUserIdAsync(userId);
            return Ok(farms);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<FarmDto>> GetById(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var farm = await _farmService.GetByIdForOwnerAsync(id, userId);
            if (farm == null)
                return NotFound();

            return Ok(farm);
        }

        [HttpPost]
        public async Task<ActionResult<FarmDto>> Create([FromBody] CreateFarmDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (created, errors) = await _farmService.CreateAsync(dto, userId);
            if (dto.OwnerUserId <= 0)
            {
                var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier) ?? User.FindFirst("UserId") ?? User.FindFirst("sub");
                if (userIdClaim != null && int.TryParse(userIdClaim.Value, out int userId))
                {
                    dto.OwnerUserId = userId;
                }
            }

            var (created, errors) = await _farmService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<FarmDto>> Update(int id, [FromBody] UpdateFarmDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (updated, errors) = await _farmService.UpdateAsync(id, dto, userId);
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

            var deleted = await _farmService.DeleteAsync(id, userId);
            if (!deleted)
                return NotFound();

            return NoContent();
        }

        private bool TryGetUserId(out int userId)
        {
            userId = 0;
            var claim = User.FindFirst(ClaimTypes.NameIdentifier)
                ?? User.FindFirst("UserId")
                ?? User.FindFirst("sub");

            return claim != null && int.TryParse(claim.Value, out userId);
        }
    }
}
