package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.data.dto.CreateFeedingRequestDto
import ucb.piscidata.database.data.dto.FeedResponseDto
import ucb.piscidata.database.data.dto.FeedingDetailResponseDto
import ucb.piscidata.database.data.dto.FeedingRecommendationResponseDto
import ucb.piscidata.database.data.mapper.toModel
import ucb.piscidata.database.data.mapper.toRecord
import ucb.piscidata.database.domain.model.FeedingRecord
import ucb.piscidata.database.domain.model.FeedingRecommendation
import ucb.piscidata.session.SessionManager

class FeedingService {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private val baseUrls = listOf(
        "http://127.0.0.1:5007"
    )

    suspend fun getFeedingsByCycle(cycleId: Int): Result<List<FeedingRecord>> {
        val token = sessionManager.token.value
        for (baseUrl in baseUrls) {
            try {
                val response = client.get("$baseUrl/api/feedings/cycle/$cycleId") {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dtos: List<FeedingDetailResponseDto> = response.body()
                    return Result.success(dtos.map { it.toRecord() })
                }
            } catch (_: Exception) {}
        }
        return Result.success(emptyList())
    }

    suspend fun getRecommendation(cycleId: Int, date: String? = null): Result<FeedingRecommendation?> {
        val token = sessionManager.token.value
        val query = if (date != null) "?date=$date" else ""
        for (baseUrl in baseUrls) {
            try {
                val response = client.get("$baseUrl/api/feedings/recommendation/$cycleId$query") {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dto: FeedingRecommendationResponseDto = response.body()
                    return Result.success(dto.toModel())
                }
            } catch (_: Exception) {}
        }
        return Result.success(null)
    }

    suspend fun createFeeding(request: CreateFeedingRequestDto): Result<FeedingRecord?> {
        val token = sessionManager.token.value
        for (baseUrl in baseUrls) {
            try {
                val response = client.post("$baseUrl/api/feedings") {
                    contentType(ContentType.Application.Json)
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                    setBody(request)
                }
                if (response.status.value in 200..299) {
                    val dto: FeedingDetailResponseDto = response.body()
                    return Result.success(dto.toRecord())
                }
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
        return Result.failure(Exception("No se pudo conectar al servidor para registrar la alimentación"))
    }

    suspend fun getAvailableFeeds(): Result<List<FeedResponseDto>> {
        val token = sessionManager.token.value
        for (baseUrl in baseUrls) {
            try {
                val response = client.get("$baseUrl/api/feeds") {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dtos: List<FeedResponseDto> = response.body()
                    return Result.success(dtos)
                }
            } catch (_: Exception) {}
        }
        return Result.success(emptyList())
    }
}
