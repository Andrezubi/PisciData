package ucb.piscidata.database.domain.repository

import ucb.piscidata.database.domain.model.Pond

interface PondRepository {
    suspend fun getPonds(): Result<List<Pond>>
}
