package ucb.piscidata.farm.domain.repository

import ucb.piscidata.farm.domain.model.Farm

interface FarmRepository {
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
