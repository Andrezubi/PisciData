package ucb.piscidata.chat.presentation.viewmodel

import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.model.ConversationModel

data class ChatState(
    val conversations: List<ConversationModel> = emptyList(),
    val conversationId: Int? = null,
    val selectedConversation: ConversationModel? = null,
    val messages: List<ChatMessageModel> = emptyList(),
    val inputMessage: String = "",
    val isRecording: Boolean = false,
    val recordSeconds: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showNewConversationDialog: Boolean = false,
    val newConversationTitleInput: String = ""
)
