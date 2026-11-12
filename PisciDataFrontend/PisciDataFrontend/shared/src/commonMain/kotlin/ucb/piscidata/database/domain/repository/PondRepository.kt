package ucb.piscidata.database.domain.repository

import ucb.piscidata.database.domain.model.Pond

interface PondRepository {
    suspend fun getPonds(): Result<List<Pond>>
    suspend fun createPond(code: String, name: String?, shape: String?, width: Double?, length: Double?, diameter: Double?, depth: Double?, area: Double?, description: String?): Result<Unit>
}
