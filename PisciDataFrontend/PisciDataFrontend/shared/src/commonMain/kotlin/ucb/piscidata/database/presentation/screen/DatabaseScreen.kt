package ucb.piscidata.database.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.database.domain.model.BadgeVariant
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.presentation.screen.ciclos.CicloFormScreen
import ucb.piscidata.database.presentation.screen.ciclos.CiclosTab
import ucb.piscidata.database.presentation.screen.estanques.EstanquesTab
import ucb.piscidata.database.presentation.screen.inventario.InventarioTab
import ucb.piscidata.database.presentation.viewmodel.DatabaseEvent
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel
import ucb.piscidata.database.presentation.viewmodel.DbTab
import ucb.piscidata.database.presentation.viewmodel.DbView
import ucb.piscidata.session.SessionManager
import ucb.piscidata.ui.components.FarmSelectorHeader

@Composable
fun DatabaseScreen(
    viewModel: DatabaseViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val farms by sessionManager.farms.collectAsState()
    val selectedFarmId by sessionManager.selectedFarmId.collectAsState()

    when (val view = state.view) {
        is DbView.Nuevo -> {
            CicloFormScreen(
                initial = CicloRecord(
                    id = viewModel.generateNewCicloId(),
                    status = BadgeVariant.ACTIVO,
                    estanque = "Estanque 1",
                    especie = "",
                    peces = "",
                    pesoActual = "0 g",
                    edadDias = 0,
                    startDate = "2026-10-07",
                    endDate = null,
                    initialAgeDays = 0,
                    initialWeightGrams = "",
                    initialFishCount = "",
                    observations = "",
                    isExpanded = true
                ),
                mode = "nuevo",
                onSave = { ciclo -> viewModel.emitEvent(DatabaseEvent.SaveCiclo(ciclo)) },
                onCancel = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.List)) }
            )
        }
        is DbView.Editar -> {
            CicloFormScreen(
                initial = view.record,
                mode = "editar",
                onSave = { ciclo -> viewModel.emitEvent(DatabaseEvent.SaveCiclo(ciclo)) },
                onCancel = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.List)) }
            )
        }
        is DbView.List -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F8FA))
            ) {
                // Unified Farm Selector Header
                FarmSelectorHeader(
                    farms = farms,
                    selectedFarmId = selectedFarmId,
                    onSelectFarm = { sessionManager.selectFarm(it) }
                )

                // Header with Tabs and New button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Base de datos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                            Text("Administra ciclos, estanques e inventario", fontSize = 12.sp, color = Color(0xFF6B818B))
                        }
                        Button(
                            onClick = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.Nuevo)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Nuevo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }

                    // Search
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.emitEvent(DatabaseEvent.Search(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar ciclo, estanque o insumo...") },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        listOf(
                            DbTab.CICLOS to "Ciclos activos",
                            DbTab.ESTANQUES to "Estanques",
                            DbTab.INVENTARIO to "Inventario"
                        ).forEach { (tab, label) ->
                            val selected = state.tab == tab
                            TextButton(onClick = { viewModel.emitEvent(DatabaseEvent.ChangeTab(tab)) }) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Color(0xFF1577C8) else Color(0xFF6B818B)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFDCE8ED))

                // Content based on tab
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (state.tab) {
                        DbTab.CICLOS -> CiclosTab(state, viewModel)
                        DbTab.ESTANQUES -> EstanquesTab(state)
                        DbTab.INVENTARIO -> InventarioTab(state, viewModel)
                    }
                }
            }
        }
    }
}
