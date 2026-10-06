using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FarmsController : ControllerBase
    {
        private readonly FarmService _farmService;

        public FarmsController(FarmService farmService)
        {
            _farmService = farmService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<FarmDto>>> GetAll()
        {
            var farms = await _farmService.GetAllAsync();
            return Ok(farms);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<FarmDto>> GetById(int id)
        {
            var farm = await _farmService.GetByIdAsync(id);
            if (farm == null)
                return NotFound();

            return Ok(farm);
        }

        [HttpPost]
        public async Task<ActionResult<FarmDto>> Create([FromBody] CreateFarmDto dto)
        {
            var (created, errors) = await _farmService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<FarmDto>> Update(int id, [FromBody] UpdateFarmDto dto)
        {
            var (updated, errors) = await _farmService.UpdateAsync(id, dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _farmService.DeleteAsync(id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}
