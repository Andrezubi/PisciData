package ucb.piscidata.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ucb.piscidata.database.domain.model.FarmModel

class SessionManager {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _farms = MutableStateFlow<List<FarmModel>>(emptyList())
    val farms: StateFlow<List<FarmModel>> = _farms.asStateFlow()

    private val _selectedFarmId = MutableStateFlow<Int>(1)
    val selectedFarmId: StateFlow<Int> = _selectedFarmId.asStateFlow()

    fun setToken(newToken: String?) {
        _token.value = newToken
    }

    fun getToken(): String? = _token.value

    fun clear() {
        _token.value = null
        _farms.value = emptyList()
    }

    fun setFarms(newFarms: List<FarmModel>) {
        _farms.value = newFarms
        if (newFarms.isNotEmpty() && !newFarms.any { it.id == _selectedFarmId.value }) {
            _selectedFarmId.value = newFarms.first().id
        }
    }

    fun selectFarm(farmId: Int) {
        _selectedFarmId.value = farmId
    }

    fun getSelectedFarmName(): String {
        return _farms.value.find { it.id == _selectedFarmId.value }?.name ?: "Piscigranja Principal"
    }
}
