package ucb.piscidata.database.presentation.screen.inventario

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

@Composable
fun SupplyFormScreen(
    viewModel: DatabaseViewModel,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Químico") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var minimumStock by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

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
                text = "Nuevo Suministro",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B2B3B)
            )
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMsg = "El nombre del suministro es obligatorio"
                        return@Button
                    }
                    val qtyVal = quantity.toDoubleOrNull()
                    if (qtyVal == null) {
                        errorMsg = "Ingrese una cantidad válida"
                        return@Button
                    }
                    viewModel.emitEvent(
                        DatabaseEvent.CreateSupply(
                            name = name.trim(),
                            category = category,
                            quantity = qtyVal,
                            unit = unit.ifBlank { "und" },
                            minimumStock = minimumStock.toDoubleOrNull()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
            ) {
                Text("Guardar")
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
                value = name,
                onValueChange = { name = it; errorMsg = null },
                label = { Text("Nombre del suministro (ej. Cal agrícola, Permanganato) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            // Category Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { TextButton(onClick = { categoryExpanded = true }) { Text("▾") } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    listOf("Químico", "Combustible", "Equipo", "Otro").forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c) },
                            onClick = {
                                category = c
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it; errorMsg = null },
                label = { Text("Cantidad actual *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unidad de medida (ej. kg, L, und)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = minimumStock,
                onValueChange = { minimumStock = it },
                label = { Text("Stock mínimo requerido") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}
