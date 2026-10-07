package ucb.piscidata.auth.presentation.viewmodel

sealed interface AuthEvent {
    data class OnPhoneChanged(val phone: String) : AuthEvent
    data class OnPasswordChanged(val password: String) : AuthEvent
    object OnLoginClicked : AuthEvent
    object OnLogout : AuthEvent
}
