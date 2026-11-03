package ucb.piscidata.database.data.mapper

import ucb.piscidata.database.data.dto.FeedResponseDto
import ucb.piscidata.database.data.dto.SupplyResponseDto
import ucb.piscidata.database.domain.model.InvCategory
import ucb.piscidata.database.domain.model.InvItem

fun FeedResponseDto.toModel(): InvItem {
    return InvItem(
        id = id,
        name = "$brand ${productName ?: phase ?: ""}".trim(),
        category = InvCategory.ALIMENTO,
        qty = stockKg?.toInt() ?: 0,
        unit = "kg",
        minQty = minimumStockKg?.toInt() ?: 0,
        proteinPercentage = proteinPercentage ?: 40.0,
        pelletSizeMm = pelletSizeMm ?: 2.0
    )
}

fun SupplyResponseDto.toModel(): InvItem {
    val cat = when (category?.lowercase()) {
        "quimico", "chemical" -> InvCategory.QUIMICO
        "combustible", "fuel" -> InvCategory.COMBUSTIBLE
        else -> InvCategory.EQUIPO
    }
    return InvItem(
        id = id + 10000,
        name = name,
        category = cat,
        qty = quantity.toInt(),
        unit = unit,
        minQty = minimumStock?.toInt() ?: 0,
        proteinPercentage = null,
        pelletSizeMm = null
    )
}
