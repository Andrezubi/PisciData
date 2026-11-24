package ucb.piscidata.chat.data.service

import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.contentType
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

    private val conversationsUrls: List<String>
        get() = RemoteBackendConfig.urlsFor("/api/AiConversations")

    private fun messagesUrls(conversationId: Int): List<String> =
        RemoteBackendConfig.urlsFor("/api/AiConversations/$conversationId/messages")

    private val transcribeUrls: List<String>
        get() = RemoteBackendConfig.urlsFor("/api/AiConversations/transcribe")

    override suspend fun getConversations(): Result<List<AiConversationDto>> {
        val token = sessionManager.token.value
        var lastError: Throwable? = null

        for (url in conversationsUrls) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    return Result.success(response.body<List<AiConversationDto>>())
                }
                lastError = Exception("HTTP ${response.status.value} al consultar conversaciones")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("Fallo de red al listar conversaciones"))
    }

    override suspend fun createConversation(title: String): Result<AiConversationDto> {
        val token = sessionManager.token.value
        val dto = CreateAiConversationDto(title = title)
        var lastError: Throwable? = null

        for (url in conversationsUrls) {
            try {
                val response = client.post(url) {
                    if (!token.isNullOrBlank()) {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(dto)
                }
                if (response.status.value in 200..299) {
                    return Result.success(response.body<AiConversationDto>())
                }
                lastError = Exception("HTTP ${response.status.value} al crear conversación")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("Fallo al crear conversación"))
    }

    override suspend fun getMessages(conversationId: Int): Result<List<ChatMessageModel>> {
        val token = sessionManager.token.value
        var lastError: Throwable? = null

        for (url in messagesUrls(conversationId)) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val messagesDto = response.body<List<AiMessageDto>>()
                    return Result.success(messagesDto.map { it.toDomain() })
                }
                lastError = Exception("HTTP ${response.status.value} al obtener mensajes")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("Fallo al obtener mensajes"))
    }

    override suspend fun sendMessage(conversationId: Int, text: String): Result<ChatMessageModel> {
        val token = sessionManager.token.value
        val dto = SendAiMessageDto(content = text)
        var lastError: Throwable? = null

        for (url in messagesUrls(conversationId)) {
            try {
                val response = client.post(url) {
                    if (!token.isNullOrBlank()) {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(dto)
                    timeout {
                        requestTimeoutMillis = 180_000 // 3 minutos para inferencia local de IA
                        socketTimeoutMillis = 180_000
                    }
                }
                if (response.status.value in 200..299) {
                    val aiMessageDto = response.body<AiMessageDto>()
                    return Result.success(aiMessageDto.toDomain())
                }
                lastError = Exception("HTTP ${response.status.value} al enviar mensaje")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("Fallo de comunicación con la IA"))
    }

    override suspend fun transcribeAudio(audioBytes: ByteArray): Result<String> {
        val token = sessionManager.token.value
        var lastError: Throwable? = null

        for (url in transcribeUrls) {
            try {
                val response = client.post(url) {
                    if (!token.isNullOrBlank()) {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
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
                lastError = Exception("HTTP ${response.status.value} al transcribir audio")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("Fallo al transcribir audio"))
    }
}
