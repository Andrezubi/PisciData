package ucb.piscidata.auth.presentation.viewmodel

sealed interface AuthEffect {
    data class ShowToast(val message: String) : AuthEffect
}
