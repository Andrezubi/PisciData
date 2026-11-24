package ucb.piscidata.database.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.data.remote.RemoteBackendConfig
import ucb.piscidata.database.data.dto.CreateWaterQualityRequestDto
import ucb.piscidata.database.data.dto.ProductionCycleResponseDto
import ucb.piscidata.database.data.dto.WaterQualityDetailResponseDto
import ucb.piscidata.database.data.dto.WaterQualityReferenceResponseDto
import ucb.piscidata.database.data.mapper.toModel
import ucb.piscidata.database.data.mapper.toRecord
import ucb.piscidata.database.domain.model.WaterQualityRecord
import ucb.piscidata.database.domain.model.WaterQualityReferenceModel
import ucb.piscidata.session.SessionManager

class WaterQualityService {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    suspend fun getWaterQualityByFarm(farmId: Int): Result<List<WaterQualityRecord>> {
        val token = sessionManager.token.value
        val references = getReferences().getOrDefault(emptyList())

        val paths = listOf(
            "/api/WaterQuality/farm/$farmId",
            "/api/waterquality/farm/$farmId",
            "/api/Farms/$farmId/waterquality",
            "/api/WaterQuality",
            "/api/waterquality",
            "/api/WaterQualities",
            "/api/waterqualities"
        )

        for (path in paths) {
            val urls = RemoteBackendConfig.urlsFor(path)
            for (url in urls) {
                try {
                    val response = client.get(url) {
                        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    }
                    if (response.status.value in 200..299) {
                        val dtos: List<WaterQualityDetailResponseDto> = response.body()
                        if (dtos.isNotEmpty()) {
                            return Result.success(dtos.map { it.toRecord(references) })
                        }
                    }
                } catch (_: Exception) {}
            }
        }

        // Fallback: fetch production cycles for this farm and query water quality per cycle
        try {
            val cycleUrls = RemoteBackendConfig.urlsFor("/api/productioncycles/farm/$farmId")
            for (url in cycleUrls) {
                try {
                    val cycleResponse = client.get(url) {
                        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    }
                    if (cycleResponse.status.value in 200..299) {
                        val cycles: List<ProductionCycleResponseDto> = cycleResponse.body()
                        val allRecords = mutableListOf<WaterQualityRecord>()
                        for (cycle in cycles) {
                            val cycleRecords = getWaterQualityByCycle(cycle.id).getOrDefault(emptyList())
                            allRecords.addAll(cycleRecords)
                        }
                        if (allRecords.isNotEmpty()) {
                            return Result.success(allRecords.distinctBy { it.id })
                        }
                    }
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}

        return Result.success(emptyList())
    }

    suspend fun getWaterQualityByCycle(cycleId: Int): Result<List<WaterQualityRecord>> {
        val token = sessionManager.token.value
        val references = getReferences().getOrDefault(emptyList())

        val urls = listOf(
            "/api/WaterQuality/cycle/$cycleId",
            "/api/waterquality/cycle/$cycleId",
            "/api/ProductionCycles/$cycleId/waterquality",
            "/api/WaterQuality",
            "/api/waterquality",
            "/api/WaterQualities"
        )

        for (path in urls) {
            val endpointUrls = RemoteBackendConfig.urlsFor(path)
            for (url in endpointUrls) {
                try {
                    val response = client.get(url) {
                        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    }
                    if (response.status.value in 200..299) {
                        val dtos: List<WaterQualityDetailResponseDto> = response.body()
                        val filtered = if (path.contains("cycle") || path.contains("ProductionCycles")) dtos else dtos.filter { it.productionCycleId == cycleId }
                        if (filtered.isNotEmpty()) {
                            return Result.success(filtered.map { it.toRecord(references) })
                        }
                    }
                } catch (_: Exception) {}
            }
        }
        return Result.success(emptyList())
    }

    suspend fun getReferences(): Result<List<WaterQualityReferenceModel>> {
        val token = sessionManager.token.value
        val paths = listOf(
            "/api/WaterQuality/references",
            "/api/waterquality/references",
            "/api/WaterQualityReferences",
            "/api/waterqualityreferences",
            "/api/water-quality-references"
        )
        for (path in paths) {
            val urls = RemoteBackendConfig.urlsFor(path)
            for (url in urls) {
                try {
                    val response = client.get(url) {
                        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    }
                    if (response.status.value in 200..299) {
                        val dtos: List<WaterQualityReferenceResponseDto> = response.body()
                        return Result.success(dtos.map { it.toModel() })
                    }
                } catch (_: Exception) {}
            }
        }
        return Result.success(emptyList())
    }

    suspend fun createWaterQuality(request: CreateWaterQualityRequestDto): Result<WaterQualityRecord?> {
        val token = sessionManager.token.value
        if (token.isNullOrBlank()) {
            return Result.failure(Exception("Tu sesión expiró. Inicia sesión nuevamente."))
        }
        val paths = listOf("/api/waterquality")
        val references = getReferences().getOrDefault(emptyList())

        for (path in paths) {
            val urls = RemoteBackendConfig.urlsFor(path)
            for (url in urls) {
                try {
                    val response = client.post(url) {
                        contentType(ContentType.Application.Json)
                        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                        setBody(request)
                    }
                    if (response.status.value !in 200..299) {
                        val responseBody = response.bodyAsText()
                        return Result.failure(
                            Exception(responseBody.toSpanishApiError(response.status.value))
                        )
                    }
                    if (response.status.value in 200..299) {
                        val dto: WaterQualityDetailResponseDto = response.body()
                        return Result.success(dto.toRecord(references))
                    }
                } catch (_: Exception) {}
            }
        }
        return Result.failure(
            Exception("No se pudo conectar al servidor para registrar la calidad del agua. Verifica tu conexión e inténtalo nuevamente.")
        )
    }

    private fun String.toSpanishApiError(statusCode: Int): String {
        val fallback = when (statusCode) {
            400 -> "Revisa los datos ingresados y vuelve a intentarlo."
            401 -> "Tu sesión expiró. Inicia sesión nuevamente."
            404 -> "El estanque o ciclo seleccionado ya no está disponible."
            else -> "No se pudo registrar la medición. Inténtalo nuevamente."
        }
        return try {
            val payload = Json.parseToJsonElement(this).jsonObject
            val message = payload["message"]?.jsonPrimitive?.content
            val errors = payload["errors"]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.content }
                ?.joinToString(" ")
                .orEmpty()
            listOfNotNull(message, errors.takeIf { it.isNotBlank() })
                .joinToString(" ")
                .ifBlank { fallback }
        } catch (_: Exception) {
            fallback
        }
    }
}
