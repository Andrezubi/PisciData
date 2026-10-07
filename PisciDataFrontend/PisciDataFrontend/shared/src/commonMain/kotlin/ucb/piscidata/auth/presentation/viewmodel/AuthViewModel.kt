package ucb.piscidata.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.auth.domain.usecase.LoginUseCase

class AuthViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AuthEffect>()
    val effect = _effect.asSharedFlow()

    fun emitEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.OnPhoneChanged -> {
                _state.update { it.copy(phone = event.phone, error = null) }
            }
            is AuthEvent.OnPasswordChanged -> {
                _state.update { it.copy(password = event.password, error = null) }
            }
            AuthEvent.OnLoginClicked -> {
                login()
            }
            AuthEvent.OnLogout -> {
                _state.update { AuthState(isAuthenticated = false, user = null) }
            }
        }
    }

    private fun login() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = loginUseCase(currentState.phone, currentState.password)
            result.fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            isAuthenticated = true,
                            error = null
                        )
                    }
                    _effect.emit(AuthEffect.ShowToast("Bienvenido ${user.name}"))
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al iniciar sesión"
                        )
                    }
                    _effect.emit(AuthEffect.ShowToast(exception.message ?: "Error al iniciar sesión"))
                }
            )
        }
    }
}
