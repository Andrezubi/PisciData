package ucb.piscidata.data.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Configuración unificada de conexión al backend (compatible con el flujo local del equipo y nube 24/7):
 *
 * - Intenta primero `127.0.0.1:5007` (si un compañero tiene `adb reverse tcp:5007 tcp:5007` activo,
 *   conecta en <2 ms a su propio backend local).
 * - Si no hay cable USB / `adb reverse` (falla instantáneo con Connection Refused en <2 ms),
 *   usa automáticamente el servidor en la nube 24/7 en Railway (`CLOUD_BASE_URL`).
 */
object RemoteBackendConfig {
    const val CLOUD_BASE_URL: String = "https://piscidatabackend-production.up.railway.app"

    val LOCAL_URLS: List<String> = listOf(
        "http://127.0.0.1:5007",
        "https://127.0.0.1:7190"
    )

    private val _useLocalOnly = MutableStateFlow(false)
    val useLocalOnly: StateFlow<Boolean> = _useLocalOnly.asStateFlow()

    val activeBaseUrl: String
        get() = if (_useLocalOnly.value) "http://127.0.0.1:5007" else CLOUD_BASE_URL

    fun setUseLocalOnly(localOnly: Boolean) {
        _useLocalOnly.value = localOnly
    }

    fun baseUrls(): List<String> = buildList {
        LOCAL_URLS.forEach { add(it) }
        if (!_useLocalOnly.value && CLOUD_BASE_URL.isNotBlank()) {
            add(CLOUD_BASE_URL)
        }
    }

    fun urlsFor(path: String): List<String> {
        val cleanPath = if (path.startsWith("/")) path else "/$path"
        return baseUrls().map { "$it$cleanPath" }
    }
}
