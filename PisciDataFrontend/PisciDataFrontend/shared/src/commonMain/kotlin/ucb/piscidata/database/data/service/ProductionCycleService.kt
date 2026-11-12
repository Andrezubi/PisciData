package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.data.dto.CreateProductionCycleRequestDto
import ucb.piscidata.database.data.dto.ProductionCycleResponseDto
import ucb.piscidata.database.data.dto.UpdateProductionCycleRequestDto
import ucb.piscidata.database.data.mapper.toRecord
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.SpeciesModel
import ucb.piscidata.session.SessionManager

class ProductionCycleService {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }
    private val loadedCiclos = mutableListOf<CicloRecord>()

    private fun cyclesUrls(farmId: Int) = listOf(
        "https://127.0.0.1:7190/api/productioncycles/farm/$farmId",
        "http://127.0.0.1:5007/api/productioncycles/farm/$farmId"
    )

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

    suspend fun getActiveCycleDtos(farmId: Int): List<ProductionCycleResponseDto> {
        val token = sessionManager.token.value
        for (url in cyclesUrls(farmId)) {
            try {
                val resp = client.get(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (resp.status.value in 200..299) {
                    return resp.body()
                }
            } catch (_: Exception) {}
        }
        return emptyList()
    }

    suspend fun createProductionCycle(pondId: Int, speciesId: Int, startDate: String, initialFishCount: Int?, initialAverageWeightGrams: Double?, initialAgeDays: Int?, observations: String?): Result<Unit> {
        val token = sessionManager.token.value
        val dto = CreateProductionCycleRequestDto(
            pondId = pondId,
            speciesId = speciesId,
            startDate = startDate,
            initialFishCount = initialFishCount,
            initialAverageWeightGrams = initialAverageWeightGrams,
            initialAgeDays = initialAgeDays,
            observations = observations
        )
        val urls = listOf(
            "https://127.0.0.1:7190/api/productioncycles",
            "http://127.0.0.1:5007/api/productioncycles"
        )
        for (url in urls) {
            try {
                val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    setBody(dto)
                }
                if (response.status.value in 200..299) return Result.success(Unit)
            } catch (_: Exception) {}
        }
        return Result.success(Unit)
    }

    suspend fun finalizeProductionCycle(cycleId: Int, pondId: Int, speciesId: Int, startDate: String): Result<Unit> {
        val token = sessionManager.token.value
        val today = "2026-11-05"
        val dto = UpdateProductionCycleRequestDto(
            pondId = pondId,
            speciesId = speciesId,
            startDate = startDate,
            endDate = today
        )
        val urls = listOf(
            "https://127.0.0.1:7190/api/productioncycles/$cycleId",
            "http://127.0.0.1:5007/api/productioncycles/$cycleId"
        )
        for (url in urls) {
            try {
                val response = client.put(url) {
                    contentType(ContentType.Application.Json)
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    setBody(dto)
                }
                if (response.status.value in 200..299) return Result.success(Unit)
            } catch (_: Exception) {}
        }
        return Result.success(Unit)
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

    suspend fun getSpecies(): Result<List<SpeciesModel>> {
        return Result.success(
            listOf(
                SpeciesModel(1, "Tilapia nilótica"),
                SpeciesModel(2, "Trucha arcoíris"),
                SpeciesModel(3, "Cachama blanca"),
                SpeciesModel(4, "Pacú"),
                SpeciesModel(5, "Tambaquí")
            )
        )
    }
}
