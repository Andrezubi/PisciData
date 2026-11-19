package ucb.piscidata.database.presentation.screen.estanques

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.presentation.viewmodel.DatabaseEvent
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel
import kotlin.math.PI
import kotlin.math.round

@Composable
fun PondFormScreen(
    viewModel: DatabaseViewModel,
    onCancel: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var shape by remember { mutableStateOf("RECTANGULAR") }
    var depth by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var diameter by remember { mutableStateOf("") }
    var areaManual by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var shapeExpanded by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Automatic calculation of area based on selected shape
    val calculatedArea: Double? = when (shape) {
        "RECTANGULAR" -> {
            val l = length.toDoubleOrNull()
            val w = width.toDoubleOrNull()
            if (l != null && w != null && l > 0 && w > 0) l * w else null
        }
        "CIRCULAR" -> {
            val d = diameter.toDoubleOrNull()
            if (d != null && d > 0) {
                val radius = d / 2.0
                val a = PI * radius * radius
                round(a * 100.0) / 100.0
            } else null
        }
        else -> areaManual.toDoubleOrNull()
    }

    val displayAreaText = when (shape) {
        "IRREGULAR" -> areaManual
        else -> calculatedArea?.toString() ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("← Cancelar", color = Color(0xFF1577C8))
            }
            Text(
                text = "Nuevo Estanque",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B2B3B)
            )
            Button(
                onClick = {
                    if (code.isBlank()) {
                        errorMsg = "El código de estanque es obligatorio"
                        return@Button
                    }
                    val finalArea = calculatedArea
                    if (shape == "IRREGULAR" && finalArea == null) {
                        errorMsg = "Ingrese el área en m² para estanque irregular"
                        return@Button
                    }
                    viewModel.emitEvent(
                        DatabaseEvent.CreatePond(
                            code = code.trim(),
                            name = name.ifBlank { null },
                            shape = shape,
                            width = width.toDoubleOrNull(),
                            length = length.toDoubleOrNull(),
                            diameter = diameter.toDoubleOrNull(),
                            depth = depth.toDoubleOrNull(),
                            area = finalArea,
                            description = description.ifBlank { null }
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        }

        if (errorMsg != null) {
            Text(errorMsg!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it; errorMsg = null },
                label = { Text("Código de estanque (ej. EST-01) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del estanque (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            // Shape Selector Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = shape,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Forma del estanque") },
                    trailingIcon = { TextButton(onClick = { shapeExpanded = true }) { Text("▾") } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                DropdownMenu(
                    expanded = shapeExpanded,
                    onDismissRequest = { shapeExpanded = false }
                ) {
                    listOf("RECTANGULAR", "CIRCULAR", "IRREGULAR").forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                shape = s
                                shapeExpanded = false
                            }
                        )
                    }
                }
            }

            // Dimension inputs based on shape
            if (shape == "RECTANGULAR") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = length,
                        onValueChange = { length = it },
                        label = { Text("Largo (m)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = width,
                        onValueChange = { width = it },
                        label = { Text("Ancho (m)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            } else if (shape == "CIRCULAR") {
                OutlinedTextField(
                    value = diameter,
                    onValueChange = { diameter = it },
                    label = { Text("Diámetro en m") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // Area Field (Automatic for RECTANGULAR/CIRCULAR, Editable for IRREGULAR)
            OutlinedTextField(
                value = displayAreaText,
                onValueChange = { if (shape == "IRREGULAR") areaManual = it },
                readOnly = shape != "IRREGULAR",
                label = {
                    Text(
                        if (shape == "IRREGULAR") "Área en m² *"
                        else "Área calculada automáticamente (m²)"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = depth,
                onValueChange = { depth = it },
                label = { Text("Profundidad en m (ej. 1.8)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción u observaciones") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}
