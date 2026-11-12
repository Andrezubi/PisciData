package ucb.piscidata.database.presentation.screen.ciclos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filtered) { ciclo ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.clickable { viewModel.emitEvent(DatabaseEvent.ToggleCicloExpand(ciclo.id)) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header row before expanding: Estanque, Codigo, Especie, Peces, Peso Actual, Edad dias
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ciclo.id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1577C8))
                            Text("•", color = Color(0xFF91A5AD), fontSize = 12.sp)
                            Text(ciclo.estanque, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
                        }
                        Surface(
                            color = when (ciclo.status) {
                                BadgeVariant.ACTIVO -> Color(0xFFE8F8F1)
                                BadgeVariant.EN_SEGUIMIENTO -> Color(0xFFE8F4FC)
                                BadgeVariant.FINALIZADO -> Color(0xFFDCE8ED)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = ciclo.status.displayName(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when (ciclo.status) {
                                    BadgeVariant.ACTIVO -> Color(0xFF19A974)
                                    BadgeVariant.EN_SEGUIMIENTO -> Color(0xFF1577C8)
                                    BadgeVariant.FINALIZADO -> Color(0xFF6B818B)
                                }
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        InfoRow("Especie", ciclo.especie)
                        InfoRow("Cantidad de Peces", ciclo.peces)
                        InfoRow("Peso Promedio Actual", ciclo.pesoActual)
                        InfoRow("Edad en Días", "${ciclo.edadDias} días")
                    }

                    // Expanded additional info
                    if (ciclo.isExpanded) {
                        HorizontalDivider(color = Color(0xFFF0F4F6), modifier = Modifier.padding(vertical = 4.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            InfoRow("Fecha Inicio", ciclo.startDate)
                            InfoRow("Fecha Fin", ciclo.endDate ?: "En curso")
                            InfoRow("Edad Inicial", "${ciclo.initialAgeDays} días")
                            InfoRow("Peso Inicial", ciclo.initialWeightGrams)
                            InfoRow("Cantidad Inicial", ciclo.initialFishCount)
                            if (ciclo.observations.isNotBlank()) {
                                InfoRow("Observaciones", ciclo.observations)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (ciclo.isExpanded) "▲ Ocultar detalles" else "▼ Ver más detalles",
                            fontSize = 11.sp,
                            color = Color(0xFF1577C8),
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.Editar(ciclo))) }) {
                                EditIcon(Color(0xFF1577C8))
                            }
                            IconButton(onClick = { viewModel.emitEvent(DatabaseEvent.DeleteCiclo(ciclo.id)) }) {
                                DeleteIcon(Color(0xFFE05A5A))
                            }
                        }
                    }
                }
            }
        }
    }
}
