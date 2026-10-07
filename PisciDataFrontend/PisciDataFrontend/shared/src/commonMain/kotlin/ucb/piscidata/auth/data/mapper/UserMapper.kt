package ucb.piscidata.auth.data.mapper

import ucb.piscidata.auth.data.dto.UserDto
import ucb.piscidata.auth.domain.model.UserModel

fun UserDto.toModel(): UserModel {
    return UserModel(
        name = name,
        phone = phone,
        role = role,
        farmName = farmName
    )
}
