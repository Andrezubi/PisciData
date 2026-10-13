package ucb.piscidata.chat.domain.repository

import ucb.piscidata.chat.domain.model.ChatMessageModel

interface ChatRepository {
    suspend fun getInitialMessages(): Result<List<ChatMessageModel>>
    suspend fun sendMessage(text: String): Result<List<ChatMessageModel>>
    suspend fun sendAudio(duration: Int): Result<List<ChatMessageModel>>
}
