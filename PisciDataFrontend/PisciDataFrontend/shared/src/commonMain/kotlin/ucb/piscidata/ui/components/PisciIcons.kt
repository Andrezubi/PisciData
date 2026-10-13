package ucb.piscidata.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ChatIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.2f)
            cubicTo(w * 0.15f, h * 0.1f, w * 0.25f, h * 0.05f, w * 0.5f, h * 0.05f)
            cubicTo(w * 0.75f, h * 0.05f, w * 0.85f, h * 0.1f, w * 0.85f, h * 0.2f)
            lineTo(w * 0.85f, h * 0.6f)
            cubicTo(w * 0.85f, h * 0.7f, w * 0.75f, h * 0.75f, w * 0.5f, h * 0.75f)
            lineTo(w * 0.35f, h * 0.75f)
            lineTo(w * 0.2f, h * 0.9f)
            lineTo(w * 0.2f, h * 0.75f)
            cubicTo(w * 0.15f, h * 0.75f, w * 0.15f, h * 0.7f, w * 0.15f, h * 0.6f)
            close()
        }
        drawPath(path, color, style = Stroke(width = 1.8.dp.toPx()))
    }
}

@Composable
fun DatabaseIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.6.dp.toPx()
        val layerH = h * 0.25f

        // Top cylinder
        drawOval(color, topLeft = Offset(0f, 0f), size = Size(w, layerH), style = Stroke(sw))
        // Middle cylinder
        drawOval(color, topLeft = Offset(0f, h * 0.35f), size = Size(w, layerH), style = Stroke(sw))
        // Bottom cylinder
        drawOval(color, topLeft = Offset(0f, h * 0.7f), size = Size(w, layerH), style = Stroke(sw))

        // Vertical connecting lines
        drawLine(color, Offset(0f, layerH * 0.5f), Offset(0f, h * 0.7f + layerH * 0.5f), strokeWidth = sw)
        drawLine(color, Offset(w, layerH * 0.5f), Offset(w, h * 0.7f + layerH * 0.5f), strokeWidth = sw)
    }
}

@Composable
fun TasksIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.8.dp.toPx()
        drawRect(color, topLeft = Offset(w * 0.2f, h * 0.15f), size = Size(w * 0.6f, h * 0.75f), style = Stroke(sw))
        drawLine(color, Offset(w * 0.35f, h * 0.4f), Offset(w * 0.45f, h * 0.5f), strokeWidth = sw)
        drawLine(color, Offset(w * 0.45f, h * 0.5f), Offset(w * 0.65f, h * 0.3f), strokeWidth = sw)
        drawLine(color, Offset(w * 0.35f, h * 0.7f), Offset(w * 0.65f, h * 0.7f), strokeWidth = sw)
    }
}

@Composable
fun ReportsIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawRect(color, topLeft = Offset(w * 0.15f, h * 0.5f), size = Size(w * 0.2f, h * 0.4f))
        drawRect(color, topLeft = Offset(w * 0.4f, h * 0.25f), size = Size(w * 0.2f, h * 0.65f))
        drawRect(color, topLeft = Offset(w * 0.65f, h * 0.1f), size = Size(w * 0.2f, h * 0.8f))
    }
}

@Composable
fun ProfileIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sw = 1.8.dp.toPx()
        drawCircle(color, radius = w * 0.22f, center = Offset(w * 0.5f, h * 0.35f), style = Stroke(sw))
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            cubicTo(w * 0.15f, h * 0.55f, w * 0.35f, h * 0.55f, w * 0.5f, h * 0.55f)
            cubicTo(w * 0.65f, h * 0.55f, w * 0.85f, h * 0.55f, w * 0.85f, h * 0.85f)
        }
        drawPath(path, color, style = Stroke(width = sw))
    }
}

@Composable
fun EditIcon(color: Color, modifier: Modifier = Modifier.size(16.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.1f, h * 0.9f)
            lineTo(w * 0.3f, h * 0.85f)
            lineTo(w * 0.9f, h * 0.25f)
            lineTo(w * 0.75f, h * 0.1f)
            lineTo(w * 0.15f, h * 0.7f)
            close()
        }
        drawPath(path, color, style = Stroke(width = 1.5.dp.toPx()))
    }
}

@Composable
fun DeleteIcon(color: Color, modifier: Modifier = Modifier.size(16.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawRect(color, topLeft = Offset(w * 0.2f, h * 0.3f), size = Size(w * 0.6f, h * 0.65f), style = Stroke(1.5.dp.toPx()))
        drawLine(color, Offset(w * 0.1f, h * 0.3f), Offset(w * 0.9f, h * 0.3f), strokeWidth = 1.5.dp.toPx())
        drawLine(color, Offset(w * 0.35f, h * 0.15f), Offset(w * 0.65f, h * 0.15f), strokeWidth = 1.5.dp.toPx())
    }
}

@Composable
fun MicIcon(color: Color, modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawRoundRect(color, topLeft = Offset(w * 0.35f, h * 0.1f), size = Size(w * 0.3f, h * 0.5f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.15f), style = Stroke(2.dp.toPx()))
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.45f)
            cubicTo(w * 0.2f, h * 0.75f, w * 0.8f, h * 0.75f, w * 0.8f, h * 0.45f)
        }
        drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
        drawLine(color, Offset(w * 0.5f, h * 0.75f), Offset(w * 0.5f, h * 0.9f), strokeWidth = 2.dp.toPx())
    }
}

@Composable
fun SendIcon(color: Color, modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.1f, h * 0.1f)
            lineTo(w * 0.9f, h * 0.5f)
            lineTo(w * 0.1f, h * 0.9f)
            lineTo(w * 0.25f, h * 0.5f)
            close()
        }
        drawPath(path, color)
    }
}
