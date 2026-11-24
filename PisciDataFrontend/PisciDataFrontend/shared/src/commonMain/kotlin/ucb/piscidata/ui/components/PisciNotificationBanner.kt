package ucb.piscidata.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NotificationKind {
    WARNING,
    NETWORK_ERROR,
    SUCCESS
}

fun classifyNotification(message: String?): NotificationKind {
    val m = message?.lowercase().orEmpty()
    return when {
        m.contains("conectar") || m.contains("conexión") || m.contains("servidor") || m.contains("internet") ->
            NotificationKind.NETWORK_ERROR
        else -> NotificationKind.WARNING
    }
}

@Composable
fun FloatingNotificationBanner(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = !message.isNullOrBlank()
    val kind = classifyNotification(message)

    val accentColor = when (kind) {
        NotificationKind.WARNING -> Color(0xFFD97706)       // Ámbar cálido (credenciales / datos)
        NotificationKind.NETWORK_ERROR -> Color(0xFFE05A5A) // Coral alerta (red / servidor)
        NotificationKind.SUCCESS -> Color(0xFF19A974)       // Verde éxito
    }
    val containerBg = when (kind) {
        NotificationKind.WARNING -> Color(0xFFFFFBEB)
        NotificationKind.NETWORK_ERROR -> Color(0xFFFFF2F2)
        NotificationKind.SUCCESS -> Color(0xFFEAFBF3)
    }
    val borderColor = when (kind) {
        NotificationKind.WARNING -> Color(0xFFFDE68A)
        NotificationKind.NETWORK_ERROR -> Color(0xFFFECACA)
        NotificationKind.SUCCESS -> Color(0xFFA7F3D0)
    }
    val title = when (kind) {
        NotificationKind.WARNING -> "Verifica tus datos"
        NotificationKind.NETWORK_ERROR -> "Problema de conexión"
        NotificationKind.SUCCESS -> "Listo"
    }
    val badgeSymbol = when (kind) {
        NotificationKind.WARNING -> "!"
        NotificationKind.NETWORK_ERROR -> "⚡"
        NotificationKind.SUCCESS -> "✓"
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(animationSpec = tween(280)) { -it } + fadeIn(tween(240)),
        exit = slideOutVertically(animationSpec = tween(220)) { -it } + fadeOut(tween(180)),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = containerBg),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, borderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = accentColor,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = badgeSymbol,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0B2B3B)
                    )
                    Text(
                        text = message.orEmpty(),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF374151)
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("✕", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B818B))
                }
            }
        }
    }
}

