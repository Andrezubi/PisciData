package ucb.piscidata.profile.presentation.viewmodel

sealed interface ProfileEffect {
    data class ShowToast(val message: String) : ProfileEffect
}
