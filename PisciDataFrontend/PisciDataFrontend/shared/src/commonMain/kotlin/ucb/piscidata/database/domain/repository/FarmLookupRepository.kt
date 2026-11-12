package ucb.piscidata.database.domain.repository

import ucb.piscidata.database.domain.model.FarmModel

interface FarmLookupRepository {
    suspend fun getFarms(): Result<List<FarmModel>>
}
