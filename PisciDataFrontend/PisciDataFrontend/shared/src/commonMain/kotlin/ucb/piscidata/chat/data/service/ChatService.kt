package ucb.piscidata.chat.data.service

import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ucb.piscidata.chat.data.datasource.ChatRemoteDataSource
import ucb.piscidata.chat.data.dto.AiConversationDto
import ucb.piscidata.chat.data.dto.AiMessageDto
import ucb.piscidata.chat.data.dto.CreateAiConversationDto
import ucb.piscidata.chat.data.dto.SendAiMessageDto
import ucb.piscidata.chat.data.mapper.toDomain
import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.data.remote.ApiEndpoints

class ChatService : ChatRemoteDataSource {
    private val client = ApiClient.client

    override suspend fun getConversations(): Result<List<AiConversationDto>> {
        return try {
            val response = client.get(ApiEndpoints.AiChat.CONVERSATIONS)
            if (response.status.value in 200..299) {
                Result.success(response.body<List<AiConversationDto>>())
            } else {
                Result.failure(Exception("Error al consultar conversaciones (${response.status.value})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de red al listar conversaciones: ${e.message ?: e.toString()}"))
        }
    }

    override suspend fun createConversation(title: String): Result<AiConversationDto> {
        return try {
            val response = client.post(ApiEndpoints.AiChat.CONVERSATIONS) {
                contentType(ContentType.Application.Json)
                setBody(CreateAiConversationDto(title = title))
            }
            if (response.status.value in 200..299) {
                Result.success(response.body<AiConversationDto>())
            } else {
                Result.failure(Exception("Error al crear conversación (${response.status.value})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo al crear conversación: ${e.message ?: e.toString()}"))
        }
    }

    override suspend fun getMessages(conversationId: Int): Result<List<ChatMessageModel>> {
        return try {
            val response = client.get(ApiEndpoints.AiChat.messages(conversationId))
            if (response.status.value in 200..299) {
                val messagesDto = response.body<List<AiMessageDto>>()
                Result.success(messagesDto.map { it.toDomain() })
            } else {
                Result.failure(Exception("Error al obtener mensajes (${response.status.value})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo al obtener mensajes: ${e.message ?: e.toString()}"))
        }
    }

    override suspend fun sendMessage(conversationId: Int, text: String): Result<ChatMessageModel> {
        return try {
            val response = client.post(ApiEndpoints.AiChat.messages(conversationId)) {
                contentType(ContentType.Application.Json)
                setBody(SendAiMessageDto(content = text))
                timeout {
                    requestTimeoutMillis = 360_000 // 6 minutos, igual que el timeout HTTP del backend
                    socketTimeoutMillis = 360_000 // Ollama devuelve la respuesta completa, sin streaming
                }
            }
            if (response.status.value in 200..299) {
                val aiMessageDto = response.body<AiMessageDto>()
                Result.success(aiMessageDto.toDomain())
            } else {
                Result.failure(Exception("Error al consultar asistente (${response.status.value})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de comunicación con la IA: ${e.message ?: e.toString()}"))
        }
    }
}
