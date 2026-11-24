package ucb.piscidata.database.presentation.screen.ciclos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.domain.model.BadgeVariant
import ucb.piscidata.database.presentation.screen.components.InfoRow
import ucb.piscidata.database.presentation.viewmodel.DatabaseEvent
import ucb.piscidata.database.presentation.viewmodel.DatabaseState
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel
import ucb.piscidata.database.presentation.viewmodel.DbView
import ucb.piscidata.ui.components.DeleteIcon
import ucb.piscidata.ui.components.EditIcon

@Composable
fun CiclosTab(
    state: DatabaseState,
    viewModel: DatabaseViewModel
) {
    val filtered = state.ciclos.filter {
        state.searchQuery.isBlank() ||
            it.estanque.contains(state.searchQuery, true) ||
            it.especie.contains(state.searchQuery, true) ||
            it.id.contains(state.searchQuery, true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "Sin ciclos para mostrar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B2B3B)
                    )
                    Text(
                        text = "Usa el botón «+ Nuevo ciclo» para iniciar una campaña.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { ciclo ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.emitEvent(DatabaseEvent.ToggleCicloExpand(ciclo.id)) },
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5ECF2))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Encabezado limpio: Estanque principal + código/especie + badge de estado
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = ciclo.estanque,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0B2B3B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${ciclo.id} · ${ciclo.especie}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Surface(
                                    color = when (ciclo.status) {
                                        BadgeVariant.ACTIVO -> Color(0xFFECFDF5)
                                        BadgeVariant.EN_SEGUIMIENTO -> Color(0xFFEFF6FF)
                                        BadgeVariant.FINALIZADO -> Color(0xFFF1F5F9)
                                    },
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = ciclo.status.displayName(),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (ciclo.status) {
                                            BadgeVariant.ACTIVO -> Color(0xFF059669)
                                            BadgeVariant.EN_SEGUIMIENTO -> Color(0xFF1577C8)
                                            BadgeVariant.FINALIZADO -> Color(0xFF64748B)
                                        }
                                    )
                                }
                            }

                            // Franja visual de 3 métricas clave (reemplaza tabla plana de Excel)
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
                                    CycleMetricPill(
                                        label = "PECES",
                                        value = ciclo.peces,
                                        modifier = Modifier.weight(1f)
                                    )
                                    CycleMetricPill(
                                        label = "PESO PROM.",
                                        value = ciclo.pesoActual,
                                        modifier = Modifier.weight(1f)
                                    )
                                    CycleMetricPill(
                                        label = "EDAD",
                                        value = "${ciclo.edadDias} d",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Información adicional expandible
                            AnimatedVisibility(visible = ciclo.isExpanded) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    HorizontalDivider(color = Color(0xFFEDF2F7))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    InfoRow("Fecha inicio", ciclo.startDate)
                                    InfoRow("Fecha fin", ciclo.endDate ?: "En curso")
                                    InfoRow("Siembra inicial", "${ciclo.initialFishCount} peces · ${ciclo.initialWeightGrams}")
                                    InfoRow("Edad inicial", "${ciclo.initialAgeDays} días")
                                    if (ciclo.observations.isNotBlank()) {
                                        Surface(
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    "Observaciones",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF64748B)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    ciclo.observations,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF0B2B3B),
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Barra inferior de acciones limpias
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (ciclo.isExpanded) "Ocultar ficha ▴" else "Ver ficha completa ▾",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1577C8),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (ciclo.status == BadgeVariant.ACTIVO || ciclo.status == BadgeVariant.EN_SEGUIMIENTO) {
                                        Surface(
                                            onClick = { viewModel.emitEvent(DatabaseEvent.PromptFinalizeCycle(ciclo)) },
                                            color = Color(0xFFFEF2F2),
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(1.dp, Color(0xFFFECACA))
                                        ) {
                                            Text(
                                                text = "Finalizar",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                    }
                                    Surface(
                                        onClick = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.Editar(ciclo))) },
                                        color = Color(0xFFF4F7FA),
                                        shape = CircleShape,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            EditIcon(Color(0xFF475569), modifier = Modifier.size(15.dp))
                                        }
                                    }
                                    Surface(
                                        onClick = { viewModel.emitEvent(DatabaseEvent.DeleteCiclo(ciclo.id)) },
                                        color = Color(0xFFFEF2F2),
                                        shape = CircleShape,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            DeleteIcon(Color(0xFFEF4444), modifier = Modifier.size(15.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.cycleToFinalize != null) {
            val cycle = state.cycleToFinalize
            AlertDialog(
                onDismissRequest = { viewModel.emitEvent(DatabaseEvent.PromptFinalizeCycle(null)) },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                title = { Text("Finalizar ciclo", fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B)) },
                text = {
                    Text(
                        "¿Deseas cerrar el ciclo ${cycle.id} en ${cycle.estanque}? Se registrará hoy como fecha de cosecha/cierre.",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.emitEvent(DatabaseEvent.ConfirmFinalizeCycle(cycle)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Finalizar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.emitEvent(DatabaseEvent.PromptFinalizeCycle(null)) }) {
                        Text("Cancelar", color = Color(0xFF64748B))
                    }
                }
            )
        }
    }
}

@Composable
private fun CycleMetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
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
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0B2B3B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
