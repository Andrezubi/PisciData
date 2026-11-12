package ucb.piscidata.chat.data.datasource

import ucb.piscidata.chat.data.dto.AiConversationDto
import ucb.piscidata.chat.domain.model.ChatMessageModel

interface ChatRemoteDataSource {
    suspend fun getConversations(): Result<List<AiConversationDto>>
    suspend fun createConversation(title: String): Result<AiConversationDto>
    suspend fun getMessages(conversationId: Int): Result<List<ChatMessageModel>>
    suspend fun sendMessage(conversationId: Int, text: String): Result<ChatMessageModel>
    suspend fun transcribeAudio(audioBytes: ByteArray): Result<String>
}
