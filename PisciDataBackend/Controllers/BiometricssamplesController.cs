using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class BiometricssamplesController : ControllerBase
    {
        private readonly BiometricssampleService _biometricssampleService;

        public BiometricssamplesController(BiometricssampleService biometricssampleService)
        {
            _biometricssampleService = biometricssampleService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<BiometricssampleDto>>> GetAll()
        {
            var biometricssamples = await _biometricssampleService.GetAllAsync();
            return Ok(biometricssamples);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<BiometricssampleDto>> GetById(int id)
        {
            var biometricssample = await _biometricssampleService.GetByIdAsync(id);
            if (biometricssample == null)
                return NotFound();

            return Ok(biometricssample);
        }

        [HttpPost]
        public async Task<ActionResult<BiometricssampleDto>> Create([FromBody] CreateBiometricssampleDto dto)
        {
            var (created, errors) = await _biometricssampleService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<BiometricssampleDto>> Update(int id, [FromBody] UpdateBiometricssampleDto dto)
        {
            var (updated, errors) = await _biometricssampleService.UpdateAsync(id, dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _biometricssampleService.DeleteAsync(id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}
