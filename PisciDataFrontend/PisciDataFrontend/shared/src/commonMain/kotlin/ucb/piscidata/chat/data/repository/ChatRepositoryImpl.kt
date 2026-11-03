package ucb.piscidata.chat.data.repository

import ucb.piscidata.chat.data.datasource.ChatRemoteDataSource
import ucb.piscidata.chat.data.mapper.toDomain
import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.model.ConversationModel
import ucb.piscidata.chat.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val dataSource: ChatRemoteDataSource
) : ChatRepository {

    override suspend fun getConversations(): Result<List<ConversationModel>> {
        return dataSource.getConversations().map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun createConversation(title: String): Result<ConversationModel> {
        return dataSource.createConversation(title).map { dto ->
            dto.toDomain()
        }
    }

    override suspend fun getOrCreateActiveConversation(): Result<Int> {
        val listResult = dataSource.getConversations()
        if (listResult.isFailure) {
            return Result.failure(listResult.exceptionOrNull() ?: Exception("Error al obtener conversaciones"))
        }

        val list = listResult.getOrNull() ?: emptyList()
        return if (list.isNotEmpty()) {
            Result.success(list.first().id)
        } else {
            val createResult = dataSource.createConversation("Conversación PisciData")
            if (createResult.isFailure) {
                return Result.failure(createResult.exceptionOrNull() ?: Exception("Error al crear conversación"))
            }
            Result.success(createResult.getOrNull()!!.id)
        }
    }

    override suspend fun getMessages(conversationId: Int): Result<List<ChatMessageModel>> {
        return dataSource.getMessages(conversationId)
    }

    override suspend fun sendMessage(conversationId: Int, text: String): Result<ChatMessageModel> {
        return dataSource.sendMessage(conversationId, text)
    }
}
