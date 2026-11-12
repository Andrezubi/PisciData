package ucb.piscidata.profile.domain.repository

import ucb.piscidata.profile.domain.model.UserProfileModel

interface ProfileRepository {
    suspend fun getUserProfile(id: Int): Result<UserProfileModel>
    suspend fun updateUserProfile(id: Int, firstName: String, lastName: String, phone: String, role: String): Result<UserProfileModel>
}
