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
