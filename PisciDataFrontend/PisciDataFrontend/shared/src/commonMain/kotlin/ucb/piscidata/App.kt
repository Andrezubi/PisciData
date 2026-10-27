package ucb.piscidata

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.koinInject
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.navigation.AppNavHost
import ucb.piscidata.session.SessionManager

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1577C8),
    secondary = Color(0xFF4EC5C1),
    background = Color(0xFFF5F8FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0B2B3B),
    onSurface = Color(0xFF0B2B3B)
)

@Composable
fun App() {
    val sessionManager = koinInject<SessionManager>()
    ApiClient.sessionManager = sessionManager
    MaterialTheme(colorScheme = LightColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost()
        }
    }
}
