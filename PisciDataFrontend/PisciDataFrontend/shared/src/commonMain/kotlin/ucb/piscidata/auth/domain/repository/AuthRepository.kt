package ucb.piscidata.auth.domain.repository

import ucb.piscidata.auth.domain.model.UserModel

interface AuthRepository {
    suspend fun login(phone: String, pass: String): Result<UserModel>
    suspend fun register(firstName: String, lastName: String, phone: String, pass: String, role: String): Result<UserModel>
}
