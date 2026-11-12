package ucb.piscidata.database.data.dto

import kotlinx.serialization.Serializable

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

@Serializable
data class CreateFeedRequestDto(
    val farmId: Int,
    val brand: String,
    val productName: String? = null,
    val phase: String? = null,
    val proteinPercentage: Double? = null,
    val pelletSizeMm: Double? = null,
    val stockKg: Double? = null,
    val minimumStockKg: Double? = null
)

@Serializable
data class CreateSupplyRequestDto(
    val farmId: Int,
    val name: String,
    val category: String? = null,
    val quantity: Double,
    val unit: String,
    val minimumStock: Double? = null
)
