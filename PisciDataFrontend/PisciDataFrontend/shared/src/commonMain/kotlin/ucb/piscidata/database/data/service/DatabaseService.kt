package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.data.dto.FarmResponseDto
import ucb.piscidata.database.data.dto.ProductionCycleResponseDto
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
        return Result.success(
            listOf(
                Pond(1, "EST-01", "Estanque 1", PondShape.RECTANGULAR, largo = 40.0, ancho = 20.0, area = 800.0, profundidad = 1.8, transparenciaCm = 45.0, ph = 7.2, claridad = "Alta", temperatura = 24.0, oxigeno = 6.8, cicloActivo = "CP-2026-017", especie = "Trucha arcoíris"),
                Pond(2, "EST-02", "Estanque 2", PondShape.RECTANGULAR, largo = 35.0, ancho = 18.0, area = 630.0, profundidad = 1.6, transparenciaCm = 50.0, ph = 7.0, claridad = "Media", temperatura = 25.0, oxigeno = 6.2, cicloActivo = null, especie = null)
            )
        )
    }

    suspend fun getInventory(): Result<List<InvItem>> {
        return Result.success(
            listOf(
                InvItem(1, "Alimento F1", InvCategory.ALIMENTO, 320, "kg", 100, proteinPercentage = 45.0, pelletSizeMm = 2.0),
                InvItem(2, "Alimento F2", InvCategory.ALIMENTO, 200, "kg", 80, proteinPercentage = 40.0, pelletSizeMm = 4.0)
            )
        )
    }

    suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
