package ucb.piscidata.database.data.mapper

import ucb.piscidata.database.data.dto.ProductionCycleResponseDto
import ucb.piscidata.database.domain.model.BadgeVariant
import ucb.piscidata.database.domain.model.CicloRecord

fun ProductionCycleResponseDto.toRecord(): CicloRecord {
    val badge = when (status) {
        "ACTIVO" -> BadgeVariant.ACTIVO
        "EN_SEGUIMIENTO" -> BadgeVariant.EN_SEGUIMIENTO
        else -> BadgeVariant.FINALIZADO
    }
    return CicloRecord(
        id = "CP-2026-${id.toString().padStart(3, '0')}",
        status = badge,
        estanque = "$pondCode - ${pondName ?: ""}".trim(),
        especie = speciesName,
        peces = currentFishCount?.toString() ?: "0",
        pesoActual = "${currentAverageWeightGrams ?: 0.0} g",
        edadDias = currentAgeDays ?: 0,
        startDate = startDate,
        endDate = endDate,
        initialAgeDays = initialAgeDays ?: 0,
        initialWeightGrams = "${initialAverageWeightGrams ?: 0.0} g",
        initialFishCount = initialFishCount?.toString() ?: "0",
        observations = observations ?: "",
        isExpanded = false
    )
}
