package ucb.piscidata.chat.data.service

import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.mp.KoinPlatform
import ucb.piscidata.chat.data.datasource.ChatRemoteDataSource
import ucb.piscidata.chat.data.dto.AiConversationDto
import ucb.piscidata.chat.data.dto.AiMessageDto
import ucb.piscidata.chat.data.dto.CreateAiConversationDto
import ucb.piscidata.chat.data.dto.SendAiMessageDto
import ucb.piscidata.chat.data.dto.TranscriptionResponseDto
import ucb.piscidata.chat.data.mapper.toDomain
import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.data.remote.RemoteBackendConfig
import ucb.piscidata.session.SessionManager

class ChatService : ChatRemoteDataSource {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private fun extractBackendError(rawBody: String, statusCode: Int): String {
        return try {
            val obj = Json.parseToJsonElement(rawBody).jsonObject
            obj["message"]?.jsonPrimitive?.content ?: "Error HTTP $statusCode"
        } catch (_: Exception) {
            "Error HTTP $statusCode"
        }
    }

    override suspend fun getConversations(): Result<List<AiConversationDto>> {
        val token = sessionManager.token.value
        var lastError: Exception? = null
        for (url in RemoteBackendConfig.urlsFor("/api/AiConversations")) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (response.status.value in 200..299) {
                    return Result.success(response.body<List<AiConversationDto>>())
                }
                lastError = Exception(extractBackendError(response.bodyAsText(), response.status.value))
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(Exception("No se pudieron cargar las conversaciones: ${lastError?.message ?: "sin conexión"}"))
    }

    override suspend fun createConversation(title: String): Result<AiConversationDto> {
        val token = sessionManager.token.value
        var lastError: Exception? = null
        for (url in RemoteBackendConfig.urlsFor("/api/AiConversations")) {
            try {
                val response = client.post(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(CreateAiConversationDto(title = title))
                }
                if (response.status.value in 200..299) {
                    return Result.success(response.body<AiConversationDto>())
                }
                lastError = Exception(extractBackendError(response.bodyAsText(), response.status.value))
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(Exception("No se pudo crear la conversación: ${lastError?.message ?: "sin conexión"}"))
    }

    override suspend fun getMessages(conversationId: Int): Result<List<ChatMessageModel>> {
        val token = sessionManager.token.value
        var lastError: Exception? = null
        for (url in RemoteBackendConfig.urlsFor("/api/AiConversations/$conversationId/messages")) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (response.status.value in 200..299) {
                    val messagesDto = response.body<List<AiMessageDto>>()
                    return Result.success(messagesDto.map { it.toDomain() })
                }
                lastError = Exception(extractBackendError(response.bodyAsText(), response.status.value))
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(Exception("No se pudieron obtener los mensajes: ${lastError?.message ?: "sin conexión"}"))
    }

    override suspend fun sendMessage(conversationId: Int, text: String): Result<ChatMessageModel> {
        val token = sessionManager.token.value
        var lastError: Exception? = null
        for (url in RemoteBackendConfig.urlsFor("/api/AiConversations/$conversationId/messages")) {
            try {
                val response = client.post(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(SendAiMessageDto(content = text))
                    timeout {
                        requestTimeoutMillis = 360_000
                        socketTimeoutMillis = 360_000
                    }
                }
                if (response.status.value in 200..299) {
                    val aiMessageDto = response.body<AiMessageDto>()
                    return Result.success(aiMessageDto.toDomain())
                }
                val backendMsg = extractBackendError(response.bodyAsText(), response.status.value)
                // Si el servidor respondió con un error HTTP (ej. 502/400), devolvemos el detalle real sin reintentar en localhost apagado
                return Result.failure(Exception(backendMsg))
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(Exception("Fallo de comunicación con PisciData IA: ${lastError?.message ?: "sin conexión"}"))
    }

    override suspend fun transcribeAudio(audioBytes: ByteArray): Result<String> {
        val token = sessionManager.token.value
        var lastError: Exception? = null
        for (url in RemoteBackendConfig.urlsFor("/api/AiConversations/transcribe")) {
            try {
                val response = client.post(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    setBody(ByteArrayContent(audioBytes, ContentType.parse("audio/wav")))
                    timeout {
                        requestTimeoutMillis = 60_000
                        socketTimeoutMillis = 60_000
                    }
                }
                if (response.status.value in 200..299) {
                    val dto = response.body<TranscriptionResponseDto>()
                    return Result.success(dto.text)
                }
                return Result.failure(Exception(extractBackendError(response.bodyAsText(), response.status.value)))
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(Exception("No se pudo transcribir el audio: ${lastError?.message ?: "sin conexión"}"))
    }
}
