package ucb.piscidata.chat.domain.model

enum class MessageSender {
    IA, USER, AUDIO
}

data class ChatMessageModel(
    val id: String = "",
    val sender: MessageSender,
    val text: String? = null,
    val durationSeconds: Int? = null,
    val isConfirmCard: Boolean = false
)
