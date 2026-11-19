package ucb.piscidata

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.navigation.AppNavHost
import ucb.piscidata.session.SessionManager

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1577C8),       // Azul Primario
    secondary = Color(0xFF4EC5C1),     // Turquesa Agua
    tertiary = Color(0xFF19A974),      // Verde Vida / Éxito
    background = Color(0xFFF5F8FA),    // Fondo general
    surface = Color.White,
    error = Color(0xFFE05A5A),         // Alerta / Advertencia
    onPrimary = Color.White,
    onSecondary = Color(0xFF0B2B3B),
    onBackground = Color(0xFF0B2B3B),  // Agua Profunda
    onSurface = Color(0xFF0B2B3B)
)

private val PisciDataTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp,
        color = Color(0xFF0B2B3B)
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = (-0.3).sp,
        color = Color(0xFF0B2B3B)
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = Color(0xFF16313D)
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        color = Color(0xFF16313D)
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.2.sp
    )
)

@Composable
fun App() {
    val sessionManager = koinInject<SessionManager>()
    ApiClient.sessionManager = sessionManager
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = PisciDataTypography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost()
        }
    }
}
