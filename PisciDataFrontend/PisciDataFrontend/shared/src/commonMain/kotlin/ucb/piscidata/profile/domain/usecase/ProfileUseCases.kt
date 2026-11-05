package ucb.piscidata.profile.domain.usecase

import ucb.piscidata.profile.domain.model.UserProfileModel
import ucb.piscidata.profile.domain.repository.ProfileRepository

class GetUserProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(id: Int): Result<UserProfileModel> = repository.getUserProfile(id)
}

class UpdateUserProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(id: Int, firstName: String, lastName: String, phone: String, role: String): Result<UserProfileModel> =
        repository.updateUserProfile(id, firstName, lastName, phone, role)
}
