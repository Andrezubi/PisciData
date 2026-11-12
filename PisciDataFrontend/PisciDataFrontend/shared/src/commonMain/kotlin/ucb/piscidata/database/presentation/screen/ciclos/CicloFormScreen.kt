package ucb.piscidata.database.presentation.screen.ciclos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import ucb.piscidata.database.presentation.viewmodel.DatabaseState
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel

@Composable
fun CicloFormScreen(
    state: DatabaseState,
    viewModel: DatabaseViewModel,
    onCancel: () -> Unit
) {
    // Select default pond (prefer available pond without active cycle)
    val availablePonds = remember(state.ponds) {
        val free = state.ponds.filter { it.cicloActivo == null }
        if (free.isNotEmpty()) free else state.ponds
    }

    var selectedPondId by remember { mutableStateOf(availablePonds.firstOrNull()?.id ?: 1) }
    var selectedSpeciesId by remember { mutableStateOf(state.speciesList.firstOrNull()?.id ?: 1) }
    var startDate by remember { mutableStateOf("2026-11-12") }
    var initialFishCount by remember { mutableStateOf("10000") }
    var initialWeightGrams by remember { mutableStateOf("12.0") }
    var initialAgeDays by remember { mutableStateOf("15") }
    var observations by remember { mutableStateOf("") }

    var pondDropdownExpanded by remember { mutableStateOf(false) }
    var speciesDropdownExpanded by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val selectedPondName = state.ponds.find { it.id == selectedPondId }?.let { "${it.code} - ${it.nombre}" }
        ?: if (state.ponds.isNotEmpty()) "${state.ponds.first().code} - ${state.ponds.first().nombre}" else "Sin estanques disponibles"

    val selectedSpeciesName = state.speciesList.find { it.id == selectedSpeciesId }?.commonName
        ?: if (state.speciesList.isNotEmpty()) state.speciesList.first().commonName else "Tilapia nilótica"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) {
                    Text("← Volver", color = Color(0xFF1577C8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Text(
                    text = "Nuevo Ciclo Productivo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B)
                )
                Spacer(modifier = Modifier.width(32.dp))
            }
        }

        if (errorMsg != null) {
            Surface(
                color = Color(0xFFFDE8E8),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMsg!!,
                    color = Color(0xFFE05A5A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Main Form Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Información del Ciclo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))

                // Pond Selector Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedPondName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estanque asignado *") },
                        trailingIcon = { Text("▼", color = Color(0xFF1577C8), fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pondDropdownExpanded = true },
                        shape = RoundedCornerShape(10.dp)
                    )
                    DropdownMenu(
                        expanded = pondDropdownExpanded,
                        onDismissRequest = { pondDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        state.ponds.forEach { p ->
                            val isFree = p.cicloActivo == null
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${p.code} - ${p.nombre}", fontWeight = FontWeight.SemiBold)
                                        Text(
                                            if (isFree) "(Disponible)" else "(En uso)",
                                            color = if (isFree) Color(0xFF19A974) else Color(0xFF6B818B),
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                onClick = {
                                    selectedPondId = p.id
                                    pondDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Species Selector Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedSpeciesName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Especie cultivada *") },
                        trailingIcon = { Text("▼", color = Color(0xFF1577C8), fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { speciesDropdownExpanded = true },
                        shape = RoundedCornerShape(10.dp)
                    )
                    DropdownMenu(
                        expanded = speciesDropdownExpanded,
                        onDismissRequest = { speciesDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        state.speciesList.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.commonName, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    selectedSpeciesId = s.id
                                    speciesDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it; errorMsg = null },
                    label = { Text("Fecha de inicio (YYYY-MM-DD) *") },
                    placeholder = { Text("2026-11-12") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = initialFishCount,
                        onValueChange = { initialFishCount = it },
                        label = { Text("Cantidad peces") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = initialWeightGrams,
                        onValueChange = { initialWeightGrams = it },
                        label = { Text("Peso inic. (g)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = initialAgeDays,
                    onValueChange = { initialAgeDays = it },
                    label = { Text("Edad inicial (días)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Observaciones") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Full Width Primary Save Button
        Button(
            onClick = {
                if (state.ponds.isEmpty()) {
                    errorMsg = "Debe crear primero un estanque en esta granja"
                    return@Button
                }
                if (startDate.isBlank() || !startDate.contains("-")) {
                    errorMsg = "Ingrese una fecha válida en formato YYYY-MM-DD"
                    return@Button
                }
                viewModel.emitEvent(
                    DatabaseEvent.CreateProductionCycle(
                        pondId = selectedPondId,
                        speciesId = selectedSpeciesId,
                        startDate = startDate.trim(),
                        initialFishCount = initialFishCount.toIntOrNull(),
                        initialAverageWeightGrams = initialWeightGrams.toDoubleOrNull(),
                        initialAgeDays = initialAgeDays.toIntOrNull(),
                        observations = observations.ifBlank { null }
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text("Guardar Ciclo Productivo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
