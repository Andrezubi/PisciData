package ucb.piscidata.database.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.usecase.*

class DatabaseViewModel(
    private val getFarmsUseCase: GetFarmsUseCase,
    private val getCiclosUseCase: GetCiclosUseCase,
    private val createProductionCycleUseCase: CreateProductionCycleUseCase,
    private val finalizeProductionCycleUseCase: FinalizeProductionCycleUseCase,
    private val saveCicloUseCase: SaveCicloUseCase,
    private val deleteCicloUseCase: DeleteCicloUseCase,
    private val toggleCicloExpandUseCase: ToggleCicloExpandUseCase,
    private val getPondsUseCase: GetPondsUseCase,
    private val createPondUseCase: CreatePondUseCase,
    private val getInventoryUseCase: GetInventoryUseCase,
    private val createFeedUseCase: CreateFeedUseCase,
    private val createSupplyUseCase: CreateSupplyUseCase,
    private val updateInventoryQtyUseCase: UpdateInventoryQtyUseCase,
    private val getSpeciesUseCase: GetSpeciesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DatabaseState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DatabaseEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadData()
    }

    fun emitEvent(event: DatabaseEvent) {
        when (event) {
            DatabaseEvent.LoadData -> loadData()
            DatabaseEvent.ClickNuevo -> handleClickNuevo()
            is DatabaseEvent.ChangeFarm -> {
                _state.update { it.copy(selectedFarmId = event.farmId) }
                loadCiclosForFarm(event.farmId)
            }
            is DatabaseEvent.ChangeTab -> _state.update { it.copy(tab = event.tab, searchQuery = "") }
            is DatabaseEvent.ChangeView -> _state.update { it.copy(view = event.view) }
            is DatabaseEvent.Search -> _state.update { it.copy(searchQuery = event.query) }
            is DatabaseEvent.FilterInventory -> _state.update { it.copy(inventoryFilter = event.category) }
            is DatabaseEvent.ToggleShowInventoryTypeDialog -> _state.update { it.copy(showInventoryTypeDialog = event.show) }
            is DatabaseEvent.CreateProductionCycle -> createProductionCycle(event)
            is DatabaseEvent.PromptFinalizeCycle -> _state.update { it.copy(cycleToFinalize = event.ciclo) }
            is DatabaseEvent.ConfirmFinalizeCycle -> finalizeCycle(event.ciclo)
            is DatabaseEvent.CreatePond -> createPond(event)
            is DatabaseEvent.CreateFeed -> createFeed(event)
            is DatabaseEvent.CreateSupply -> createSupply(event)
            is DatabaseEvent.SaveCiclo -> saveCiclo(event.ciclo)
            is DatabaseEvent.DeleteCiclo -> deleteCiclo(event.id)
            is DatabaseEvent.ToggleCicloExpand -> toggleExpand(event.id)
            is DatabaseEvent.UpdateInventoryQty -> updateInventoryQty(event.id, event.newQty)
        }
    }

    private fun handleClickNuevo() {
        when (_state.value.tab) {
            DbTab.CICLOS -> _state.update { it.copy(view = DbView.NuevoCiclo) }
            DbTab.ESTANQUES -> _state.update { it.copy(view = DbView.NuevoEstanque) }
            DbTab.INVENTARIO -> _state.update { it.copy(showInventoryTypeDialog = true) }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getFarmsUseCase().onSuccess { farms ->
                _state.update { it.copy(farms = farms) }
                val initialFarmId = farms.firstOrNull()?.id ?: 1
                loadCiclosForFarm(initialFarmId)
            }
            getPondsUseCase().onSuccess { ponds ->
                _state.update { it.copy(ponds = ponds) }
            }
            getInventoryUseCase().onSuccess { inv ->
                _state.update { it.copy(inventory = inv, isLoading = false) }
            }
            getSpeciesUseCase().onSuccess { species ->
                _state.update { it.copy(speciesList = species) }
            }
        }
    }

    private fun loadCiclosForFarm(farmId: Int) {
        viewModelScope.launch {
            getCiclosUseCase(farmId).onSuccess { ciclos ->
                _state.update { it.copy(ciclos = ciclos) }
            }
        }
    }

    private fun createProductionCycle(event: DatabaseEvent.CreateProductionCycle) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            createProductionCycleUseCase(
                pondId = event.pondId,
                speciesId = event.speciesId,
                startDate = event.startDate,
                initialFishCount = event.initialFishCount,
                initialAverageWeightGrams = event.initialAverageWeightGrams,
                initialAgeDays = event.initialAgeDays,
                observations = event.observations
            ).onSuccess {
                loadCiclosForFarm(_state.value.selectedFarmId)
                _state.update { it.copy(view = DbView.List, isLoading = false) }
                _effect.emit(DatabaseEffect.ShowToast("Ciclo productivo creado exitosamente"))
            }
        }
    }

    private fun finalizeCycle(ciclo: CicloRecord) {
        viewModelScope.launch {
            val cycleId = ciclo.id.replace("CP-2026-", "").toIntOrNull() ?: 1
            finalizeProductionCycleUseCase(
                cycleId = cycleId,
                pondId = 1,
                speciesId = 1,
                startDate = ciclo.startDate
            ).onSuccess {
                loadCiclosForFarm(_state.value.selectedFarmId)
                _state.update { it.copy(cycleToFinalize = null) }
                _effect.emit(DatabaseEffect.ShowToast("Ciclo finalizado exitosamente"))
            }
        }
    }

    private fun createPond(event: DatabaseEvent.CreatePond) {
        viewModelScope.launch {
            createPondUseCase(
                code = event.code,
                name = event.name,
                shape = event.shape,
                width = event.width,
                length = event.length,
                diameter = event.diameter,
                depth = event.depth,
                area = event.area,
                description = event.description
            ).onSuccess {
                getPondsUseCase().onSuccess { ponds ->
                    _state.update { it.copy(ponds = ponds, view = DbView.List) }
                }
                _effect.emit(DatabaseEffect.ShowToast("Estanque creado exitosamente"))
            }
        }
    }

    private fun createFeed(event: DatabaseEvent.CreateFeed) {
        viewModelScope.launch {
            createFeedUseCase(
                brand = event.brand,
                productName = event.productName,
                phase = event.phase,
                proteinPercentage = event.proteinPercentage,
                pelletSizeMm = event.pelletSizeMm,
                stockKg = event.stockKg,
                minimumStockKg = event.minimumStockKg
            ).onSuccess {
                getInventoryUseCase().onSuccess { inv ->
                    _state.update { it.copy(inventory = inv, view = DbView.List, showInventoryTypeDialog = false) }
                }
                _effect.emit(DatabaseEffect.ShowToast("Alimento registrado exitosamente"))
            }
        }
    }

    private fun createSupply(event: DatabaseEvent.CreateSupply) {
        viewModelScope.launch {
            createSupplyUseCase(
                name = event.name,
                category = event.category,
                quantity = event.quantity,
                unit = event.unit,
                minimumStock = event.minimumStock
            ).onSuccess {
                getInventoryUseCase().onSuccess { inv ->
                    _state.update { it.copy(inventory = inv, view = DbView.List, showInventoryTypeDialog = false) }
                }
                _effect.emit(DatabaseEffect.ShowToast("Suministro registrado exitosamente"))
            }
        }
    }

    private fun saveCiclo(ciclo: CicloRecord) {
        viewModelScope.launch {
            saveCicloUseCase(ciclo).onSuccess {
                loadCiclosForFarm(_state.value.selectedFarmId)
                _state.update { it.copy(view = DbView.List, tab = DbTab.CICLOS) }
                _effect.emit(DatabaseEffect.ShowToast("Ciclo guardado exitosamente"))
            }
        }
    }

    private fun deleteCiclo(id: String) {
        viewModelScope.launch {
            deleteCicloUseCase(id).onSuccess {
                loadCiclosForFarm(_state.value.selectedFarmId)
                _effect.emit(DatabaseEffect.ShowToast("Ciclo eliminado"))
            }
        }
    }

    private fun toggleExpand(id: String) {
        viewModelScope.launch {
            toggleCicloExpandUseCase(id).onSuccess { ciclos ->
                _state.update { it.copy(ciclos = ciclos) }
            }
        }
    }

    private fun updateInventoryQty(id: Int, newQty: Int) {
        viewModelScope.launch {
            updateInventoryQtyUseCase(id, newQty).onSuccess {
                getInventoryUseCase().onSuccess { inv ->
                    _state.update { it.copy(inventory = inv) }
                }
            }
        }
    }

    fun generateNewCicloId(): String {
        val nums = _state.value.ciclos.mapNotNull { r ->
            r.id.replace("CP-2026-", "").toIntOrNull()
        }
        val next = if (nums.isNotEmpty()) nums.maxOrNull()!! + 1 else 1
        return "CP-2026-${next.toString().padStart(3, '0')}"
    }
}
