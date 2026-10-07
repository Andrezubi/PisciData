package ucb.piscidata.database.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.usecase.*

class DatabaseViewModel(
    private val getCiclosUseCase: GetCiclosUseCase,
    private val saveCicloUseCase: SaveCicloUseCase,
    private val deleteCicloUseCase: DeleteCicloUseCase,
    private val getPondsUseCase: GetPondsUseCase,
    private val getInventoryUseCase: GetInventoryUseCase,
    private val updateInventoryQtyUseCase: UpdateInventoryQtyUseCase
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
            is DatabaseEvent.ChangeTab -> _state.update { it.copy(tab = event.tab, searchQuery = "") }
            is DatabaseEvent.ChangeView -> _state.update { it.copy(view = event.view) }
            is DatabaseEvent.Search -> _state.update { it.copy(searchQuery = event.query) }
            is DatabaseEvent.FilterInventory -> _state.update { it.copy(inventoryFilter = event.category) }
            is DatabaseEvent.SaveCiclo -> saveCiclo(event.ciclo)
            is DatabaseEvent.DeleteCiclo -> deleteCiclo(event.id)
            is DatabaseEvent.UpdateInventoryQty -> updateInventoryQty(event.id, event.newQty)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getCiclosUseCase().onSuccess { ciclos ->
                _state.update { it.copy(ciclos = ciclos) }
            }
            getPondsUseCase().onSuccess { ponds ->
                _state.update { it.copy(ponds = ponds) }
            }
            getInventoryUseCase().onSuccess { inv ->
                _state.update { it.copy(inventory = inv, isLoading = false) }
            }
        }
    }

    private fun saveCiclo(ciclo: CicloRecord) {
        viewModelScope.launch {
            saveCicloUseCase(ciclo).onSuccess {
                loadData()
                _state.update { it.copy(view = DbView.List, tab = DbTab.CICLOS) }
                _effect.emit(DatabaseEffect.ShowToast("Ciclo guardado exitosamente"))
            }
        }
    }

    private fun deleteCiclo(id: String) {
        viewModelScope.launch {
            deleteCicloUseCase(id).onSuccess {
                loadData()
                _effect.emit(DatabaseEffect.ShowToast("Ciclo eliminado"))
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
