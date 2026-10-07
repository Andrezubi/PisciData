package ucb.piscidata.auth.data.service

import ucb.piscidata.auth.data.datasource.AuthRemoteDataSource
import ucb.piscidata.auth.domain.model.UserModel

class AuthService : AuthRemoteDataSource {
    override suspend fun login(phone: String, pass: String): Result<UserModel> {
        return try {
            // Mock authentication matching reference app credentials (phone 76424237)
            kotlinx.coroutines.delay(500)
            if (phone.isNotBlank()) {
                Result.success(
                    UserModel(
                        name = "Carlos Mendoza",
                        phone = phone,
                        role = "Administrador",
                        farmName = "Piscigranja El Manantial"
                    )
                )
            } else {
                Result.failure(Exception("Número de teléfono inválido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
