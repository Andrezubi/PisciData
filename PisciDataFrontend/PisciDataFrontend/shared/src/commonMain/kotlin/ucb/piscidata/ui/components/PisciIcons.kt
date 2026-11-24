package ucb.piscidata.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import piscidatafrontend.shared.generated.resources.Res
import piscidatafrontend.shared.generated.resources.logo_piscidata_dark
import piscidatafrontend.shared.generated.resources.logo_piscidata_light

@Composable
fun PisciDataLogo(
    darkTheme: Boolean = false,
    modifier: Modifier = Modifier.size(36.dp)
) {
    val resource = if (darkTheme) Res.drawable.logo_piscidata_dark else Res.drawable.logo_piscidata_light
    Image(
        painter = painterResource(resource),
        contentDescription = "PisciData Logo",
        modifier = modifier.clip(RoundedCornerShape(10.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ChatIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.9.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.14f, h * 0.24f)
            cubicTo(w * 0.14f, h * 0.14f, w * 0.24f, h * 0.10f, w * 0.50f, h * 0.10f)
            cubicTo(w * 0.76f, h * 0.10f, w * 0.86f, h * 0.14f, w * 0.86f, h * 0.24f)
            lineTo(w * 0.86f, h * 0.58f)
            cubicTo(w * 0.86f, h * 0.68f, w * 0.76f, h * 0.72f, w * 0.52f, h * 0.72f)
            lineTo(w * 0.34f, h * 0.72f)
            lineTo(w * 0.18f, h * 0.88f)
            lineTo(w * 0.20f, h * 0.72f)
            cubicTo(w * 0.14f, h * 0.70f, w * 0.14f, h * 0.65f, w * 0.14f, h * 0.58f)
            close()
        }
        drawPath(path, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun DatabaseIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.9.dp.toPx()
        // Modern 2x2 grid icon (clean Linear/Notion style for farm modules)
        val box = w * 0.34f
        val r = CornerRadius(3.dp.toPx())
        drawRoundRect(color, Offset(w * 0.12f, h * 0.12f), Size(box, box), r, style = Stroke(sw))
        drawRoundRect(color, Offset(w * 0.54f, h * 0.12f), Size(box, box), r, style = Stroke(sw))
        drawRoundRect(color, Offset(w * 0.12f, h * 0.54f), Size(box, box), r, style = Stroke(sw))
        drawRoundRect(color, Offset(w * 0.54f, h * 0.54f), Size(box, box), r, style = Stroke(sw))
    }
}

@Composable
fun TasksIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.9.dp.toPx()
        drawCircle(color, radius = w * 0.38f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(sw))
        val check = Path().apply {
            moveTo(w * 0.34f, h * 0.51f)
            lineTo(w * 0.45f, h * 0.62f)
            lineTo(w * 0.67f, h * 0.39f)
        }
        drawPath(check, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ReportsIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val r = CornerRadius(2.5.dp.toPx())
        drawRoundRect(color, topLeft = Offset(w * 0.14f, h * 0.52f), size = Size(w * 0.18f, h * 0.34f), cornerRadius = r)
        drawRoundRect(color, topLeft = Offset(w * 0.41f, h * 0.30f), size = Size(w * 0.18f, h * 0.56f), cornerRadius = r)
        drawRoundRect(color, topLeft = Offset(w * 0.68f, h * 0.14f), size = Size(w * 0.18f, h * 0.72f), cornerRadius = r)
    }
}

@Composable
fun ProfileIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.9.dp.toPx()
        drawCircle(color, radius = w * 0.20f, center = Offset(w * 0.5f, h * 0.32f), style = Stroke(sw))
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.84f)
            cubicTo(w * 0.18f, h * 0.58f, w * 0.34f, h * 0.56f, w * 0.5f, h * 0.56f)
            cubicTo(w * 0.66f, h * 0.56f, w * 0.82f, h * 0.58f, w * 0.82f, h * 0.84f)
        }
        drawPath(path, color, style = Stroke(width = sw, cap = StrokeCap.Round))
    }
}

