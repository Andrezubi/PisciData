package ucb.piscidata.auth.domain.usecase

import ucb.piscidata.auth.domain.model.UserModel
import ucb.piscidata.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(phone: String, pass: String): Result<UserModel> {
        return repository.login(phone, pass)
    }
}
