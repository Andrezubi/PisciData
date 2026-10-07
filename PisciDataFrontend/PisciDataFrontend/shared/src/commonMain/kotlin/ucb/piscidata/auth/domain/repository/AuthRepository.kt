package ucb.piscidata.auth.domain.repository

import ucb.piscidata.auth.domain.model.UserModel

interface AuthRepository {
    suspend fun login(phone: String, pass: String): Result<UserModel>
}
