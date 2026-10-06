using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
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
            var ponds = await _pondService.GetAllAsync();
            return Ok(ponds);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<PondDto>> GetById(int id)
        {
            var pond = await _pondService.GetByIdAsync(id);
            if (pond == null)
                return NotFound();

            return Ok(pond);
        }

        [HttpPost]
        public async Task<ActionResult<PondDto>> Create([FromBody] CreatePondDto dto)
        {
            var (created, errors) = await _pondService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<PondDto>> Update(int id, [FromBody] UpdatePondDto dto)
        {
            var (updated, errors) = await _pondService.UpdateAsync(id, dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _pondService.DeleteAsync(id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}
