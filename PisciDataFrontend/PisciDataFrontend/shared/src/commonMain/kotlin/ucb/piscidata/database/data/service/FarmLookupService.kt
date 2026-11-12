package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.data.dto.FarmResponseDto
import ucb.piscidata.database.domain.model.FarmModel
import ucb.piscidata.session.SessionManager

class FarmLookupService {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private val farmUrls = listOf(
        "https://127.0.0.1:7190/api/farms/my",
        "http://127.0.0.1:5007/api/farms/my"
    )

    suspend fun getFarms(): Result<List<FarmModel>> {
        val token = sessionManager.token.value
        for (url in farmUrls) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dtos: List<FarmResponseDto> = response.body()
                    val models = dtos.map { FarmModel(id = it.id, name = it.name) }
                    sessionManager.setFarms(models)
                    return Result.success(models)
                }
            } catch (_: Exception) {}
        }
        val fallbackFarms = listOf(
            FarmModel(1, "Piscigranja El Manantial"),
            FarmModel(2, "Piscigranja Norte - San Juan")
        )
        sessionManager.setFarms(fallbackFarms)
        return Result.success(fallbackFarms)
    }
}
