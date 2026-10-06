using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class BiometricsController : ControllerBase
    {
        private readonly BiometricService _biometricService;

        public BiometricsController(BiometricService biometricService)
        {
            _biometricService = biometricService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<BiometricDto>>> GetAll()
        {
            var biometrics = await _biometricService.GetAllAsync();
            return Ok(biometrics);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<BiometricDto>> GetById(int id)
        {
            var biometric = await _biometricService.GetByIdAsync(id);
            if (biometric == null)
                return NotFound();

            return Ok(biometric);
        }

        [HttpPost]
        public async Task<ActionResult<BiometricDto>> Create([FromBody] CreateBiometricDto dto)
        {
            var (created, errors) = await _biometricService.CreateAsync(dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetById), new { id = created!.Id }, created);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<BiometricDto>> Update(int id, [FromBody] UpdateBiometricDto dto)
        {
            var (updated, errors) = await _biometricService.UpdateAsync(id, dto);
            if (errors.Count > 0)
                return BadRequest(errors);

            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _biometricService.DeleteAsync(id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}
