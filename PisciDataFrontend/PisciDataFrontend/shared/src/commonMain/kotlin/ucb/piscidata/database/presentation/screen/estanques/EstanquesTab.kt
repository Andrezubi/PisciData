package ucb.piscidata.database.presentation.screen.estanques

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.presentation.viewmodel.DatabaseState

@Composable
fun EstanquesTab(state: DatabaseState) {
    val filtered = state.ponds.filter {
        state.searchQuery.isBlank() ||
            it.code.contains(state.searchQuery, true) ||
            it.nombre.contains(state.searchQuery, true)
    }

    if (filtered.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Sin estanques encontrados",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B)
                )
                Text(
                    text = "Usa «+ Estanque» para registrar infraestructura.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filtered, key = { it.id }) { pond ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE5ECF2))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = pond.code,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1577C8)
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = pond.nombre,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B2B3B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val shapeLabel = pond.forma.name.lowercase().replaceFirstChar { it.uppercase() }
                                Text(
                                    text = "$shapeLabel · ${pond.area} m² · Prof. ${pond.profundidad} m",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        val active = pond.cicloActivo != null
                        Surface(
                            color = if (active) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (active) "Con ciclo" else "Disponible",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) Color(0xFF059669) else Color(0xFF64748B)
                            )
                        }
                    }

                    // Franja limpia de parámetros de agua
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEDF2F7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WaterMetricCell("OXÍGENO", "${pond.oxigeno} mg/L", Modifier.weight(1f))
                            WaterMetricCell("NIVEL pH", "${pond.ph}", Modifier.weight(1f))
                            WaterMetricCell("TEMP.", "${pond.temperatura} °C", Modifier.weight(1f))
                            if (pond.transparenciaCm != null) {
                                WaterMetricCell("TRANSP.", "${pond.transparenciaCm} cm", Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterMetricCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 0.4.sp
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B2B3B)
        )
    }
}
