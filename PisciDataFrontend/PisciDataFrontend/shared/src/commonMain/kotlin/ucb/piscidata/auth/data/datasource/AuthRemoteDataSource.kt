package ucb.piscidata.auth.data.datasource

import ucb.piscidata.auth.domain.model.UserModel

interface AuthRemoteDataSource {
    suspend fun login(phone: String, pass: String): Result<UserModel>
}
