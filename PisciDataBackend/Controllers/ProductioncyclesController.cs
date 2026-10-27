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
    public class ProductioncyclesController : ControllerBase
    {
        private readonly ProductioncycleService _productioncycleService;

        public ProductioncyclesController(ProductioncycleService productioncycleService)
        {
            _productioncycleService = productioncycleService;
        }

        [HttpGet("farm/{farmId:int}")]
        public async Task<ActionResult<IEnumerable<ProductioncycleDto>>> GetByFarm(int farmId)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var cycles = await _productioncycleService.GetByFarmAsync(farmId, userId);
            return Ok(cycles);
        }

        [HttpGet("{id:int}")]
        public async Task<ActionResult<ProductioncycleDto>> GetById(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var cycle = await _productioncycleService.GetByIdAsync(id, userId);
            return cycle == null ? NotFound() : Ok(cycle);
        }

        [HttpPost]
        public async Task<ActionResult<ProductioncycleDto>> Create([FromBody] CreateProductioncycleDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (created, errors) = await _productioncycleService.CreateAsync(dto, userId);
            if (errors.Count > 0)
                return BadRequest(errors);

            return created == null
                ? NotFound()
                : CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }

        [HttpPut("{id:int}")]
        public async Task<ActionResult<ProductioncycleDto>> Update(
            int id, [FromBody] UpdateProductioncycleDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (updated, errors) = await _productioncycleService.UpdateAsync(id, dto, userId);
            if (errors.Count > 0)
                return BadRequest(errors);

            return updated == null ? NotFound() : Ok(updated);
        }

        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            return await _productioncycleService.DeleteAsync(id, userId)
                ? NoContent()
                : NotFound();
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