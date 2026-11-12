package ucb.piscidata.database.presentation.screen.ciclos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.domain.model.CicloRecord

@Composable
fun CicloFormScreen(
    initial: CicloRecord,
    mode: String,
    onSave: (CicloRecord) -> Unit,
    onCancel: () -> Unit
) {
    var estanque by remember { mutableStateOf(initial.estanque) }
    var especie by remember { mutableStateOf(initial.especie) }
    var peces by remember { mutableStateOf(initial.peces) }
    var pesoActual by remember { mutableStateOf(initial.pesoActual) }
    var edadDias by remember { mutableStateOf(initial.edadDias.toString()) }
    var inicio by remember { mutableStateOf(initial.startDate) }
    var fin by remember { mutableStateOf(initial.endDate ?: "") }
    var initialAge by remember { mutableStateOf(initial.initialAgeDays.toString()) }
    var pesoInicial by remember { mutableStateOf(initial.initialWeightGrams) }
    var cantidadInicial by remember { mutableStateOf(initial.initialFishCount) }
    var observaciones by remember { mutableStateOf(initial.observations) }
    var status by remember { mutableStateOf(initial.status) }

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
                text = if (mode == "nuevo") "Nuevo Ciclo" else "Editar Ciclo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B2B3B)
            )
            Button(
                onClick = {
                    onSave(
                        CicloRecord(
                            id = initial.id,
                            status = status,
                            estanque = estanque,
                            especie = especie,
                            peces = peces,
                            pesoActual = pesoActual,
                            edadDias = edadDias.toIntOrNull() ?: 0,
                            startDate = inicio,
                            endDate = fin.ifBlank { null },
                            initialAgeDays = initialAge.toIntOrNull() ?: 0,
                            initialWeightGrams = pesoInicial,
                            initialFishCount = cantidadInicial,
                            observations = observaciones,
                            isExpanded = true
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
            ) {
                Text("Guardar")
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = estanque,
                onValueChange = { estanque = it },
                label = { Text("Estanque") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = especie,
                onValueChange = { especie = it },
                label = { Text("Especie") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = peces,
                onValueChange = { peces = it },
                label = { Text("Cantidad actual de peces") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pesoActual,
                onValueChange = { pesoActual = it },
                label = { Text("Peso promedio actual") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = edadDias,
                onValueChange = { edadDias = it },
                label = { Text("Edad en días (actual)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = inicio,
                onValueChange = { inicio = it },
                label = { Text("Fecha de inicio (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fin,
                onValueChange = { fin = it },
                label = { Text("Fecha de fin (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = initialAge,
                onValueChange = { initialAge = it },
                label = { Text("Edad inicial (días)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pesoInicial,
                onValueChange = { pesoInicial = it },
                label = { Text("Peso inicial") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cantidadInicial,
                onValueChange = { cantidadInicial = it },
                label = { Text("Cantidad inicial de peces") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
