package ucb.piscidata.database.presentation.viewmodel

import ucb.piscidata.database.domain.model.*

enum class DbTab {
    CICLOS, ESTANQUES, INVENTARIO
}

sealed interface DbView {
    object List : DbView
    object NuevoCiclo : DbView
    object NuevoEstanque : DbView
    object NuevoFeed : DbView
    object NuevoSupply : DbView
    data class Editar(val record: CicloRecord) : DbView
}

data class DatabaseState(
    val tab: DbTab = DbTab.CICLOS,
    val view: DbView = DbView.List,
    val farms: List<FarmModel> = emptyList(),
    val selectedFarmId: Int = 1,
    val ciclos: List<CicloRecord> = emptyList(),
    val ponds: List<Pond> = emptyList(),
    val inventory: List<InvItem> = emptyList(),
    val speciesList: List<SpeciesModel> = emptyList(),
    val searchQuery: String = "",
    val inventoryFilter: InvCategory? = null,
    val showInventoryTypeDialog: Boolean = false,
    val cycleToFinalize: CicloRecord? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
