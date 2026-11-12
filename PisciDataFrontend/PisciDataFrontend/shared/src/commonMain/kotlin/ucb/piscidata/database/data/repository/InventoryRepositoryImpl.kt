package ucb.piscidata.database.data.repository

import ucb.piscidata.database.data.service.InventoryService
import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.database.domain.repository.InventoryRepository

class InventoryRepositoryImpl(
    private val service: InventoryService
) : InventoryRepository {
    override suspend fun getInventory(): Result<List<InvItem>> = service.getInventory()
    override suspend fun createFeed(
        brand: String, productName: String?, phase: String?, proteinPercentage: Double?, pelletSizeMm: Double?, stockKg: Double?, minimumStockKg: Double?
    ): Result<Unit> = service.createFeed(brand, productName, phase, proteinPercentage, pelletSizeMm, stockKg, minimumStockKg)

    override suspend fun createSupply(
        name: String, category: String?, quantity: Double, unit: String, minimumStock: Double?
    ): Result<Unit> = service.createSupply(name, category, quantity, unit, minimumStock)

    override suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit> = service.updateInventoryQty(id, newQty)
}
