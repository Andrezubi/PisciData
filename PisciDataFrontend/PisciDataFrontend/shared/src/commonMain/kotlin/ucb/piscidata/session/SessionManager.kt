package ucb.piscidata.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ucb.piscidata.database.domain.model.FarmModel

class SessionManager {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId.asStateFlow()

    private val _farms = MutableStateFlow<List<FarmModel>>(emptyList())
    val farms: StateFlow<List<FarmModel>> = _farms.asStateFlow()

    private val _selectedFarmId = MutableStateFlow<Int?>(null)
    val selectedFarmId: StateFlow<Int?> = _selectedFarmId.asStateFlow()

    fun setToken(token: String) {
        _token.value = token
    }

    fun getToken(): String? = _token.value

    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    fun setFarms(farms: List<FarmModel>) {
        _farms.value = farms
        if (_selectedFarmId.value == null && farms.isNotEmpty()) {
            _selectedFarmId.value = farms.first().id
        } else if (farms.isNotEmpty() && _selectedFarmId.value != null && farms.none { it.id == _selectedFarmId.value }) {
            _selectedFarmId.value = farms.first().id
        }
    }

    fun selectFarm(farmId: Int) {
        _selectedFarmId.value = farmId
    }

    fun getSelectedFarmName(): String {
        return _farms.value.find { it.id == _selectedFarmId.value }?.name ?: ""
    }

    fun clear() {
        _token.value = null
        _userId.value = null
        _farms.value = emptyList()
        _selectedFarmId.value = null
    }
}
