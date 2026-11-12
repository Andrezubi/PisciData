package ucb.piscidata.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.profile.domain.usecase.GetUserProfileUseCase
import ucb.piscidata.profile.domain.usecase.UpdateUserProfileUseCase

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>()
    val effect = _effect.asSharedFlow()

    private var currentUserId: Int = 1

    fun emitEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> {
                currentUserId = event.userId
                loadProfile(event.userId)
            }
            ProfileEvent.ToggleEditMode -> {
                val current = _state.value
                if (current.editMode) {
                    current.profile?.let { p ->
                        _state.update { it.copy(firstName = p.firstName, lastName = p.lastName, phone = p.phone, role = p.role, editMode = false) }
                    }
                } else {
                    _state.update { it.copy(editMode = true, successMessage = null, error = null) }
                }
            }
            is ProfileEvent.UpdateFirstName -> _state.update { it.copy(firstName = event.value, error = null) }
            is ProfileEvent.UpdateLastName -> _state.update { it.copy(lastName = event.value, error = null) }
            is ProfileEvent.UpdatePhone -> _state.update { it.copy(phone = event.value, error = null) }
            ProfileEvent.SaveProfile -> saveProfile()
        }
    }

    private fun loadProfile(userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getUserProfileUseCase(userId).fold(
                onSuccess = { p ->
                    _state.update {
                        it.copy(
                            profile = p,
                            firstName = p.firstName,
                            lastName = p.lastName,
                            phone = p.phone,
                            role = p.role,
                            createdAt = p.createdAt,
                            isLoading = false
                        )
                    }
                },
                onFailure = { ex ->
                    _state.update { it.copy(isLoading = false, error = ex.message ?: "Error al cargar perfil") }
                }
            )
        }
    }

    private fun saveProfile() {
        val s = _state.value
        if (s.firstName.isBlank() || s.lastName.isBlank() || s.phone.isBlank()) {
            _state.update { it.copy(error = "Nombre, apellido y teléfono son obligatorios") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            updateUserProfileUseCase(currentUserId, s.firstName, s.lastName, s.phone, s.role).fold(
                onSuccess = { p ->
                    _state.update {
                        it.copy(
                            profile = p,
                            firstName = p.firstName,
                            lastName = p.lastName,
                            phone = p.phone,
                            role = p.role,
                            editMode = false,
                            isLoading = false,
                            successMessage = "Perfil actualizado correctamente"
                        )
                    }
                    _effect.emit(ProfileEffect.ShowToast("Perfil actualizado correctamente"))
                },
                onFailure = { ex ->
                    _state.update { it.copy(isLoading = false, error = ex.message ?: "Error al actualizar perfil") }
                }
            )
        }
    }
}
