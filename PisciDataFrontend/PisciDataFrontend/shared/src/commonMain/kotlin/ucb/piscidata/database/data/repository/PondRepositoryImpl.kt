package ucb.piscidata.database.data.repository

import ucb.piscidata.database.data.service.PondService
import ucb.piscidata.database.domain.model.Pond
import ucb.piscidata.database.domain.repository.PondRepository

class PondRepositoryImpl(
    private val service: PondService
) : PondRepository {
    override suspend fun getPonds(): Result<List<Pond>> = service.getPonds()
}
