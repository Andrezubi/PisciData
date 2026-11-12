package ucb.piscidata.database.data.dto

import kotlinx.serialization.Serializable

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
data class CreatePondRequestDto(
    val farmId: Int,
    val code: String,
    val name: String? = null,
    val shape: String? = null,
    val width: Double? = null,
    val length: Double? = null,
    val diameter: Double? = null,
    val depth: Double? = null,
    val area: Double? = null,
    val description: String? = null
)
