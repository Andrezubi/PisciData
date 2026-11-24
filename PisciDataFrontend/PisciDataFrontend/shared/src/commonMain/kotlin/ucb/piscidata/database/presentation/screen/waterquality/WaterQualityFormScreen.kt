package ucb.piscidata.database.presentation.screen.waterquality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ucb.piscidata.database.data.dto.CreateWaterQualityRequestDto
import ucb.piscidata.database.data.service.ProductionCycleService
import ucb.piscidata.database.data.service.WaterQualityService
import ucb.piscidata.database.domain.model.CicloRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterQualityFormScreen(
    farmId: Int,
    waterQualityService: WaterQualityService = koinInject(),
    productionCycleService: ProductionCycleService = koinInject(),
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var cycles by remember { mutableStateOf<List<CicloRecord>>(emptyList()) }
    var selectedCycle by remember { mutableStateOf<CicloRecord?>(null) }
    var expandedCycleDropdown by remember { mutableStateOf(false) }

    var measurementDate by remember { mutableStateOf("2026-11-24") }
    var measurementTime by remember { mutableStateOf("09:00") }
    var temperatureC by remember { mutableStateOf("") }
    var ph by remember { mutableStateOf("") }
    var dissolvedOxygen by remember { mutableStateOf("") }
    var transparencyCm by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }

    var isLoadingCycles by remember { mutableStateOf(true) }
    var isLoadingSave by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(farmId) {
        isLoadingCycles = true
        productionCycleService.getCiclos(farmId).fold(
            onSuccess = { list ->
                cycles = list
                selectedCycle = list.firstOrNull()
                isLoadingCycles = false
            },
            onFailure = {
                cycles = emptyList()
                isLoadingCycles = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Registrar Calidad de Agua",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B)
                )
                Text(
                    text = "Ingrese los parámetros medidos en el estanque",
                    fontSize = 12.sp,
                    color = Color(0xFF6B818B)
                )
            }
            TextButton(onClick = onCancel) {
                Text("Cancelar", color = Color(0xFF1577C8), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        if (errorMessage != null) {
            Surface(
                color = Color(0xFFFDE8E8),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFD32F2F),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (isLoadingCycles) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF1577C8))
            }
        } else if (cycles.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No hay ciclos productivos en esta granja",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B2B3B),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Para registrar calidad de agua, primero debe crear un ciclo productivo en la sección 'Ciclos productivos'.",
                        color = Color(0xFF6B818B),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        } else {
            // Cycle / Pond Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Estanque / Ciclo productivo *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                ExposedDropdownMenuBox(
                    expanded = expandedCycleDropdown,
                    onExpandedChange = { expandedCycleDropdown = !expandedCycleDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedCycle?.let { "${it.id} • ${it.estanque} (${it.especie})" } ?: "Seleccionar estanque",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCycleDropdown,
                        onDismissRequest = { expandedCycleDropdown = false }
                    ) {
                        cycles.forEach { cycle ->
                            DropdownMenuItem(
                                text = { Text("${cycle.id} • ${cycle.estanque} (${cycle.especie})", fontSize = 13.sp) },
                                onClick = {
                                    selectedCycle = cycle
                                    expandedCycleDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Date and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Fecha (YYYY-MM-DD)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                    OutlinedTextField(
                        value = measurementDate,
                        onValueChange = { measurementDate = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Hora (HH:MM)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                    OutlinedTextField(
                        value = measurementTime,
                        onValueChange = { measurementTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            // Parameters: Temperature & pH
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Temperatura (°C)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                    OutlinedTextField(
                        value = temperatureC,
                        onValueChange = { temperatureC = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("Ej. 26.5") }
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("pH (0 - 14)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                    OutlinedTextField(
                        value = ph,
                        onValueChange = { ph = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("Ej. 7.2") }
                    )
                }
            }

            // Parameters: Dissolved Oxygen & Transparency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Oxígeno Disuelto (mg/L)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                    OutlinedTextField(
                        value = dissolvedOxygen,
                        onValueChange = { dissolvedOxygen = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("Ej. 6.8") }
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Transparencia (cm)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                    OutlinedTextField(
                        value = transparencyCm,
                        onValueChange = { transparencyCm = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("Ej. 35") }
                    )
                }
            }

            // Observations
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Observaciones", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("Comentarios sobre el agua, clima, comportamiento...") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Save Button
            Button(
                onClick = {
                    val cycle = selectedCycle
                    if (cycle == null || cycle.numericId <= 0 || cycle.pondId <= 0) {
                        errorMessage = "Selecciona un ciclo y estanque válidos antes de guardar."
                        return@Button
                    }

                    val dateValue = measurementDate.trim()
                    if (!Regex("\\d{4}-\\d{2}-\\d{2}").matches(dateValue)) {
                        errorMessage = "Ingresa una fecha válida con formato AAAA-MM-DD."
                        return@Button
                    }

                    val timeValue = measurementTime.trim()
                    if (!Regex("\\d{2}:\\d{2}(:\\d{2})?").matches(timeValue)) {
                        errorMessage = "Ingresa una hora válida con formato HH:MM."
                        return@Button
                    }

                    fun parseMeasurement(value: String): Double? =
                        value.trim().replace(',', '.').takeIf { it.isNotEmpty() }?.toDoubleOrNull()

                    val temperatureValue = parseMeasurement(temperatureC)
                    val phValue = parseMeasurement(ph)
                    val oxygenValue = parseMeasurement(dissolvedOxygen)
                    val transparencyValue = parseMeasurement(transparencyCm)
                    val inputs = listOf(temperatureC, ph, dissolvedOxygen, transparencyCm)
                    val parsedValues = listOf(temperatureValue, phValue, oxygenValue, transparencyValue)

                    if (inputs.zip(parsedValues).any { (input, value) -> input.isNotBlank() && value == null }) {
                        errorMessage = "Revisa los parámetros: todos deben ser números válidos."
                        return@Button
                    }
                    if (parsedValues.all { it == null }) {
                        errorMessage = "Ingresa al menos un parámetro de calidad del agua."
                        return@Button
                    }
                    if (parsedValues.any { it != null && it < 0 }) {
                        errorMessage = "Los parámetros no pueden tener valores negativos."
                        return@Button
                    }

                    isLoadingSave = true
                    errorMessage = null
                    coroutineScope.launch {
                        val req = CreateWaterQualityRequestDto(
                            pondId = cycle.pondId,
                            productionCycleId = cycle.numericId,
                            measurementDate = dateValue,
                            measurementTime = if (timeValue.length == 5) "$timeValue:00" else timeValue,
                            transparencyCm = transparencyValue,
                            temperatureC = temperatureValue,
                            ph = phValue,
                            dissolvedOxygen = oxygenValue,
                            observations = observations.ifBlank { null }
                        )
                        waterQualityService.createWaterQuality(req).fold(
                            onSuccess = {
                                isLoadingSave = false
                                onSaved()
                            },
                            onFailure = { e ->
                                isLoadingSave = false
                                errorMessage = e.message ?: "Error al registrar la calidad del agua en el servidor"
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoadingSave
            ) {
                if (isLoadingSave) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Medición", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
