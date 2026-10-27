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
}
