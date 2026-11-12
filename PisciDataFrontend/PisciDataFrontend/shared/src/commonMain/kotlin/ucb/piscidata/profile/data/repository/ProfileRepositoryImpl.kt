package ucb.piscidata.profile.data.repository

import ucb.piscidata.profile.data.datasource.ProfileRemoteDataSource
import ucb.piscidata.profile.domain.model.UserProfileModel
import ucb.piscidata.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val dataSource: ProfileRemoteDataSource
) : ProfileRepository {
    override suspend fun getUserProfile(id: Int): Result<UserProfileModel> = dataSource.getUserProfile(id)
    override suspend fun updateUserProfile(id: Int, firstName: String, lastName: String, phone: String, role: String): Result<UserProfileModel> =
        dataSource.updateUserProfile(id, firstName, lastName, phone, role)
}
