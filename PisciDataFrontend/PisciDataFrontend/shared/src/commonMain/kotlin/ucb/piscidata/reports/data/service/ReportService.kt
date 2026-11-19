package ucb.piscidata.reports.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.data.remote.RemoteBackendConfig
import ucb.piscidata.reports.data.dto.FarmReportDto
import ucb.piscidata.session.SessionManager

class ReportService {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    suspend fun getFarmReport(farmId: Int): Result<FarmReportDto> {
        if (farmId <= 0) {
            return Result.failure(Exception("Selecciona una granja para ver sus reportes."))
        }
        val token = sessionManager.token.value
        var lastError: Throwable? = null

        for (url in RemoteBackendConfig.urlsFor("/api/reports/farm/$farmId")) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (response.status.value in 200..299) {
                    return Result.success(response.body())
                }
                lastError = Exception("Error HTTP ${response.status.value} al cargar reportes")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("No se pudo conectar al servidor de reportes"))
    }
}

