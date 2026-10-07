package ucb.piscidata.database.presentation.viewmodel

import ucb.piscidata.database.domain.model.BadgeVariant
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.InvCategory
import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.database.domain.model.Pond

enum class DbTab {
    CICLOS, ESTANQUES, INVENTARIO
}

sealed interface DbView {
    object List : DbView
    object Nuevo : DbView
    data class Editar(val record: CicloRecord) : DbView
}

data class DatabaseState(
    val tab: DbTab = DbTab.CICLOS,
    val view: DbView = DbView.List,
    val ciclos: List<CicloRecord> = emptyList(),
    val ponds: List<Pond> = emptyList(),
    val inventory: List<InvItem> = emptyList(),
    val searchQuery: String = "",
    val inventoryFilter: InvCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
