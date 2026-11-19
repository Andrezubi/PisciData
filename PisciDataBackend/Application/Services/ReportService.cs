using Microsoft.EntityFrameworkCore;
using PisciDataBackend.Application.DTOs;
using PisciDataBackend.Domain.Models;

namespace PisciDataBackend.Application.Services
{
    public class ReportService
    {
        private readonly PiscidatadbContext _context;

        public ReportService(PiscidatadbContext context)
        {
            _context = context;
        }

        public async Task<FarmReportDto?> GetFarmReportAsync(int farmId, int userId)
        {
            var farm = await _context.Farms
                .AsNoTracking()
                .FirstOrDefaultAsync(f => f.Id == farmId && f.OwnerUserId == userId && f.IsActive == true);

            if (farm == null)
                return null;

            var ponds = await _context.Ponds
                .AsNoTracking()
                .Where(p => p.FarmId == farmId && (p.IsActive == null || p.IsActive == true))
                .ToListAsync();

            var pondIds = ponds.Select(p => p.Id).ToList();

            var cycles = await _context.Productioncycles
                .AsNoTracking()
                .Include(c => c.Pond)
                .Include(c => c.Species)
                .Where(c => pondIds.Contains(c.PondId) && (c.IsActive == null || c.IsActive == true))
                .ToListAsync();

            var activeCycles = cycles.Where(c => c.EndDate == null).ToList();
            var completedCyclesCount = cycles.Count(c => c.EndDate != null);
            var activeCycleIds = activeCycles.Select(c => c.Id).ToList();

            var biometrics = await _context.Biometrics
                .AsNoTracking()
                .Where(b => activeCycleIds.Contains(b.ProductionCycleId) && (b.IsActive == null || b.IsActive == true))
                .OrderByDescending(b => b.MeasurementDate)
                .ThenByDescending(b => b.Id)
                .ToListAsync();

            var mortalities = await _context.Mortalities
                .AsNoTracking()
                .Where(m => activeCycleIds.Contains(m.ProductionCycleId) && (m.IsActive == null || m.IsActive == true))
                .ToListAsync();

            var feedings = await _context.Feedings
                .AsNoTracking()
                .Where(f => activeCycleIds.Contains(f.ProductionCycleId) && (f.IsActive == null || f.IsActive == true))
                .ToListAsync();

            var feeds = await _context.Feeds
                .AsNoTracking()
                .Where(f => f.FarmId == farmId && (f.IsActive == null || f.IsActive == true))
                .ToListAsync();

            var supplies = await _context.Supplies
                .AsNoTracking()
                .Where(s => s.FarmId == farmId && (s.IsActive == null || s.IsActive == true))
                .ToListAsync();

            var tasks = await _context.Tasks
                .AsNoTracking()
                .Where(t => t.FarmId == farmId && (t.IsActive == null || t.IsActive == true))
                .ToListAsync();

            var latestWater = await _context.Waterqualities
                .AsNoTracking()
                .Where(w => activeCycleIds.Contains(w.ProductionCycleId) && (w.IsActive == null || w.IsActive == true))
                .OrderByDescending(w => w.MeasurementDate)
                .ThenByDescending(w => w.MeasurementTime)
                .FirstOrDefaultAsync();

            var totalInitialFish = activeCycles.Sum(c => c.InitialFishCount ?? 0);
            var totalMortality = mortalities.Sum(m => m.DeadFishCount);
            var totalActiveFish = Math.Max(0, totalInitialFish - totalMortality);
            var mortalityRate = totalInitialFish > 0
                ? Math.Round((decimal)totalMortality / totalInitialFish * 100m, 2)
                : 0m;

            decimal totalBiomassKg = 0m;
            decimal totalInitialBiomassKg = 0m;
            var cyclePerformances = new List<CyclePerformanceDto>();

            foreach (var cycle in activeCycles)
            {
                var latestBio = biometrics.FirstOrDefault(b => b.ProductionCycleId == cycle.Id);
                var cycleDead = mortalities.Where(m => m.ProductionCycleId == cycle.Id).Sum(m => m.DeadFishCount);
                var initialCount = cycle.InitialFishCount ?? 0;
                var aliveCount = Math.Max(0, initialCount - cycleDead);
                var initialWeightG = cycle.InitialAverageWeightGrams ?? 0m;
                var latestWeightG = latestBio?.AverageWeightGrams ?? initialWeightG;
                var cycleBiomassKg = latestBio?.BiomassKg
                    ?? Math.Round(aliveCount * latestWeightG / 1000m, 2);

                totalBiomassKg += cycleBiomassKg;
                totalInitialBiomassKg += Math.Round(initialCount * initialWeightG / 1000m, 2);

                cyclePerformances.Add(new CyclePerformanceDto
                {
                    CycleId = cycle.Id,
                    PondCode = cycle.Pond?.Code ?? $"E-{cycle.PondId}",
                    PondName = cycle.Pond?.Name ?? "Estanque",
                    SpeciesName = cycle.Species?.CommonName ?? "Especie",
                    InitialFishCount = initialCount,
                    DeadFishCount = cycleDead,
                    LatestAverageWeightGrams = Math.Round(latestWeightG, 1),
                    EstimatedBiomassKg = Math.Round(cycleBiomassKg, 2),
                    HealthStatus = latestBio?.HealthStatus ?? "Sin biometría"
                });
            }

            var totalFeedKg = Math.Round(feedings.Sum(f => f.QuantityKg), 2);
            var biomassGainKg = totalBiomassKg - totalInitialBiomassKg;
            decimal? estimatedFcr = (totalFeedKg > 0 && biomassGainKg > 0)
                ? Math.Round(totalFeedKg / biomassGainKg, 2)
                : null;

            var lowFeeds = feeds.Count(f => f.MinimumStockKg.HasValue && (f.StockKg ?? 0m) <= f.MinimumStockKg.Value);
            var lowSupplies = supplies.Count(s => s.MinimumStock.HasValue && s.Quantity <= s.MinimumStock.Value);
            var lowStockAlerts = lowFeeds + lowSupplies;

            var pendingTasks = tasks.Count(t => !t.Completed);
            var completedTasks = tasks.Count(t => t.Completed);

            var insight = BuildInsight(
                activeCycles.Count,
                mortalityRate,
                lowStockAlerts,
                pendingTasks,
                latestWater?.DissolvedOxygen,
                estimatedFcr);

            return new FarmReportDto
            {
                FarmId = farm.Id,
                FarmName = farm.Name,
                ActiveCyclesCount = activeCycles.Count,
                CompletedCyclesCount = completedCyclesCount,
                TotalPondsCount = ponds.Count,
                TotalActiveFishCount = totalActiveFish,
                EstimatedBiomassKg = Math.Round(totalBiomassKg, 2),
                TotalFeedConsumedKg = totalFeedKg,
                TotalMortalityCount = totalMortality,
                MortalityRatePercent = mortalityRate,
                EstimatedFcr = estimatedFcr,
                LowStockAlertsCount = lowStockAlerts,
                PendingTasksCount = pendingTasks,
                CompletedTasksCount = completedTasks,
                LatestOxygenMgL = latestWater?.DissolvedOxygen,
                LatestPh = latestWater?.Ph,
                LatestTemperatureC = latestWater?.TemperatureC,
                InsightMessage = insight,
                ActiveCycles = cyclePerformances
            };
        }

