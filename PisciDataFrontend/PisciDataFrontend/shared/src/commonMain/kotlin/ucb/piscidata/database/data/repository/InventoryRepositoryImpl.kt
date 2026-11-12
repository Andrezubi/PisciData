package ucb.piscidata.database.data.repository

import ucb.piscidata.database.data.service.InventoryService
import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.database.domain.repository.InventoryRepository

class InventoryRepositoryImpl(
    private val service: InventoryService
) : InventoryRepository {
    override suspend fun getInventory(): Result<List<InvItem>> = service.getInventory()
    override suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit> = service.updateInventoryQty(id, newQty)
}
