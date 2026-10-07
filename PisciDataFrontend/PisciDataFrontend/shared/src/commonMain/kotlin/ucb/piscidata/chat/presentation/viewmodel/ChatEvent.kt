package ucb.piscidata.chat.presentation.viewmodel

sealed interface ChatEvent {
    object OnLoadMessages : ChatEvent
    data class OnInputChanged(val text: String) : ChatEvent
    object OnSendText : ChatEvent
    object OnStartRecording : ChatEvent
    data class OnStopRecording(val duration: Int) : ChatEvent
    object OnConfirmAction : ChatEvent
}
