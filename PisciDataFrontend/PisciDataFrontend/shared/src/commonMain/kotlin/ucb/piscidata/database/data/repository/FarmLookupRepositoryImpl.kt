package ucb.piscidata.database.data.repository

import ucb.piscidata.database.data.service.FarmLookupService
import ucb.piscidata.database.domain.model.FarmModel
import ucb.piscidata.database.domain.repository.FarmLookupRepository

class FarmLookupRepositoryImpl(
    private val service: FarmLookupService
) : FarmLookupRepository {
    override suspend fun getFarms(): Result<List<FarmModel>> = service.getFarms()
}
