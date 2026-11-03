package ucb.piscidata.chat.domain.usecase

import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.repository.ChatRepository

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(conversationId: Int, text: String): Result<ChatMessageModel> {
        return repository.sendMessage(conversationId, text)
    }
}
