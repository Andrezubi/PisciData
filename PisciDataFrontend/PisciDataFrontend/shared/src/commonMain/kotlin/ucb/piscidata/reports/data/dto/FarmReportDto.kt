package ucb.piscidata.reports.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FarmReportDto(
    val farmId: Int = 0,
    val farmName: String = "",
    val activeCyclesCount: Int = 0,
    val completedCyclesCount: Int = 0,
    val totalPondsCount: Int = 0,
    val totalActiveFishCount: Int = 0,
    val estimatedBiomassKg: Double = 0.0,
    val totalFeedConsumedKg: Double = 0.0,
    val totalMortalityCount: Int = 0,
    val mortalityRatePercent: Double = 0.0,
    val estimatedFcr: Double? = null,
    val lowStockAlertsCount: Int = 0,
    val pendingTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val latestOxygenMgL: Double? = null,
    val latestPh: Double? = null,
    val latestTemperatureC: Double? = null,
    val insightMessage: String = "",
    val activeCycles: List<CyclePerformanceDto> = emptyList()
)

@Serializable
data class CyclePerformanceDto(
    val cycleId: Int = 0,
    val pondCode: String = "",
    val pondName: String = "",
    val speciesName: String = "",
    val initialFishCount: Int = 0,
    val deadFishCount: Int = 0,
    val latestAverageWeightGrams: Double = 0.0,
    val estimatedBiomassKg: Double = 0.0,
    val healthStatus: String = "Sin biometría"
)

