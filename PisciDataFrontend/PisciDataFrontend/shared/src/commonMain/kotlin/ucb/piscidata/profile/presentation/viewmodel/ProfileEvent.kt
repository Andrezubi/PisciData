package ucb.piscidata.profile.presentation.viewmodel

sealed interface ProfileEvent {
    data class LoadProfile(val userId: Int) : ProfileEvent
    object ToggleEditMode : ProfileEvent
    data class UpdateFirstName(val value: String) : ProfileEvent
    data class UpdateLastName(val value: String) : ProfileEvent
    data class UpdatePhone(val value: String) : ProfileEvent
    object SaveProfile : ProfileEvent
}
