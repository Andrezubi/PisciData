package ucb.piscidata.auth.presentation.viewmodel

sealed interface RegisterEvent {
    data class OnFirstNameChanged(val value: String) : RegisterEvent
    data class OnLastNameChanged(val value: String) : RegisterEvent
    data class OnPhoneChanged(val value: String) : RegisterEvent
    data class OnPasswordChanged(val value: String) : RegisterEvent
    data class OnConfirmPasswordChanged(val value: String) : RegisterEvent
    data class OnRoleChanged(val value: String) : RegisterEvent
    object OnRegisterClicked : RegisterEvent
}
