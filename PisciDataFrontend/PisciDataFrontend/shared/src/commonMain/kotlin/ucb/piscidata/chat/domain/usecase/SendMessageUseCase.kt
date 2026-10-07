package ucb.piscidata.chat.domain.usecase

import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.repository.ChatRepository

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(text: String): Result<List<ChatMessageModel>> {
        return repository.sendMessage(text)
    }
}
