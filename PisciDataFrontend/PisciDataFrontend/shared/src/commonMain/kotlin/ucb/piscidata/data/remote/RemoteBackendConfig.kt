package ucb.piscidata.data.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Configuración centralizada para conexión remota al backend de PisciData.
 *
 * Cómo usarla sin cable USB ni `adb reverse`:
 *  1) Coloca aquí tu URL remota (túnel Cloudflare/ngrok, IP Wi-Fi local o VPS),
 *     por ejemplo: "https://mi-tunel.trycloudflare.com" o "http://192.168.1.50:5007"
 *  2) O cámbiala en tiempo real desde la pantalla de Perfil -> "Conexión remota".
 *
 * Los puertos locales (`https://127.0.0.1:7190` y `http://127.0.0.1:5007`) se mantienen
 * siempre como respaldo automático si no hay URL remota configurada o si falla.
 */
object RemoteBackendConfig {
    // URL remota en Railway (producción 24/7). Si falla o se borra, cae automáticamente a 127.0.0.1:5007
    const val DEFAULT_REMOTE_URL: String = "https://piscidatabackend-production.up.railway.app"

    val LOCAL_FALLBACK_URLS: List<String> = listOf(
        "https://127.0.0.1:7190",
        "http://127.0.0.1:5007"
    )

    private val _remoteBaseUrl = MutableStateFlow(normalizeUrl(DEFAULT_REMOTE_URL))
    val remoteBaseUrl: StateFlow<String> = _remoteBaseUrl.asStateFlow()

    val isRemoteEnabled: Boolean
        get() = _remoteBaseUrl.value.isNotBlank()

    val activeBaseUrl: String
        get() = _remoteBaseUrl.value.ifBlank { "http://127.0.0.1:5007" }

    fun setRemoteBaseUrl(url: String) {
        _remoteBaseUrl.value = normalizeUrl(url)
    }

    /**
     * Devuelve la lista ordenada de URLs candidatas para un endpoint (ej. "/api/tasks").
     * Si hay URL remota configurada, se intenta primero y se conservan los puertos locales como fallback.
     */
    fun urlsFor(path: String): List<String> {
        val cleanPath = if (path.startsWith("/")) path else "/$path"
        val remote = _remoteBaseUrl.value
        return buildList {
            if (remote.isNotBlank()) {
                add("$remote$cleanPath")
            }
            LOCAL_FALLBACK_URLS.forEach { base ->
                add("$base$cleanPath")
            }
        }
    }

    private fun normalizeUrl(raw: String): String {
        val trimmed = raw.trim().trimEnd('/')
        if (trimmed.isBlank()) return ""
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }
}

