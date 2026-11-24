package ucb.piscidata.database.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class WaterQualityDetailResponseDto(
    val id: Int,
    val productionCycleId: Int,
    val pondId: Int? = null,
    val pondCode: String? = null,
    val pondName: String? = null,
    val measurementDate: String,
    val measurementTime: String,
    val transparencyCm: Double? = null,
    val temperatureC: Double? = null,
    val ph: Double? = null,
    val dissolvedOxygen: Double? = null,
    val observations: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isActive: Boolean? = null
)

@Serializable
data class CreateWaterQualityRequestDto(
    val pondId: Int,
    val productionCycleId: Int,
    val measurementDate: String,
    val measurementTime: String,
    val transparencyCm: Double? = null,
    val temperatureC: Double? = null,
    val ph: Double? = null,
    val dissolvedOxygen: Double? = null,
    val observations: String? = null
)

@Serializable
data class WaterQualityReferenceResponseDto(
    val id: Int,
    val speciesId: Int? = null,
    val parameter: String,
    val unit: String,
    val optimalMin: Double? = null,
    val optimalMax: Double? = null,
    val warningMin: Double? = null,
    val warningMax: Double? = null,
    val criticalMin: Double? = null,
    val criticalMax: Double? = null,
    val recommendedAction: String? = null,
    val source: String? = null
)
