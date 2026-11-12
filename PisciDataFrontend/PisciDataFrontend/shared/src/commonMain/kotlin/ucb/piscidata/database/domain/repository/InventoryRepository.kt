package ucb.piscidata.database.domain.repository

import ucb.piscidata.database.domain.model.InvItem

interface InventoryRepository {
    suspend fun getInventory(): Result<List<InvItem>>
    suspend fun createFeed(brand: String, productName: String?, phase: String?, proteinPercentage: Double?, pelletSizeMm: Double?, stockKg: Double?, minimumStockKg: Double?): Result<Unit>
    suspend fun createSupply(name: String, category: String?, quantity: Double, unit: String, minimumStock: Double?): Result<Unit>
    suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit>
}
