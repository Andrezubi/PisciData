package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.data.dto.FarmResponseDto
import ucb.piscidata.database.data.dto.FeedResponseDto
import ucb.piscidata.database.data.dto.PondResponseDto
import ucb.piscidata.database.data.dto.ProductionCycleResponseDto
import ucb.piscidata.database.data.dto.SupplyResponseDto
import ucb.piscidata.database.data.mapper.toModel
import ucb.piscidata.database.data.mapper.toRecord
import ucb.piscidata.database.domain.model.*
import ucb.piscidata.session.SessionManager

class DatabaseService {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }
    private val loadedCiclos = mutableListOf<CicloRecord>()

    private val farmUrls = listOf(
        "https://127.0.0.1:7190/api/farms/my",
        "http://127.0.0.1:5007/api/farms/my"
    )

    private fun cyclesUrls(farmId: Int) = listOf(
        "https://127.0.0.1:7190/api/productioncycles/farm/$farmId",
        "http://127.0.0.1:5007/api/productioncycles/farm/$farmId"
    )

    private val pondUrls = listOf(
        "https://127.0.0.1:7190/api/ponds",
        "http://127.0.0.1:5007/api/ponds"
    )

    private val feedUrls = listOf(
        "https://127.0.0.1:7190/api/feeds",
        "http://127.0.0.1:5007/api/feeds"
    )

    private val supplyUrls = listOf(
        "https://127.0.0.1:7190/api/supplies",
        "http://127.0.0.1:5007/api/supplies"
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

    suspend fun getCiclos(farmId: Int): Result<List<CicloRecord>> {
        val token = sessionManager.token.value
        for (url in cyclesUrls(farmId)) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dtos: List<ProductionCycleResponseDto> = response.body()
                    val records = dtos.map { dto ->
                        val existing = loadedCiclos.find { it.id == "CP-2026-${dto.id.toString().padStart(3, '0')}" }
                        dto.toRecord().copy(isExpanded = existing?.isExpanded ?: false)
                    }
                    loadedCiclos.clear()
                    loadedCiclos.addAll(records)
                    return Result.success(loadedCiclos.toList())
                }
            } catch (_: Exception) {}
        }
        return Result.success(emptyList())
    }

    suspend fun saveCiclo(ciclo: CicloRecord): Result<Unit> {
        return Result.success(Unit)
    }

    suspend fun deleteCiclo(id: String): Result<Unit> {
        loadedCiclos.removeAll { it.id == id }
        return Result.success(Unit)
    }

    suspend fun toggleCicloExpand(id: String): Result<List<CicloRecord>> {
        val idx = loadedCiclos.indexOfFirst { it.id == id }
        if (idx >= 0) {
            loadedCiclos[idx] = loadedCiclos[idx].copy(isExpanded = !loadedCiclos[idx].isExpanded)
        }
        return Result.success(loadedCiclos.toList())
    }

    suspend fun getPonds(): Result<List<Pond>> {
        val token = sessionManager.token.value
        val farmId = sessionManager.selectedFarmId.value

        // Fetch active cycles for this farm so we can match them to ponds
        var activeCycles = listOf<ProductionCycleResponseDto>()
        for (url in cyclesUrls(farmId)) {
            try {
                val resp = client.get(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (resp.status.value in 200..299) {
                    activeCycles = resp.body()
                    break
                }
            } catch (_: Exception) {}
        }

        for (url in pondUrls) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val dtos: List<PondResponseDto> = response.body()
                    // Filter ponds belonging to current farm if FarmId matches
                    val farmPonds = dtos.filter { it.farmId == farmId }
                    return Result.success(farmPonds.map { it.toModel(activeCycles) })
                }
            } catch (_: Exception) {}
        }
        return Result.success(emptyList())
    }

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
