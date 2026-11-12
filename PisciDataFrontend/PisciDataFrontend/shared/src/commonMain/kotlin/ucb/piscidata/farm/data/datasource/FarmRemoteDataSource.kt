package ucb.piscidata.farm.data.datasource

import ucb.piscidata.farm.domain.model.Farm

interface FarmRemoteDataSource {
    suspend fun getMyFarms(): Result<List<Farm>>
    suspend fun createFarm(
        ownerUserId: Int,
        name: String,
        description: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?
    ): Result<Farm>
}
