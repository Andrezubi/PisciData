package ucb.piscidata.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ucb.piscidata.database.domain.model.FarmModel

class SessionManager {
    private val _farms = MutableStateFlow(
        listOf(
            FarmModel(1, "Piscigranja El Manantial"),
            FarmModel(2, "Piscigranja Norte - San Juan")
        )
    )
    val farms: StateFlow<List<FarmModel>> = _farms.asStateFlow()

    private val _selectedFarmId = MutableStateFlow(1)
    val selectedFarmId: StateFlow<Int> = _selectedFarmId.asStateFlow()

    fun selectFarm(farmId: Int) {
        _selectedFarmId.value = farmId
    }

    fun getSelectedFarmName(): String {
        return _farms.value.find { it.id == _selectedFarmId.value }?.name ?: "Piscigranja El Manantial"
    }
}
