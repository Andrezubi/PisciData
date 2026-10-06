using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ProductioncyclesController : ControllerBase
    {
        private readonly ProductioncycleService _productioncycleService;

        public ProductioncyclesController(ProductioncycleService productioncycleService)
        {
            _productioncycleService = productioncycleService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<ProductioncycleDto>>> GetAll()
        {
            var productioncycles = await _productioncycleService.GetAllAsync();
            return Ok(productioncycles);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ProductioncycleDto>> GetById(int id)
        {
            var productioncycle = await _productioncycleService.GetByIdAsync(id);
            if (productioncycle == null)
                return NotFound();

            return Ok(productioncycle);
        }

        [HttpPost]
        public async Task<ActionResult<ProductioncycleDto>> Create([FromBody] CreateProductioncycleDto dto)
        {
            var (created, errors) = await _productioncycleService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<ProductioncycleDto>> Update(int id, [FromBody] UpdateProductioncycleDto dto)
        {
            var (updated, errors) = await _productioncycleService.UpdateAsync(id, dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _productioncycleService.DeleteAsync(id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}
