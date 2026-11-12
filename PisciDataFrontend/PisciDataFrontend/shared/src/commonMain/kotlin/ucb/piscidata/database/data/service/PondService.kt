package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.data.dto.PondResponseDto
import ucb.piscidata.database.data.mapper.toModel
import ucb.piscidata.database.domain.model.Pond
import ucb.piscidata.session.SessionManager

class PondService(
    private val productionCycleService: ProductionCycleService
) {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private val pondUrls = listOf(
        "https://127.0.0.1:7190/api/ponds",
        "http://127.0.0.1:5007/api/ponds"
    )

    suspend fun getPonds(): Result<List<Pond>> {
        val token = sessionManager.token.value
        val farmId = sessionManager.selectedFarmId.value

        val activeCycles = productionCycleService.getActiveCycleDtos(farmId)

        for (url in pondUrls) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dtos: List<PondResponseDto> = response.body()
                    val farmPonds = dtos.filter { it.farmId == farmId }
                    return Result.success(farmPonds.map { it.toModel(activeCycles) })
                }
            } catch (_: Exception) {}
        }
        return Result.success(emptyList())
    }
}
