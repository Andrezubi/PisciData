package ucb.piscidata.farm.domain.repository

import ucb.piscidata.farm.domain.model.Farm

interface FarmRepository {
    suspend fun getMyFarms(): Result<List<Farm>>
}
