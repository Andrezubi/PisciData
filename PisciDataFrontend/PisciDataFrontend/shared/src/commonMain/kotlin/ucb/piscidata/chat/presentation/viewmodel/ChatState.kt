package ucb.piscidata.chat.presentation.viewmodel

import ucb.piscidata.chat.domain.model.ChatMessageModel

data class ChatState(
    val messages: List<ChatMessageModel> = emptyList(),
    val inputMessage: String = "",
    val isRecording: Boolean = false,
    val recordSeconds: Int = 0,
    val isLoading: Boolean = false
)
