package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.database.data.dto.FarmResponseDto
import ucb.piscidata.database.data.dto.ProductionCycleResponseDto
import ucb.piscidata.database.data.mapper.toRecord
import ucb.piscidata.database.domain.model.*

class DatabaseService {
    private val client = ApiClient.client

    private val farmUrls = listOf(
        "https://127.0.0.1:7190/api/farms",
        "http://127.0.0.1:5007/api/farms"
    )

    private fun cyclesUrls(farmId: Int) = listOf(
        "https://127.0.0.1:7190/api/productioncycles/farm/$farmId",
        "http://127.0.0.1:5007/api/productioncycles/farm/$farmId"
    )

    suspend fun getFarms(): Result<List<FarmModel>> {
        for (url in farmUrls) {
            try {
                val response = client.get(url)
                if (response.status.value in 200..299) {
                    val dtos: List<FarmResponseDto> = response.body()
                    val models = dtos.map { FarmModel(id = it.id, name = it.name) }
                    return Result.success(models)
                }
            } catch (_: Exception) {}
        }
        return Result.success(
            listOf(
                FarmModel(1, "Piscigranja El Manantial"),
                FarmModel(2, "Piscigranja Norte - San Juan")
            )
        )
    }

    suspend fun getCiclos(farmId: Int): Result<List<CicloRecord>> {
        for (url in cyclesUrls(farmId)) {
            try {
                val response = client.get(url)
                if (response.status.value in 200..299) {
                    val dtos: List<ProductionCycleResponseDto> = response.body()
                    val records = dtos.map { it.toRecord() }
                    return Result.success(records)
                }
            } catch (_: Exception) {}
        }
        return Result.success(
            listOf(
                CicloRecord(
                    id = "CP-2026-018",
                    status = BadgeVariant.ACTIVO,
                    estanque = "EST-03 - Estanque 3",
                    especie = "Tilapia nilótica",
                    peces = "15.400",
                    pesoActual = "145.2 g",
                    edadDias = 56,
                    startDate = "2026-08-12",
                    endDate = null,
                    initialAgeDays = 15,
                    initialWeightGrams = "12 g",
                    initialFishCount = "16.000",
                    observations = "Crecimiento óptimo, consumo estable.",
                    isExpanded = false
                )
            )
        )
    }

    suspend fun saveCiclo(ciclo: CicloRecord): Result<Unit> {
        return Result.success(Unit)
    }

    suspend fun deleteCiclo(id: String): Result<Unit> {
        return Result.success(Unit)
    }

    suspend fun toggleCicloExpand(id: String): Result<List<CicloRecord>> {
        return getCiclos(1)
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
