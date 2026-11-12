package ucb.piscidata.database.domain.usecase

import ucb.piscidata.database.domain.model.Pond
import ucb.piscidata.database.domain.repository.PondRepository

class GetPondsUseCase(private val repository: PondRepository) {
    suspend operator fun invoke(): Result<List<Pond>> = repository.getPonds()
}

class CreatePondUseCase(private val repository: PondRepository) {
    suspend operator fun invoke(
        code: String, name: String?, shape: String?, width: Double?, length: Double?, diameter: Double?, depth: Double?, area: Double?, description: String?
    ): Result<Unit> = repository.createPond(code, name, shape, width, length, diameter, depth, area, description)
}
