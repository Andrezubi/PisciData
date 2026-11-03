package ucb.piscidata.chat.presentation.viewmodel

sealed interface ChatEvent {
    object OnLoadMessages : ChatEvent
    data class OnSelectConversation(val conversationId: Int) : ChatEvent
    object OnOpenNewConversationDialog : ChatEvent
    object OnCloseNewConversationDialog : ChatEvent
    data class OnNewConversationTitleChanged(val title: String) : ChatEvent
    object OnCreateConversation : ChatEvent
    data class OnInputChanged(val text: String) : ChatEvent
    object OnSendText : ChatEvent
    object OnStartRecording : ChatEvent
    data class OnStopRecording(val duration: Int) : ChatEvent
    object OnConfirmAction : ChatEvent
}