@Composable
fun SearchIcon(color: Color, modifier: Modifier = Modifier.size(19.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.9.dp.toPx()
        drawCircle(color, radius = w * 0.32f, center = Offset(w * 0.44f, h * 0.44f), style = Stroke(sw))
        drawLine(color, Offset(w * 0.67f, h * 0.67f), Offset(w * 0.88f, h * 0.88f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
fun PlusIcon(color: Color, modifier: Modifier = Modifier.size(18.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 2.1.dp.toPx()
        drawLine(color, Offset(w * 0.5f, h * 0.18f), Offset(w * 0.5f, h * 0.82f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.18f, h * 0.5f), Offset(w * 0.82f, h * 0.5f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
fun MenuLinesIcon(color: Color, modifier: Modifier = Modifier.size(19.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.9.dp.toPx()
        drawLine(color, Offset(w * 0.16f, h * 0.28f), Offset(w * 0.84f, h * 0.28f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.16f, h * 0.50f), Offset(w * 0.64f, h * 0.50f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.16f, h * 0.72f), Offset(w * 0.76f, h * 0.72f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
fun RefreshIcon(color: Color, modifier: Modifier = Modifier.size(18.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.8.dp.toPx()
        drawArc(
            color = color,
            startAngle = -35f,
            sweepAngle = 285f,
            useCenter = false,
            topLeft = Offset(w * 0.16f, h * 0.16f),
            size = Size(w * 0.68f, h * 0.68f),
            style = Stroke(width = sw, cap = StrokeCap.Round)
        )
        val arrow = Path().apply {
            moveTo(w * 0.62f, h * 0.14f)
            lineTo(w * 0.84f, h * 0.26f)
            lineTo(w * 0.66f, h * 0.39f)
        }
        drawPath(arrow, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun EditIcon(color: Color, modifier: Modifier = Modifier.size(16.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.12f, h * 0.88f)
            lineTo(w * 0.30f, h * 0.84f)
            lineTo(w * 0.88f, h * 0.26f)
            lineTo(w * 0.74f, h * 0.12f)
            lineTo(w * 0.16f, h * 0.70f)
            close()
        }
        drawPath(path, color, style = Stroke(width = 1.6.dp.toPx(), join = StrokeJoin.Round))
    }
}

@Composable
fun DeleteIcon(color: Color, modifier: Modifier = Modifier.size(16.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.6.dp.toPx()
        drawRoundRect(color, topLeft = Offset(w * 0.22f, h * 0.28f), size = Size(w * 0.56f, h * 0.62f), cornerRadius = CornerRadius(2.dp.toPx()), style = Stroke(sw))
        drawLine(color, Offset(w * 0.12f, h * 0.28f), Offset(w * 0.88f, h * 0.28f), strokeWidth = sw, cap = StrokeCap.Round)
        drawLine(color, Offset(w * 0.36f, h * 0.15f), Offset(w * 0.64f, h * 0.15f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
fun MicIcon(color: Color, modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.9.dp.toPx()
        drawRoundRect(color, topLeft = Offset(w * 0.35f, h * 0.10f), size = Size(w * 0.30f, h * 0.48f), cornerRadius = CornerRadius(w * 0.15f), style = Stroke(sw))
        val path = Path().apply {
            moveTo(w * 0.20f, h * 0.45f)
            cubicTo(w * 0.20f, h * 0.74f, w * 0.80f, h * 0.74f, w * 0.80f, h * 0.45f)
        }
        drawPath(path, color, style = Stroke(width = sw, cap = StrokeCap.Round))
        drawLine(color, Offset(w * 0.5f, h * 0.74f), Offset(w * 0.5f, h * 0.90f), strokeWidth = sw, cap = StrokeCap.Round)
    }
}

@Composable
fun SendIcon(color: Color, modifier: Modifier = Modifier.size(18.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 2.1.dp.toPx()
        // Crisp upward arrow (ChatGPT/Claude send button style)
        drawLine(color, Offset(w * 0.5f, h * 0.82f), Offset(w * 0.5f, h * 0.20f), strokeWidth = sw, cap = StrokeCap.Round)
        val head = Path().apply {
            moveTo(w * 0.24f, h * 0.46f)
            lineTo(w * 0.50f, h * 0.18f)
            lineTo(w * 0.76f, h * 0.46f)
        }
        drawPath(head, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
