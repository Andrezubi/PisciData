package ucb.piscidata.chat.domain.repository

import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.model.ConversationModel

interface ChatRepository {
    suspend fun getConversations(): Result<List<ConversationModel>>
    suspend fun createConversation(title: String): Result<ConversationModel>
    suspend fun getOrCreateActiveConversation(): Result<Int>
    suspend fun getMessages(conversationId: Int): Result<List<ChatMessageModel>>
    suspend fun sendMessage(conversationId: Int, text: String): Result<ChatMessageModel>
}
