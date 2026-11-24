package ucb.piscidata.reports.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import ucb.piscidata.ui.components.RefreshIcon
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
            .background(Color(0xFFF8FAFC))
    ) {
        // Barra superior unificada con acción de actualización integrada
        FarmSelectorHeader(
            farms = farms,
            selectedFarmId = selectedFarmId,
            onSelectFarm = { sessionManager.selectFarm(it) },
            subtitle = "Indicadores en tiempo real",
            trailingActions = {
                Surface(
                    onClick = { viewModel.refresh() },
                    color = Color(0xFFF4F7FA),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        RefreshIcon(color = Color(0xFF475569), modifier = Modifier.size(17.dp))
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.isLoading && state.report == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1577C8))
                }
            } else if (state.error != null && state.report == null) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "No se pudo cargar el reporte",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB91C1C)
                        )
                        Text(state.error!!, fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }
            } else {
                val report = state.report
                AnimatedVisibility(
                    visible = report != null,
                    enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 14 }
                ) {
                    if (report != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Tarjeta Hero de Biomasa refinada estilo Linear / Stripe
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF0F172A), Color(0xFF1577C8))
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(20.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "BIOMASA ESTIMADA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF7DD3FC),
                                            letterSpacing = 0.8.sp
                                        )
                                        Surface(
                                            color = Color(0x26FFFFFF),
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
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )

                                    HorizontalDivider(color = Color(0x24FFFFFF))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "${report.totalActiveFishCount} peces activos",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFE0F2FE)
                                        )
                                        Text(
                                            "${report.totalPondsCount} estanques totales",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFE0F2FE)
                                        )
                                    }
                                }
                            }

                            // Cuadrícula KPI 2x2 con bordes finos
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                KpiTile(
                                    modifier = Modifier.weight(1f),
                                    label = "Mortalidad",
                                    value = "${formatOneDecimal(report.mortalityRatePercent)}%",
                                    subtitle = "${report.totalMortalityCount} bajas registradas",
                                    accent = if (report.mortalityRatePercent > 5.0) Color(0xFFDC2626) else Color(0xFF059669)
                                )
                                KpiTile(
                                    modifier = Modifier.weight(1f),
                                    label = "Conversión (FCR)",
                                    value = report.estimatedFcr?.let { formatTwoDecimals(it) } ?: "—",
                                    subtitle = "${formatOneDecimal(report.totalFeedConsumedKg)} kg alimento",
                                    accent = Color(0xFF1577C8)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                KpiTile(
                                    modifier = Modifier.weight(1f),
                                    label = "Alertas de stock",
                                    value = "${report.lowStockAlertsCount}",
                                    subtitle = if (report.lowStockAlertsCount == 0) "Inventario óptimo" else "Ítems bajo mínimo",
                                    accent = if (report.lowStockAlertsCount > 0) Color(0xFFD97706) else Color(0xFF059669)
                                )
                                KpiTile(
                                    modifier = Modifier.weight(1f),
                                    label = "Tareas operativas",
                                    value = "${report.completedTasksCount}/${report.pendingTasksCount + report.completedTasksCount}",
                                    subtitle = "${report.pendingTasksCount} pendientes",
                                    accent = Color(0xFF0B2B3B)
                                )
                            }

                            // Parámetros de agua recientes
                            if (report.latestOxygenMgL != null || report.latestPh != null || report.latestTemperatureC != null) {
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            "Última medición de calidad de agua",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0B2B3B)
                                        )
                                        Surface(
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFFEDF2F7)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp, horizontal = 14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                WaterMetricItem("OXÍGENO", report.latestOxygenMgL?.let { "${formatOneDecimal(it)} mg/L" } ?: "—", Modifier.weight(1f))
                                                WaterMetricItem("NIVEL pH", report.latestPh?.let { formatOneDecimal(it) } ?: "—", Modifier.weight(1f))
                                                WaterMetricItem("TEMP.", report.latestTemperatureC?.let { "${formatOneDecimal(it)} °C" } ?: "—", Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }

                            // Diagnóstico inteligente PisciData
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    PisciDataLogo(darkTheme = false, modifier = Modifier.size(32.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            "Diagnóstico operativo",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0B2B3B)
                                        )
                                        Text(
                                            text = report.insightMessage,
                                            fontSize = 12.sp,
                                            color = Color(0xFF1E40AF),
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }

                            // Desglose por ciclo activo
                            if (report.activeCycles.isNotEmpty()) {
                                Text(
                                    "Desglose por ciclo activo",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569),
                                    modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                                )
                                report.activeCycles.forEach { c ->
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                                        modifier = Modifier.fillMaxWidth()
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
                                                    "Peso prom. ${formatOneDecimal(c.latestAverageWeightGrams)} g · ${c.healthStatus}",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF64748B)
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
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF94A3B8)
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
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5ECF2))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
            Text(value, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = accent)
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
private fun WaterMetricItem(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 0.4.sp)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0B2B3B))
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
