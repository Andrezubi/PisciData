package ucb.piscidata.chat.data.mapper

import ucb.piscidata.chat.data.dto.AiMessageDto
import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.model.MessageSender

// Convierte el DTO de red al modelo de dominio que consume Compose en la UI
fun AiMessageDto.toDomain(): ChatMessageModel {
    val sender = when (role.lowercase()) {
        "user" -> MessageSender.USER
        "assistant", "ia", "system" -> MessageSender.IA
        else -> MessageSender.IA
    }

    return ChatMessageModel(
        id = id?.toString() ?: "",
        sender = sender,
        text = content,
        isConfirmCard = false
    )
}
