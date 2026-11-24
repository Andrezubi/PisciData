using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;
using WaterQualityDto = PisciDataBackend.Application.DTOs.WaterQualityDto;

namespace PisciDataBackend.Controllers
{
    [ApiController]
    [Authorize]
    [Route("api/waterquality")]
    public class WaterQualityController : ControllerBase
    {
        private readonly WaterQualityService _waterQualityService;

        public WaterQualityController(WaterQualityService waterQualityService)
        {
            _waterQualityService = waterQualityService;
        }

        [HttpGet("farm/{farmId:int}")]
        [HttpGet("farm/{farmId:int}/history")]
        [ProducesResponseType(typeof(IEnumerable<WaterQualityDto>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<IEnumerable<WaterQualityDto>>> GetByFarm(
            int farmId,
            [FromQuery] DateOnly? from = null,
            [FromQuery] DateOnly? to = null)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            if (from.HasValue && to.HasValue && from.Value > to.Value)
                return BadRequest(new { message = "La fecha inicial no puede ser posterior a la fecha final." });

            var result = await _waterQualityService.GetByFarmAsync(farmId, userId, from, to);
            return result == null
                ? NotFound(new { message = "La granja no existe o no pertenece al usuario." })
                : Ok(result);
        }

        [HttpGet("pond/{pondId:int}/history")]
        [ProducesResponseType(typeof(IEnumerable<WaterQualityDto>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<IEnumerable<WaterQualityDto>>> GetByPond(
            int pondId,
            [FromQuery] DateOnly? from = null,
            [FromQuery] DateOnly? to = null)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            if (from.HasValue && to.HasValue && from.Value > to.Value)
                return BadRequest(new { message = "La fecha inicial no puede ser posterior a la fecha final." });

            var result = await _waterQualityService.GetByPondAsync(pondId, userId, from, to);
            return result == null
                ? NotFound(new { message = "El estanque no existe o no pertenece al usuario." })
                : Ok(result);
        }

        [HttpGet("pond/{pondId:int}/latest")]
        [ProducesResponseType(typeof(WaterQualityDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<WaterQualityDto>> GetLatestByPond(int pondId)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var result = await _waterQualityService.GetLatestByPondAsync(pondId, userId);
            return result == null
                ? NotFound(new { message = "No se encontró una medición para el estanque solicitado." })
                : Ok(result);
        }

        [HttpGet("pond/{pondId:int}/summary")]
        [ProducesResponseType(typeof(WaterQualitySummaryDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<WaterQualitySummaryDto>> GetSummaryByPond(int pondId)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var result = await _waterQualityService.GetSummaryByPondAsync(pondId, userId);
            return result == null
                ? NotFound(new { message = "El estanque no existe o no pertenece al usuario." })
                : Ok(result);
        }

        [HttpGet("references")]
        [ProducesResponseType(typeof(IEnumerable<WaterQualityReferenceDto>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<WaterQualityReferenceDto>>> GetReferences()
        {
            if (!TryGetUserId(out _))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            return Ok(await _waterQualityService.GetReferencesAsync());
        }

        [HttpGet("{id:int}")]
        [ProducesResponseType(typeof(WaterQualityDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<WaterQualityDto>> GetById(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var result = await _waterQualityService.GetByIdAsync(id, userId);
            return result == null
                ? NotFound(new { message = "La medición no existe o no pertenece al usuario." })
                : Ok(result);
        }

        [HttpPost]
        [ProducesResponseType(typeof(WaterQualityDto), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<WaterQualityDto>> Create(
            [FromBody] CreateWaterQualityDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (created, errors) = await _waterQualityService.CreateAsync(dto, userId);
            if (errors.Count > 0)
            {
                if (created == null && errors.Any(error =>
                    error.Contains("no existe", StringComparison.OrdinalIgnoreCase)
                    || error.Contains("no pertenece", StringComparison.OrdinalIgnoreCase)
                    || error.Contains("no tiene", StringComparison.OrdinalIgnoreCase)))
                {
                    return NotFound(new { message = errors[0] });
                }

                return BadRequest(new { message = "Los datos de la medición no son válidos.", errors });
            }

            if (created == null)
                return NotFound(new { message = "La medición no pudo ser registrada." });

            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }

        [HttpPut("{id:int}")]
        [ProducesResponseType(typeof(WaterQualityDto), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<WaterQualityDto>> Update(
            int id,
            [FromBody] UpdateWaterQualityDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (updated, errors) = await _waterQualityService.UpdateAsync(id, dto, userId);
            if (errors.Count > 0)
            {
                if (errors.Any(error => error.Contains("no existe", StringComparison.OrdinalIgnoreCase)))
                    return NotFound(new { message = errors[0] });

                return BadRequest(new { message = "Los datos de la medición no son válidos.", errors });
            }

            return updated == null
                ? NotFound(new { message = "La medición no existe o no pertenece al usuario." })
                : Ok(updated);
        }

        [HttpDelete("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> Delete(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            return await _waterQualityService.DeleteAsync(id, userId)
                ? NoContent()
                : NotFound(new { message = "La medición no existe o no pertenece al usuario." });
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
