package ucb.piscidata.database.domain.model

enum class WaterParameterStatus {
    OPTIMAL, WARNING, CRITICAL, UNKNOWN;

    fun label(): String = when (this) {
        OPTIMAL -> "Óptimo"
        WARNING -> "Precaución"
        CRITICAL -> "Crítico"
        UNKNOWN -> "Sin referencia"
    }
}

data class WaterQualityRecord(
    val id: Int,
    val productionCycleId: Int,
    val pondCode: String,
    val pondName: String?,
    val measurementDate: String,
    val measurementTime: String,
    val transparencyCm: Double?,
    val temperatureC: Double?,
    val ph: Double?,
    val dissolvedOxygen: Double?,
    val observations: String?,
    val parameterStatuses: Map<String, WaterParameterStatus> = emptyMap(),
    val parameterActions: Map<String, String> = emptyMap()
)

data class WaterQualityReferenceModel(
    val id: Int,
    val speciesId: Int?,
    val parameter: String,
    val unit: String,
    val optimalMin: Double?,
    val optimalMax: Double?,
    val warningMin: Double?,
    val warningMax: Double?,
    val criticalMin: Double?,
    val criticalMax: Double?,
    val recommendedAction: String?,
    val source: String?
)
