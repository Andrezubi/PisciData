package ucb.piscidata.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val phone: String,
    val password: String
)

@Serializable
data class CreateUserDto(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val password: String,
    val role: String
)

@Serializable
data class UserResponseDto(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val role: String = "Owner"
)

@Serializable
data class LoginResponseDto(
    val token: String
)
