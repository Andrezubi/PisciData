package ucb.piscidata.chat.domain.repository

import ucb.piscidata.chat.domain.model.ChatMessageModel

interface ChatRepository {
    suspend fun getOrCreateActiveConversation(): Result<Int>
    suspend fun getMessages(conversationId: Int): Result<List<ChatMessageModel>>
    suspend fun sendMessage(conversationId: Int, text: String): Result<ChatMessageModel>
}
