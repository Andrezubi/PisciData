package ucb.piscidata.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.auth.domain.usecase.RegisterUseCase

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun emitEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnFirstNameChanged -> _state.update { it.copy(firstName = event.value, error = null) }
            is RegisterEvent.OnLastNameChanged -> _state.update { it.copy(lastName = event.value, error = null) }
            is RegisterEvent.OnPhoneChanged -> _state.update { it.copy(phone = event.value, error = null) }
            is RegisterEvent.OnPasswordChanged -> _state.update { it.copy(password = event.value, error = null) }
            is RegisterEvent.OnConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = event.value, error = null) }
            is RegisterEvent.OnRoleChanged -> _state.update { it.copy(role = event.value, error = null) }
            RegisterEvent.OnRegisterClicked -> register()
        }
    }

    private fun register() {
        val s = _state.value
        if (s.firstName.isBlank() || s.lastName.isBlank() || s.phone.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }
        if (s.password != s.confirmPassword) {
            _state.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = registerUseCase(s.firstName, s.lastName, s.phone, s.password, s.role)
            result.fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            registeredUser = user,
                            isRegistered = true,
                            error = null
                        )
                    }
                },
                onFailure = { ex ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = ex.message ?: "Error al registrar usuario"
                        )
                    }
                }
            )
        }
    }
}
