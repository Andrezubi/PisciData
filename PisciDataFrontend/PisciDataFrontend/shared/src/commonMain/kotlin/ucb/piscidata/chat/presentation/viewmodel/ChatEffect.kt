package ucb.piscidata.chat.presentation.viewmodel

sealed interface ChatEffect {
    data class ShowToast(val message: String) : ChatEffect
}
