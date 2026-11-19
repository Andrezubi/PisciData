namespace PisciDataBackend.Application.DTOs
{
    public class FarmReportDto
    {
        public int FarmId { get; set; }
        public string FarmName { get; set; } = string.Empty;
        public int ActiveCyclesCount { get; set; }
        public int CompletedCyclesCount { get; set; }
        public int TotalPondsCount { get; set; }
        public int TotalActiveFishCount { get; set; }
        public decimal EstimatedBiomassKg { get; set; }
        public decimal TotalFeedConsumedKg { get; set; }
        public int TotalMortalityCount { get; set; }
        public decimal MortalityRatePercent { get; set; }
        public decimal? EstimatedFcr { get; set; }
        public int LowStockAlertsCount { get; set; }
        public int PendingTasksCount { get; set; }
        public int CompletedTasksCount { get; set; }
        public decimal? LatestOxygenMgL { get; set; }
        public decimal? LatestPh { get; set; }
        public decimal? LatestTemperatureC { get; set; }
        public string InsightMessage { get; set; } = string.Empty;
        public List<CyclePerformanceDto> ActiveCycles { get; set; } = new();
    }

    public class CyclePerformanceDto
    {
        public int CycleId { get; set; }
        public string PondCode { get; set; } = string.Empty;
        public string PondName { get; set; } = string.Empty;
        public string SpeciesName { get; set; } = string.Empty;
        public int InitialFishCount { get; set; }
        public int DeadFishCount { get; set; }
        public decimal LatestAverageWeightGrams { get; set; }
        public decimal EstimatedBiomassKg { get; set; }
        public string HealthStatus { get; set; } = "Sin biometría";
    }
}

