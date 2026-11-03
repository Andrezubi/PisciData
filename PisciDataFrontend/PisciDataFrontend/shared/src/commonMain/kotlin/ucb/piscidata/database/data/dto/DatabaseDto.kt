package ucb.piscidata.database.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FarmResponseDto(
    val id: Int,
    val name: String,
    val description: String? = null
)

@Serializable
data class ProductionCycleResponseDto(
    val id: Int,
    val pondId: Int,
    val speciesId: Int,
    val startDate: String,
    val endDate: String? = null,
    val initialFishCount: Int? = null,
    val initialAverageWeightGrams: Double? = null,
    val initialAgeDays: Int? = null,
    val observations: String? = null,
    val farmId: Int,
    val farmName: String,
    val pondCode: String,
    val pondName: String? = null,
    val speciesName: String,
    val currentFishCount: Int? = null,
    val currentAverageWeightGrams: Double? = null,
    val currentAgeDays: Int? = null,
    val status: String
)

@Serializable
data class PondResponseDto(
    val id: Int,
    val farmId: Int,
    val code: String,
    val name: String? = null,
    val shape: String? = null,
    val width: Double? = null,
    val length: Double? = null,
    val diameter: Double? = null,
    val depth: Double? = null,
    val area: Double? = null,
    val description: String? = null,
    val latestWaterQuality: WaterQualityResponseDto? = null
)

@Serializable
data class WaterQualityResponseDto(
    val id: Int,
    val transparencyCm: Double? = null,
    val temperatureC: Double? = null,
    val ph: Double? = null,
    val dissolvedOxygen: Double? = null,
    val measurementDate: String? = null
)

@Serializable
data class FeedResponseDto(
    val id: Int,
    val farmId: Int,
    val brand: String,
    val productName: String? = null,
    val phase: String? = null,
    val proteinPercentage: Double? = null,
    val pelletSizeMm: Double? = null,
    val stockKg: Double? = null,
    val minimumStockKg: Double? = null,
    val stockStatus: String? = null
)

@Serializable
data class SupplyResponseDto(
    val id: Int,
    val farmId: Int,
    val name: String,
    val category: String? = null,
    val quantity: Double,
    val unit: String,
    val minimumStock: Double? = null,
    val stockStatus: String? = null
)
