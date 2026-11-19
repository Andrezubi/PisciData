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
    public class ReportsController : ControllerBase
    {
        private readonly ReportService _reportService;

        public ReportsController(ReportService reportService)
        {
            _reportService = reportService;
        }

        [HttpGet("farm/{farmId:int}")]
        public async Task<ActionResult<FarmReportDto>> GetFarmReport(int farmId)
        {
            if (!TryGetUserId(out var userId))
                return Unauthorized(new { message = "No se pudo identificar al usuario." });

            var report = await _reportService.GetFarmReportAsync(farmId, userId);
            if (report == null)
                return NotFound(new { message = "La granja no existe o no pertenece al usuario." });

            return Ok(report);
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

