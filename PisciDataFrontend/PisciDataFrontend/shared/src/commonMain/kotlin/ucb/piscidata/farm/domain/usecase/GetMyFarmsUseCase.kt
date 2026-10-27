package ucb.piscidata.farm.domain.usecase

import ucb.piscidata.farm.domain.model.Farm
import ucb.piscidata.farm.domain.repository.FarmRepository

class GetMyFarmsUseCase(
    private val repository: FarmRepository
) {
    suspend operator fun invoke(): Result<List<Farm>> {
        return repository.getMyFarms()
    }
}
