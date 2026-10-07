package ucb.piscidata.chat.data.datasource

import ucb.piscidata.chat.domain.model.ChatMessageModel

interface ChatRemoteDataSource {
    suspend fun getInitialMessages(): Result<List<ChatMessageModel>>
    suspend fun sendMessage(text: String): Result<List<ChatMessageModel>>
    suspend fun sendAudio(duration: Int): Result<List<ChatMessageModel>>
}
