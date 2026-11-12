package ucb.piscidata.farm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ucb.piscidata.auth.domain.usecase.LoginUseCase
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.domain.model.FarmModel
import ucb.piscidata.farm.domain.usecase.CreateFarmUseCase
import ucb.piscidata.farm.domain.usecase.GetMyFarmsUseCase
import ucb.piscidata.session.SessionManager

class RegisterFarmViewModel(
    private val createFarmUseCase: CreateFarmUseCase,
    private val loginUseCase: LoginUseCase,
    private val getMyFarmsUseCase: GetMyFarmsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterFarmState())
    val state = _state.asStateFlow()

    fun emitEvent(event: RegisterFarmEvent) {
        when (event) {
            is RegisterFarmEvent.Init -> _state.update {
                it.copy(
                    userId = event.userId,
                    phone = event.phone,
                    pass = event.pass
                )
            }
            is RegisterFarmEvent.OnNameChanged -> _state.update { it.copy(name = event.value, error = null) }
            is RegisterFarmEvent.OnDescriptionChanged -> _state.update { it.copy(description = event.value, error = null) }
            is RegisterFarmEvent.OnAddressChanged -> _state.update { it.copy(address = event.value, error = null) }
            is RegisterFarmEvent.OnLocationSelected -> _state.update {
                it.copy(
                    latitude = event.latitude,
                    longitude = event.longitude,
                    isMapPickerOpen = false,
                    error = null
                )
            }
            is RegisterFarmEvent.SetMapPickerOpen -> _state.update { it.copy(isMapPickerOpen = event.open) }
            is RegisterFarmEvent.SetError -> _state.update { it.copy(error = event.error) }
            RegisterFarmEvent.OnSubmitClicked -> submit()
        }
    }

    private fun submit() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "El nombre de la granja es obligatorio.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = createFarmUseCase(
                ownerUserId = s.userId,
                name = s.name.trim(),
                description = s.description.trim().ifBlank { null },
                address = s.address.trim().ifBlank { null },
                latitude = s.latitude,
                longitude = s.longitude
            )

            result.fold(
                onSuccess = { createdFarm ->
                    // Auto-login to obtain JWT token and initialize session
                    if (s.phone.isNotBlank() && s.pass.isNotBlank()) {
                        val loginResult = loginUseCase(s.phone, s.pass)
                        loginResult.onSuccess {
                            ApiClient.sessionManager = sessionManager
                            getMyFarmsUseCase().onSuccess { farms ->
                                val farmModels = farms.map { FarmModel(it.id, it.name) }
                                sessionManager.setFarms(farmModels)
                                sessionManager.selectFarm(createdFarm.id)
                            }
                        }
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            error = null
                        )
                    }
                },
                onFailure = { ex ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = ex.message ?: "Error al registrar la granja."
                        )
                    }
                }
            )
        }
    }
}
