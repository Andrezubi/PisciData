package ucb.piscidata.auth.data.mapper

import ucb.piscidata.auth.data.dto.UserResponseDto
import ucb.piscidata.auth.domain.model.UserModel

fun UserResponseDto.toModel(): UserModel {
    return UserModel(
        name = "$firstName $lastName",
        phone = phone,
        role = role,
        farmName = "Piscigranja El Manantial"
    )
}
