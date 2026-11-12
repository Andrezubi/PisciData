package ucb.piscidata.database.domain.usecase

import ucb.piscidata.database.domain.model.FarmModel
import ucb.piscidata.database.domain.repository.FarmLookupRepository

class GetFarmsUseCase(private val repository: FarmLookupRepository) {
    suspend operator fun invoke(): Result<List<FarmModel>> = repository.getFarms()
}
