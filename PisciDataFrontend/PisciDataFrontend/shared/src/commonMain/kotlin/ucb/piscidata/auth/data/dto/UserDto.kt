package ucb.piscidata.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String,
    val phone: String,
    val role: String,
    val farmName: String
)
