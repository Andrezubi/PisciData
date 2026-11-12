package ucb.piscidata.farm.data.repository

import ucb.piscidata.farm.data.datasource.FarmRemoteDataSource
import ucb.piscidata.farm.domain.model.Farm
import ucb.piscidata.farm.domain.repository.FarmRepository

class FarmRepositoryImpl(
    private val dataSource: FarmRemoteDataSource
) : FarmRepository {
    override suspend fun getMyFarms(): Result<List<Farm>> {
        return dataSource.getMyFarms()
    }

    override suspend fun createFarm(
        ownerUserId: Int,
        name: String,
        description: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?
    ): Result<Farm> {
        return dataSource.createFarm(
            ownerUserId = ownerUserId,
            name = name,
            description = description,
            address = address,
            latitude = latitude,
            longitude = longitude
        )
    }
}
