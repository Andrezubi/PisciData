package ucb.piscidata.database.presentation.viewmodel

import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.InvCategory

sealed interface DatabaseEvent {
    object LoadData : DatabaseEvent
    object ClickNuevo : DatabaseEvent
    data class ChangeFarm(val farmId: Int) : DatabaseEvent
    data class ChangeTab(val tab: DbTab) : DatabaseEvent
    data class ChangeView(val view: DbView) : DatabaseEvent
    data class Search(val query: String) : DatabaseEvent
    data class FilterInventory(val category: InvCategory?) : DatabaseEvent
    data class ToggleShowInventoryTypeDialog(val show: Boolean) : DatabaseEvent

    data class CreateProductionCycle(
        val pondId: Int, val speciesId: Int, val startDate: String,
        val initialFishCount: Int?, val initialAverageWeightGrams: Double?,
        val initialAgeDays: Int?, val observations: String?
    ) : DatabaseEvent

    data class PromptFinalizeCycle(val ciclo: CicloRecord?) : DatabaseEvent
    data class ConfirmFinalizeCycle(val ciclo: CicloRecord) : DatabaseEvent

    data class CreatePond(
        val code: String, val name: String?, val shape: String?,
        val width: Double?, val length: Double?, val diameter: Double?,
        val depth: Double?, val area: Double?, val description: String?
    ) : DatabaseEvent

    data class CreateFeed(
        val brand: String, val productName: String?, val phase: String?,
        val proteinPercentage: Double?, val pelletSizeMm: Double?,
        val stockKg: Double?, val minimumStockKg: Double?
    ) : DatabaseEvent

    data class CreateSupply(
        val name: String, val category: String?, val quantity: Double,
        val unit: String, val minimumStock: Double?
    ) : DatabaseEvent

    data class SaveCiclo(val ciclo: CicloRecord) : DatabaseEvent
    data class DeleteCiclo(val id: String) : DatabaseEvent
    data class ToggleCicloExpand(val id: String) : DatabaseEvent
    data class UpdateInventoryQty(val id: Int, val newQty: Int) : DatabaseEvent
}
