package ucb.piscidata.reports.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.reports.data.dto.FarmReportDto
import ucb.piscidata.reports.data.service.ReportService
import ucb.piscidata.session.SessionManager

data class ReportsState(
    val report: FarmReportDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ReportsViewModel(
    private val reportService: ReportService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.selectedFarmId.collectLatest { farmId ->
                if (farmId > 0) {
                    loadReport(farmId)
                }
            }
        }
    }

    fun refresh() {
        val farmId = sessionManager.selectedFarmId.value
        if (farmId > 0) {
            loadReport(farmId)
        }
    }

    private fun loadReport(farmId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            reportService.getFarmReport(farmId)
                .onSuccess { dto ->
                    _state.update { it.copy(report = dto, isLoading = false, error = null) }
                }
                .onFailure { err ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = err.message ?: "No se pudieron obtener los reportes"
                        )
                    }
                }
        }
    }
}

