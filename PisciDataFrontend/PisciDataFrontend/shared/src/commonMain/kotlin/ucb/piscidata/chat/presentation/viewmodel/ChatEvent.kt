package ucb.piscidata.chat.presentation.viewmodel

sealed interface ChatEvent {
    object OnLoadMessages : ChatEvent
    data class OnToggleHistorySheet(val show: Boolean) : ChatEvent
    data class OnSelectConversation(val conversationId: Int) : ChatEvent
    object OnOpenNewConversationDialog : ChatEvent
    object OnCloseNewConversationDialog : ChatEvent
    data class OnNewConversationTitleChanged(val title: String) : ChatEvent
    object OnCreateConversation : ChatEvent
    data class OnInputChanged(val text: String) : ChatEvent
    object OnSendText : ChatEvent
    data class OnSendQuickPrompt(val prompt: String) : ChatEvent
    object OnRetryLastMessage : ChatEvent
    object OnDismissError : ChatEvent
    object OnStartRecording : ChatEvent
    object OnTickRecording : ChatEvent
    object OnCancelRecording : ChatEvent
    data class OnAudioRecorded(val audioBytes: ByteArray) : ChatEvent
    object OnConfirmAction : ChatEvent
}
