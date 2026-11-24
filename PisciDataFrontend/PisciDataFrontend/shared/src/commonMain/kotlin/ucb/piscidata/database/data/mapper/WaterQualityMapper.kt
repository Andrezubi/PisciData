package ucb.piscidata.database.data.mapper

import ucb.piscidata.database.data.dto.WaterQualityDetailResponseDto
import ucb.piscidata.database.data.dto.WaterQualityReferenceResponseDto
import ucb.piscidata.database.domain.model.WaterParameterStatus
import ucb.piscidata.database.domain.model.WaterQualityRecord
import ucb.piscidata.database.domain.model.WaterQualityReferenceModel

fun WaterQualityDetailResponseDto.toRecord(references: List<WaterQualityReferenceModel> = emptyList()): WaterQualityRecord {
    val statuses = mutableMapOf<String, WaterParameterStatus>()
    val actions = mutableMapOf<String, String>()

    fun evaluate(paramKey: String, value: Double?) {
        if (value == null) {
            statuses[paramKey] = WaterParameterStatus.UNKNOWN
            return
        }
        val ref = references.find { it.parameter.equals(paramKey, true) }
        if (ref == null) {
            val status = when (paramKey.uppercase()) {
                "DISSOLVED_OXYGEN", "OXYGEN" -> when {
                    value < 3.0 -> WaterParameterStatus.CRITICAL
                    value < 5.0 -> WaterParameterStatus.WARNING
                    else -> WaterParameterStatus.OPTIMAL
                }
                "PH" -> when {
                    value < 6.0 || value > 9.0 -> WaterParameterStatus.CRITICAL
                    value < 6.5 || value > 8.5 -> WaterParameterStatus.WARNING
                    else -> WaterParameterStatus.OPTIMAL
                }
                "TEMPERATURE" -> when {
                    value < 20.0 || value > 34.0 -> WaterParameterStatus.WARNING
                    else -> WaterParameterStatus.OPTIMAL
                }
                else -> WaterParameterStatus.OPTIMAL
            }
            statuses[paramKey] = status
            return
        }

        val status = when {
            (ref.criticalMin != null && value <= ref.criticalMin) ||
            (ref.criticalMax != null && value >= ref.criticalMax) -> WaterParameterStatus.CRITICAL
            
            (ref.optimalMin != null && value >= ref.optimalMin) &&
            (ref.optimalMax != null && value <= ref.optimalMax) -> WaterParameterStatus.OPTIMAL
            
            (ref.warningMin != null && value < ref.warningMin) ||
            (ref.warningMax != null && value > ref.warningMax) -> WaterParameterStatus.WARNING
            
            else -> WaterParameterStatus.OPTIMAL
        }
        statuses[paramKey] = status
        if (!ref.recommendedAction.isNullOrBlank()) {
            actions[paramKey] = ref.recommendedAction!!
        }
    }

    evaluate("DISSOLVED_OXYGEN", dissolvedOxygen)
    evaluate("PH", ph)
    evaluate("TEMPERATURE", temperatureC)
    evaluate("TRANSPARENCY", transparencyCm)

    return WaterQualityRecord(
        id = id,
        productionCycleId = productionCycleId,
        pondCode = pondCode ?: "EST-${productionCycleId}",
        pondName = pondName,
        measurementDate = measurementDate,
        measurementTime = measurementTime,
        transparencyCm = transparencyCm,
        temperatureC = temperatureC,
        ph = ph,
        dissolvedOxygen = dissolvedOxygen,
        observations = observations,
        parameterStatuses = statuses,
        parameterActions = actions
    )
}

fun WaterQualityReferenceResponseDto.toModel(): WaterQualityReferenceModel {
    return WaterQualityReferenceModel(
        id = id,
        speciesId = speciesId,
        parameter = parameter,
        unit = unit,
        optimalMin = optimalMin,
        optimalMax = optimalMax,
        warningMin = warningMin,
        warningMax = warningMax,
        criticalMin = criticalMin,
        criticalMax = criticalMax,
        recommendedAction = recommendedAction,
        source = source
    )
}
