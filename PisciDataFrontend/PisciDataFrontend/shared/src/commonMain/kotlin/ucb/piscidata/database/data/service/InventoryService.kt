package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.data.dto.FeedResponseDto
import ucb.piscidata.database.data.dto.SupplyResponseDto
import ucb.piscidata.database.data.mapper.toModel
import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.session.SessionManager

class InventoryService {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private val feedUrls = listOf(
        "https://127.0.0.1:7190/api/feeds",
        "http://127.0.0.1:5007/api/feeds"
    )

    private val supplyUrls = listOf(
        "https://127.0.0.1:7190/api/supplies",
        "http://127.0.0.1:5007/api/supplies"
    )

    suspend fun getInventory(): Result<List<InvItem>> {
        val token = sessionManager.token.value
        val items = mutableListOf<InvItem>()

        for (url in feedUrls) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dtos: List<FeedResponseDto> = response.body()
                    items.addAll(dtos.map { it.toModel() })
                    break
                }
            } catch (_: Exception) {}
        }

        for (url in supplyUrls) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dtos: List<SupplyResponseDto> = response.body()
                    items.addAll(dtos.map { it.toModel() })
                    break
                }
            } catch (_: Exception) {}
        }

        return Result.success(items)
    }

    suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
