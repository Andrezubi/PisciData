package ucb.piscidata.database.domain.usecase

import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.database.domain.repository.InventoryRepository

class GetInventoryUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(): Result<List<InvItem>> = repository.getInventory()
}

class CreateFeedUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(
        brand: String, productName: String?, phase: String?, proteinPercentage: Double?, pelletSizeMm: Double?, stockKg: Double?, minimumStockKg: Double?
    ): Result<Unit> = repository.createFeed(brand, productName, phase, proteinPercentage, pelletSizeMm, stockKg, minimumStockKg)
}

class CreateSupplyUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(
        name: String, category: String?, quantity: Double, unit: String, minimumStock: Double?
    ): Result<Unit> = repository.createSupply(name, category, quantity, unit, minimumStock)
}

class UpdateInventoryQtyUseCase(private val repository: InventoryRepository) {
    suspend operator fun invoke(id: Int, newQty: Int): Result<Unit> = repository.updateInventoryQty(id, newQty)
}
