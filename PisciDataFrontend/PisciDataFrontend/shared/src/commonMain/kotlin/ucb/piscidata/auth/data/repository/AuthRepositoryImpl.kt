package ucb.piscidata.auth.data.repository

import ucb.piscidata.auth.data.datasource.AuthRemoteDataSource
import ucb.piscidata.auth.domain.model.UserModel
import ucb.piscidata.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun login(phone: String, pass: String): Result<UserModel> {
        return dataSource.login(phone, pass)
    }
}
