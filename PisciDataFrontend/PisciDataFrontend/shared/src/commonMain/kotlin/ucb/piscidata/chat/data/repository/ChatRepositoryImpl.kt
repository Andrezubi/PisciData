package ucb.piscidata.chat.data.repository

import ucb.piscidata.chat.data.datasource.ChatRemoteDataSource
import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val dataSource: ChatRemoteDataSource
) : ChatRepository {
    override suspend fun getInitialMessages(): Result<List<ChatMessageModel>> {
        return dataSource.getInitialMessages()
    }

    override suspend fun sendMessage(text: String): Result<List<ChatMessageModel>> {
        return dataSource.sendMessage(text)
    }

    override suspend fun sendAudio(duration: Int): Result<List<ChatMessageModel>> {
        return dataSource.sendAudio(duration)
    }
}
