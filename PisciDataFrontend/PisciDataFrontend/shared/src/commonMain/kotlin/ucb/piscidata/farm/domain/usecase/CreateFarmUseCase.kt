package ucb.piscidata.farm.domain.usecase

import ucb.piscidata.farm.domain.model.Farm
import ucb.piscidata.farm.domain.repository.FarmRepository

class CreateFarmUseCase(
    private val repository: FarmRepository
) {
    suspend operator fun invoke(
        ownerUserId: Int,
        name: String,
        description: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?
    ): Result<Farm> {
        return repository.createFarm(
            ownerUserId = ownerUserId,
            name = name,
            description = description,
            address = address,
            latitude = latitude,
            longitude = longitude
        )
    }
}
