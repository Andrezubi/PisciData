using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Application.Services;

namespace PisciDataBackend.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class TasksController : ControllerBase
    {
        private readonly TaskService _taskService;

        public TasksController(TaskService taskService)
        {
            _taskService = taskService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<TaskDto>>> GetAll()
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            return Ok(await _taskService.GetAllAsync(userId));
        }

        [HttpGet("farm/{farmId:int}")]
        public async Task<ActionResult<IEnumerable<TaskDto>>> GetByFarm(int farmId)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var tasks = await _taskService.GetByFarmAsync(farmId, userId);
            if (tasks == null)
                return NotFound(new { message = "La granja no existe o no pertenece al usuario." });

            return Ok(tasks);
        }

        [HttpPost]
        public async Task<ActionResult<TaskDto>> Create([FromBody] CreateTaskDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (created, errors) = await _taskService.CreateAsync(dto, userId);
            if (errors.Count > 0)
                return BadRequest(errors);

            return CreatedAtAction(nameof(GetAll), new { id = created!.Id }, created);
        }

        [HttpPut("{id:int}")]
        public async Task<ActionResult<TaskDto>> Update(int id, [FromBody] UpdateTaskDto dto)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var (updated, errors) = await _taskService.UpdateAsync(id, dto, userId);
            if (errors.Count > 0)
                return BadRequest(errors);

            return updated == null ? NotFound() : Ok(updated);
        }

        [HttpPatch("{id:int}/toggle")]
        public async Task<ActionResult<TaskDto>> Toggle(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var toggled = await _taskService.ToggleAsync(id, userId);
            return toggled == null ? NotFound() : Ok(toggled);
        }

        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var deleted = await _taskService.DeleteAsync(id, userId);
            return deleted ? NoContent() : NotFound();
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

