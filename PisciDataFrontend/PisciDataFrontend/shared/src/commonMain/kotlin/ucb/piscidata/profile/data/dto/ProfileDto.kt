package ucb.piscidata.profile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val role: String,
    val createdAt: String? = null
)

@Serializable
data class UpdateUserRequestDto(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val role: String
)
