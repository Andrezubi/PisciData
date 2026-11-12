package ucb.piscidata.database.domain.usecase

import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.database.domain.repository.InventoryRepository

class GetInventoryUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(): Result<List<InvItem>> = repository.getInventory()
}

class UpdateInventoryQtyUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(id: Int, newQty: Int): Result<Unit> = repository.updateInventoryQty(id, newQty)
}
