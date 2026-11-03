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
    public class SuppliesController : ControllerBase
    {
        private readonly SupplyService _supplyService;

        public SuppliesController(SupplyService supplyService)
        {
            _supplyService = supplyService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<SupplyDto>>> GetAll()
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var supplies = await _supplyService.GetAllAsync(userId);
            return Ok(supplies);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<SupplyDto>> GetById(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var supply = await _supplyService.GetByIdAsync(id, userId);
            if (supply == null)
                return NotFound();

            return Ok(supply);
        }

        [HttpPost]
        public async Task<ActionResult<SupplyDto>> Create([FromBody] CreateSupplyDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (created, errors) = await _supplyService.CreateAsync(dto, userId);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<SupplyDto>> Update(int id, [FromBody] UpdateSupplyDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (updated, errors) = await _supplyService.UpdateAsync(id, dto, userId);
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

            var deleted = await _supplyService.DeleteAsync(id, userId);
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
