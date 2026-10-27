package ucb.piscidata.farm.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FarmDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
