using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
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
            var supplies = await _supplyService.GetAllAsync();
            return Ok(supplies);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<SupplyDto>> GetById(int id)
        {
            var supply = await _supplyService.GetByIdAsync(id);
            if (supply == null)
                return NotFound();

            return Ok(supply);
        }

        [HttpPost]
        public async Task<ActionResult<SupplyDto>> Create([FromBody] CreateSupplyDto dto)
        {
            var (created, errors) = await _supplyService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<SupplyDto>> Update(int id, [FromBody] UpdateSupplyDto dto)
        {
            var (updated, errors) = await _supplyService.UpdateAsync(id, dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _supplyService.DeleteAsync(id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}
