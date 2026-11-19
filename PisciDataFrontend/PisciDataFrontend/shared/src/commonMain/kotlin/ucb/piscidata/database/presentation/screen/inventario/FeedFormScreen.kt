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
fun FeedFormScreen(
    viewModel: DatabaseViewModel,
    onCancel: () -> Unit
) {
    var brand by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var phase by remember { mutableStateOf("F1") }
    var proteinPercentage by remember { mutableStateOf("45.0") }
    var pelletSizeMm by remember { mutableStateOf("2.0") }
    var stockKg by remember { mutableStateOf("") }
    var minimumStockKg by remember { mutableStateOf("") }
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
                text = "Nuevo Alimento",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B2B3B)
            )
            Button(
                onClick = {
                    if (brand.isBlank()) {
                        errorMsg = "La marca del alimento es obligatoria"
                        return@Button
                    }
                    viewModel.emitEvent(
                        DatabaseEvent.CreateFeed(
                            brand = brand.trim(),
                            productName = productName.ifBlank { null },
                            phase = phase.ifBlank { null },
                            proteinPercentage = proteinPercentage.toDoubleOrNull(),
                            pelletSizeMm = pelletSizeMm.toDoubleOrNull(),
                            stockKg = stockKg.toDoubleOrNull(),
                            minimumStockKg = minimumStockKg.toDoubleOrNull()
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
                value = brand,
                onValueChange = { brand = it; errorMsg = null },
                label = { Text("Marca del alimento (ej. Aquamax, Purina) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Nombre del producto (ej. Iniciador I)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = phase,
                onValueChange = { phase = it },
                label = { Text("Fase (ej. F1, F2, Levante, Engorde)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = proteinPercentage,
                onValueChange = { proteinPercentage = it },
                label = { Text("Porcentaje de proteína (%) (ej. 45.0)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = pelletSizeMm,
                onValueChange = { pelletSizeMm = it },
                label = { Text("Tamaño de pellet (mm) (ej. 2.0)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = stockKg,
                onValueChange = { stockKg = it },
                label = { Text("Stock actual en kg (ej. 300)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = minimumStockKg,
                onValueChange = { minimumStockKg = it },
                label = { Text("Stock mínimo requerido en kg (ej. 50)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}
