package ucb.piscidata.database.presentation.viewmodel

import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.InvCategory

sealed interface DatabaseEvent {
    object LoadData : DatabaseEvent
    data class ChangeTab(val tab: DbTab) : DatabaseEvent
    data class ChangeView(val view: DbView) : DatabaseEvent
    data class Search(val query: String) : DatabaseEvent
    data class FilterInventory(val category: InvCategory?) : DatabaseEvent
    data class SaveCiclo(val ciclo: CicloRecord) : DatabaseEvent
    data class DeleteCiclo(val id: String) : DatabaseEvent
    data class UpdateInventoryQty(val id: Int, val newQty: Int) : DatabaseEvent
}