        private static string BuildInsight(
            int activeCycles,
            decimal mortalityRate,
            int lowStockAlerts,
            int pendingTasks,
            decimal? latestOxygen,
            decimal? fcr)
        {
            if (activeCycles == 0)
                return "Aún no hay ciclos productivos activos en esta granja. Registra una siembra en Base de Datos para comenzar a medir biomasa y rendimiento.";

            if (latestOxygen.HasValue && latestOxygen.Value < 4.5m)
                return $"Atención: el último registro de oxígeno disuelto ({latestOxygen.Value:0.0} mg/L) está por debajo del rango óptimo. Revisa la aireación y evita sobrealimentar.";

            if (lowStockAlerts > 0)
                return $"Tienes {lowStockAlerts} alerta(s) de stock bajo en inventario y {pendingTasks} tarea(s) pendiente(s). Planifica reposición de alimento para no interrumpir raciones.";

            if (mortalityRate > 5m)
                return $"La mortalidad acumulada ({mortalityRate:0.0}%) supera el umbral recomendado del 5%. Conviene revisar calidad de agua y realizar una biometría de control.";

            var fcrText = fcr.HasValue ? $" con un FCR estimado de {fcr.Value:0.00}" : "";
            return $"Buen ritmo productivo: {activeCycles} ciclo(s) activo(s) con mortalidad controlada ({mortalityRate:0.0}%){fcrText}. Mantén las raciones y controles al día.";
        }
    }
}

