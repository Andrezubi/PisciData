package ucb.piscidata

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ucb.piscidata.navigation.AppNavHost

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
    MaterialTheme(colorScheme = LightColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost()
        }
    }
}
