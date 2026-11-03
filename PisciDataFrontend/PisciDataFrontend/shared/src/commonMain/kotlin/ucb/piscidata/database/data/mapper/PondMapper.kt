package ucb.piscidata.database.data.mapper

import ucb.piscidata.database.data.dto.PondResponseDto
import ucb.piscidata.database.data.dto.ProductionCycleResponseDto
import ucb.piscidata.database.domain.model.Pond
import ucb.piscidata.database.domain.model.PondShape

fun PondResponseDto.toModel(activeCycles: List<ProductionCycleResponseDto> = emptyList()): Pond {
    val parsedShape = when (shape?.uppercase()) {
        "CIRCULAR" -> PondShape.CIRCULAR
        "IRREGULAR" -> PondShape.IRREGULAR
        else -> PondShape.RECTANGULAR
    }
    val wq = latestWaterQuality
    val activeCycle = activeCycles.find { it.pondId == id && it.status != "FINALIZADO" }
    return Pond(
        id = id,
        code = code,
        nombre = name ?: code,
        forma = parsedShape,
        largo = length,
        ancho = width,
        diametro = diameter,
        area = area ?: 0.0,
        profundidad = depth ?: 0.0,
        transparenciaCm = wq?.transparencyCm,
        ph = wq?.ph ?: 7.0,
        claridad = if ((wq?.transparencyCm ?: 0.0) > 40.0) "Alta" else "Media",
        temperatura = wq?.temperatureC ?: 24.0,
        oxigeno = wq?.dissolvedOxygen ?: 6.0,
        cicloActivo = activeCycle?.let { "CP-2026-${it.id.toString().padStart(3, '0')}" },
        especie = activeCycle?.speciesName
    )
}
