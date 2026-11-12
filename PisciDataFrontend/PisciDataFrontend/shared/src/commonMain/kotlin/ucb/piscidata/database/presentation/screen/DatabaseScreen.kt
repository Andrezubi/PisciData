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
import ucb.piscidata.database.presentation.screen.ciclos.CicloFormScreen
import ucb.piscidata.database.presentation.screen.ciclos.CiclosTab
import ucb.piscidata.database.presentation.screen.estanques.EstanquesTab
import ucb.piscidata.database.presentation.screen.estanques.PondFormScreen
import ucb.piscidata.database.presentation.screen.inventario.FeedFormScreen
import ucb.piscidata.database.presentation.screen.inventario.InventarioTab
import ucb.piscidata.database.presentation.screen.inventario.SupplyFormScreen
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

    when (state.view) {
        is DbView.NuevoCiclo -> {
            CicloFormScreen(
                state = state,
                viewModel = viewModel,
                onCancel = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.List)) }
            )
        }
        is DbView.NuevoEstanque -> {
            PondFormScreen(
                viewModel = viewModel,
                onCancel = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.List)) }
            )
        }
        is DbView.NuevoFeed -> {
            FeedFormScreen(
                viewModel = viewModel,
                onCancel = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.List)) }
            )
        }
        is DbView.NuevoSupply -> {
            SupplyFormScreen(
                viewModel = viewModel,
                onCancel = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.List)) }
            )
        }
        is DbView.Editar -> {
            CicloFormScreen(
                state = state,
                viewModel = viewModel,
                onCancel = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.List)) }
            )
        }
        is DbView.List -> {
            Box(modifier = Modifier.fillMaxSize()) {
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

                    // Header with Tabs and Contextual New button
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Base de datos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                                Text(
                                    text = when (state.tab) {
                                        DbTab.CICLOS -> "Administra los ciclos productivos de tu granja"
                                        DbTab.ESTANQUES -> "Administra los estanques e infraestructura"
                                        DbTab.INVENTARIO -> "Administra alimentos y suministros de tu granja"
                                    },
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B818B)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.emitEvent(DatabaseEvent.ClickNuevo) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text = when (state.tab) {
                                        DbTab.CICLOS -> "+ Nuevo Ciclo"
                                        DbTab.ESTANQUES -> "+ Nuevo Estanque"
                                        DbTab.INVENTARIO -> "+ Nuevo Registro"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
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
                                DbTab.CICLOS to "Ciclos productivos",
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

                // Inventory Type Choice Dialog
                if (state.showInventoryTypeDialog) {
                    AlertDialog(
                        onDismissRequest = { viewModel.emitEvent(DatabaseEvent.ToggleShowInventoryTypeDialog(false)) },
                        title = { Text("Registrar en Inventario") },
                        text = { Text("Seleccione el tipo de elemento que desea registrar en el inventario:") },
                        confirmButton = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.emitEvent(DatabaseEvent.ToggleShowInventoryTypeDialog(false))
                                        viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.NuevoFeed))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
                                ) {
                                    Text("Alimento para peces (Feed)")
                                }
                                Button(
                                    onClick = {
                                        viewModel.emitEvent(DatabaseEvent.ToggleShowInventoryTypeDialog(false))
                                        viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.NuevoSupply))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF19A974))
                                ) {
                                    Text("Suministro / Insumo (Supply)")
                                }
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.emitEvent(DatabaseEvent.ToggleShowInventoryTypeDialog(false)) }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}
