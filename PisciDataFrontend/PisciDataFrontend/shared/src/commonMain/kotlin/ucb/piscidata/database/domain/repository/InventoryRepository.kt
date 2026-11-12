package ucb.piscidata.database.domain.repository

import ucb.piscidata.database.domain.model.InvItem

interface InventoryRepository {
    suspend fun getInventory(): Result<List<InvItem>>
    suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit>
}
