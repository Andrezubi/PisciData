package ucb.piscidata.chat.data.dto

import kotlinx.serialization.Serializable

// Representa el resumen de una conversacion devuelto por GET /api/AiConversations
@Serializable
data class AiConversationDto(
    val id: Int,
    val ownerUserId: Int? = null,
    val title: String? = null,
    val startDate: String? = null,
    val lastMessageDate: String? = null,
    val createdAt: String? = null
)

// Payload para crear una nueva conversacion mediante POST /api/AiConversations
@Serializable
data class CreateAiConversationDto(
    val title: String
)

// Representa un mensaje individual (usuario o IA) devuelto por la API
@Serializable
data class AiMessageDto(
    val id: Int? = null,
    val aiconversationId: Int? = null,
    val role: String,
    val content: String,
    val messageDate: String? = null
)

// Payload enviado para consultar a la IA mediante POST /api/AiConversations/{id}/messages
@Serializable
data class SendAiMessageDto(
    val content: String
)

// Respuesta de la transcripción de audio devuelta por POST /api/AiConversations/transcribe
@Serializable
data class TranscriptionResponseDto(
    val text: String = "",
    val language: String = "es"
)
