package ucb.piscidata.farm.data.datasource

import ucb.piscidata.farm.domain.model.Farm

interface FarmRemoteDataSource {
    suspend fun getMyFarms(): Result<List<Farm>>
}
