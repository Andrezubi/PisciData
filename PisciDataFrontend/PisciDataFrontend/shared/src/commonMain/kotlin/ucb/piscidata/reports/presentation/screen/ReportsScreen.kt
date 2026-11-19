package ucb.piscidata.reports.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.reports.presentation.viewmodel.ReportsViewModel
import ucb.piscidata.session.SessionManager
import ucb.piscidata.ui.components.FarmSelectorHeader
import ucb.piscidata.ui.components.PisciDataLogo
import kotlin.math.roundToInt

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val farms by sessionManager.farms.collectAsState()
    val selectedFarmId by sessionManager.selectedFarmId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
    ) {
        FarmSelectorHeader(
            farms = farms,
            selectedFarmId = selectedFarmId,
            onSelectFarm = { sessionManager.selectFarm(it) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Reportes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0B2B3B)
                    )
                    Text(
                        "Indicadores reales en tiempo real",
                        fontSize = 12.sp,
                        color = Color(0xFF6B818B)
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.refresh() },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text("↻ Actualizar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1577C8))
                }
            }

            if (state.isLoading && state.report == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1577C8))
                }
            } else if (state.error != null && state.report == null) {
                Surface(
                    color = Color(0xFFFFF0F0),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "No se pudo cargar el reporte",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE05A5A)
                        )
                        Text(state.error!!, fontSize = 12.sp, color = Color(0xFF6B818B))
                    }
                }
            } else {
                val report = state.report
                AnimatedVisibility(
                    visible = report != null,
                    enter = fadeIn(tween(320)) + slideInVertically(tween(320)) { it / 12 }
                ) {
                    if (report != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Hero Biomass Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF0B2B3B), Color(0xFF1577C8))
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(20.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "BIOMASA ESTIMADA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4EC5C1),
                                            letterSpacing = 1.sp
                                        )
                                        Surface(
                                            color = Color(0x334EC5C1),
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                "${report.activeCyclesCount} ciclos activos",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    val biomassLabel = if (report.estimatedBiomassKg >= 1000.0) {
                                        "${formatOneDecimal(report.estimatedBiomassKg / 1000.0)} t"
                                    } else {
                                        "${formatOneDecimal(report.estimatedBiomassKg)} kg"
                                    }
                                    Text(
                                        text = biomassLabel,
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "${report.totalActiveFishCount} peces activos",
                                            fontSize = 12.sp,
                                            color = Color(0xFFD6EAF8)
                                        )
                                        Text(
                                            "${report.totalPondsCount} estanques",
                                            fontSize = 12.sp,
                                            color = Color(0xFFD6EAF8)
                                        )
                                    }
                                }
                            }

                            // 2x2 KPI Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                KpiTile(
                                    modifier = Modifier.weight(1f),
                                    label = "Mortalidad",
                                    value = "${formatOneDecimal(report.mortalityRatePercent)}%",
                                    subtitle = "${report.totalMortalityCount} bajas",
                                    accent = if (report.mortalityRatePercent > 5.0) Color(0xFFE05A5A) else Color(0xFF19A974)
                                )
                                KpiTile(
                                    modifier = Modifier.weight(1f),
                                    label = "FCR estimado",
                                    value = report.estimatedFcr?.let { formatTwoDecimals(it) } ?: "—",
                                    subtitle = "${formatOneDecimal(report.totalFeedConsumedKg)} kg alimento",
                                    accent = Color(0xFF1577C8)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                KpiTile(
                                    modifier = Modifier.weight(1f),
                                    label = "Alertas stock",
                                    value = "${report.lowStockAlertsCount}",
                                    subtitle = if (report.lowStockAlertsCount == 0) "Inventario óptimo" else "Bajo mínimo",
                                    accent = if (report.lowStockAlertsCount > 0) Color(0xFFE08B00) else Color(0xFF19A974)
                                )
                                KpiTile(
                                    modifier = Modifier.weight(1f),
                                    label = "Tareas",
                                    value = "${report.completedTasksCount}/${report.pendingTasksCount + report.completedTasksCount}",
                                    subtitle = "${report.pendingTasksCount} pendientes",
                                    accent = Color(0xFF0B2B3B)
                                )
                            }

                            // Water quality snapshot if present
                            if (report.latestOxygenMgL != null || report.latestPh != null || report.latestTemperatureC != null) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            "Última medición de agua",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0B2B3B)
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            WaterChip("O₂", report.latestOxygenMgL?.let { "${formatOneDecimal(it)} mg/L" } ?: "—")
                                            WaterChip("pH", report.latestPh?.let { formatOneDecimal(it) } ?: "—")
                                            WaterChip("Temp", report.latestTemperatureC?.let { "${formatOneDecimal(it)} °C" } ?: "—")
                                        }
                                    }
                                }
                            }

                            // AI / System Insight Card
                            Surface(
                                color = Color(0xFFE6F4FA),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PisciDataLogo(darkTheme = true, modifier = Modifier.size(36.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            "Diagnóstico PisciData",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0B2B3B)
                                        )
                                        Text(
                                            text = report.insightMessage,
                                            fontSize = 12.sp,
                                            color = Color(0xFF155D96),
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }

                            // Active cycles performance breakdown
                            if (report.activeCycles.isNotEmpty()) {
                                Text(
                                    "Rendimiento por ciclo activo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B2B3B)
                                )
                                report.activeCycles.forEach { c ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(14.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                Text(
                                                    "${c.pondCode} · ${c.speciesName}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0B2B3B)
                                                )
                                                Text(
                                                    "Peso prom.: ${formatOneDecimal(c.latestAverageWeightGrams)} g · ${c.healthStatus}",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF6B818B)
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    "${formatOneDecimal(c.estimatedBiomassKg)} kg",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFF1577C8)
                                                )
                                                Text(
                                                    "${c.deadFishCount} bajas",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF8AA0AA)
                                                )
                                            }
                                        }
                                    }
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
private fun KpiTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtitle: String,
    accent: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B818B))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = accent)
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF8AA0AA))
        }
    }
}

@Composable
private fun WaterChip(title: String, value: String) {
    Surface(
        color = Color(0xFFF0F7FA),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B818B))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
        }
    }
}

private fun formatOneDecimal(v: Double): String {
    val scaled = (v * 10.0).roundToInt()
    val whole = scaled / 10
    val frac = kotlin.math.abs(scaled % 10)
    return "$whole.$frac"
}

private fun formatTwoDecimals(v: Double): String {
    val scaled = (v * 100.0).roundToInt()
    val whole = scaled / 100
    val frac = kotlin.math.abs(scaled % 100)
    return "$whole.${frac.toString().padStart(2, '0')}"
}
