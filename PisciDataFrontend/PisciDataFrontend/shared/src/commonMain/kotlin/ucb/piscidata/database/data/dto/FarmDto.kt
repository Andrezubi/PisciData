package ucb.piscidata.database.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FarmResponseDto(
    val id: Int,
    val name: String,
    val description: String? = null
)
