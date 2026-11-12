package ucb.piscidata.profile.data.datasource

import ucb.piscidata.profile.domain.model.UserProfileModel

interface ProfileRemoteDataSource {
    suspend fun getUserProfile(id: Int): Result<UserProfileModel>
    suspend fun updateUserProfile(id: Int, firstName: String, lastName: String, phone: String, role: String): Result<UserProfileModel>
}
