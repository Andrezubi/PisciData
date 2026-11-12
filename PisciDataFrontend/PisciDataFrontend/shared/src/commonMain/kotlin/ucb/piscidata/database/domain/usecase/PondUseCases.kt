package ucb.piscidata.database.domain.usecase

import ucb.piscidata.database.domain.model.Pond
import ucb.piscidata.database.domain.repository.PondRepository

class GetPondsUseCase(private val repository: PondRepository) {
    suspend operator fun invoke(): Result<List<Pond>> = repository.getPonds()
}
