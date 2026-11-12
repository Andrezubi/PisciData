package ucb.piscidata.profile.data.mapper

import ucb.piscidata.profile.data.dto.UserProfileDto
import ucb.piscidata.profile.domain.model.UserProfileModel

fun UserProfileDto.toModel(): UserProfileModel {
    return UserProfileModel(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        role = role,
        createdAt = createdAt
    )
}
