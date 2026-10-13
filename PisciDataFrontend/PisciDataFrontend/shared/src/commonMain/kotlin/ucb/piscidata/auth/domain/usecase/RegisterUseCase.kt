package ucb.piscidata.auth.domain.usecase

import ucb.piscidata.auth.domain.model.UserModel
import ucb.piscidata.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(firstName: String, lastName: String, phone: String, pass: String, role: String): Result<UserModel> {
        return repository.register(firstName, lastName, phone, pass, role)
    }
}
