package ucb.piscidata.database.presentation.screen.waterquality

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ucb.piscidata.database.data.service.WaterQualityService
import ucb.piscidata.database.domain.model.WaterParameterStatus
import ucb.piscidata.database.domain.model.WaterQualityRecord
import ucb.piscidata.database.presentation.viewmodel.DatabaseState
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel

@Composable
fun WaterQualityTab(
    state: DatabaseState,
    viewModel: DatabaseViewModel,
    waterQualityService: WaterQualityService = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    var measurements by remember { mutableStateOf<List<WaterQualityRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isCreating by remember { mutableStateOf(false) }

    fun loadData() {
        isLoading = true
        coroutineScope.launch {
            waterQualityService.getWaterQualityByFarm(state.selectedFarmId).fold(
                onSuccess = { list ->
                    measurements = list
                    isLoading = false
                },
                onFailure = {
                    measurements = emptyList()
                    isLoading = false
                }
            )
        }
    }

    LaunchedEffect(state.selectedFarmId) {
        loadData()
    }

    if (isCreating) {
        WaterQualityFormScreen(
            farmId = state.selectedFarmId,
            waterQualityService = waterQualityService,
            onSaved = {
                isCreating = false
                loadData()
            },
            onCancel = {
                isCreating = false
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Registros de Calidad de Agua",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B)
                )
                Text(
                    text = "Monitoreo de parámetros fisicoquímicos en tus estanques",
                    fontSize = 11.sp,
                    color = Color(0xFF6B818B)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { isCreating = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "+ Nueva Medición",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1577C8))
            }
        } else if (measurements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "No hay mediciones registradas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B2B3B)
                        )
                        Text(
                            text = "Presiona el botón '+ Nueva Medición' superior para registrar el primer control de calidad de agua en esta granja.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B818B),
                            lineHeight = 18.sp,
                            modifier = Modifier.widthIn(max = 300.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { isCreating = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text("+ Registrar Primera Medición", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        } else {
            val filtered = measurements.filter {
                state.searchQuery.isBlank() ||
                        it.pondCode.contains(state.searchQuery, true) ||
                        it.measurementDate.contains(state.searchQuery, true) ||
                        (it.observations?.contains(state.searchQuery, true) == true)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { record ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Header: Pond Code & Date/Time
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estanque: ${record.pondCode}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1577C8)
                                )
                                Text(
                                    text = "${record.measurementDate} • ${record.measurementTime.take(5)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6B818B)
                                )
                            }

                            // Parameters Grid / Row chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ParameterChip("Temp", record.temperatureC?.let { "$it C" } ?: "—", record.parameterStatuses["TEMPERATURE"])
                                ParameterChip("pH", record.ph?.toString() ?: "—", record.parameterStatuses["PH"])
                                ParameterChip("O2", record.dissolvedOxygen?.let { "$it mg/L" } ?: "—", record.parameterStatuses["DISSOLVED_OXYGEN"])
                                ParameterChip("Transp.", record.transparencyCm?.let { "$it cm" } ?: "—", record.parameterStatuses["TRANSPARENCY"])
                            }

                            // Observations if present
                            if (!record.observations.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Observaciones:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B818B))
                                    Text(record.observations, fontSize = 11.sp, color = Color(0xFF0B2B3B))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParameterChip(title: String, value: String, status: WaterParameterStatus?) {
    val (bg, fg) = when (status) {
        WaterParameterStatus.OPTIMAL -> Color(0xFFE8F8F1) to Color(0xFF19A974)
        WaterParameterStatus.WARNING -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        WaterParameterStatus.CRITICAL -> Color(0xFFFDE8E8) to Color(0xFFD32F2F)
        else -> Color(0xFFF0F4F7) to Color(0xFF6B818B)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = fg)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
        }
    }
}
