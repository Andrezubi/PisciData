package ucb.piscidata.database.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.database.presentation.screen.alimentacion.AlimentacionTab
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
import ucb.piscidata.ui.components.PlusIcon
import ucb.piscidata.ui.components.SearchIcon

@Composable
fun DatabaseScreen(
    viewModel: DatabaseViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val farms by sessionManager.farms.collectAsState()
    val selectedFarmId by sessionManager.selectedFarmId.collectAsState()
    var showSearch by remember { mutableStateOf(false) }

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Barra superior unificada con ícono de búsqueda colapsable
                    FarmSelectorHeader(
                        farms = farms,
                        selectedFarmId = selectedFarmId,
                        onSelectFarm = { sessionManager.selectFarm(it) },
                        subtitle = when (state.tab) {
                            DbTab.CICLOS -> "${state.ciclos.size} ciclos registrados"
                            DbTab.ESTANQUES -> "${state.ponds.size} estanques"
                            DbTab.INVENTARIO -> "${state.inventory.size} ítems en stock"
                            DbTab.ALIMENTACION -> "Control de raciones"
                        },
                        trailingActions = {
                            val activeSearch = showSearch || state.searchQuery.isNotBlank()
                            Surface(
                                onClick = {
                                    if (showSearch) {
                                        viewModel.emitEvent(DatabaseEvent.Search(""))
                                        showSearch = false
                                    } else {
                                        showSearch = true
                                    }
                                },
                                color = if (activeSearch) Color(0xFFEFF6FF) else Color(0xFFF4F7FA),
                                shape = CircleShape,
                                border = BorderStroke(
                                    1.dp,
                                    if (activeSearch) Color(0xFFBFDBFE) else Color(0xFFE5ECF2)
                                ),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    SearchIcon(
                                        color = if (activeSearch) Color(0xFF1577C8) else Color(0xFF475569),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    )

                    // Sub-barra luminosa: buscador desplegable + selector segmentado de 4 pestañas sin cortes
                    Surface(color = Color.White) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AnimatedVisibility(
                                visible = showSearch,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Surface(
                                    color = Color(0xFFF4F7FA),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SearchIcon(color = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                        TextField(
                                            value = state.searchQuery,
                                            onValueChange = { viewModel.emitEvent(DatabaseEvent.Search(it)) },
                                            modifier = Modifier.weight(1f),
                                            placeholder = {
                                                Text(
                                                    "Buscar ciclo, estanque o insumo…",
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF94A3B8)
                                                )
                                            },
                                            textStyle = TextStyle(fontSize = 13.sp, color = Color(0xFF0B2B3B)),
                                            singleLine = true,
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent,
                                                cursorColor = Color(0xFF1577C8)
                                            )
                                        )
                                        if (state.searchQuery.isNotBlank()) {
                                            TextButton(
                                                onClick = { viewModel.emitEvent(DatabaseEvent.Search("")) },
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("Limpiar", fontSize = 11.sp, color = Color(0xFF64748B))
                                            }
                                        }
                                    }
                                }
                            }

                            // Selector segmentado de 4 columnas iguales (0 texto cortado)
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    listOf(
                                        DbTab.CICLOS to "Ciclos",
                                        DbTab.ESTANQUES to "Estanques",
                                        DbTab.INVENTARIO to "Inventario",
                                        DbTab.ALIMENTACION to "Alimento"
                                    ).forEach { (tab, label) ->
                                        val selected = state.tab == tab
                                        val pillBg by animateColorAsState(
                                            targetValue = if (selected) Color.White else Color.Transparent,
                                            animationSpec = tween(180),
                                            label = "DbTabBg"
                                        )
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(9.dp))
                                                .clickable { viewModel.emitEvent(DatabaseEvent.ChangeTab(tab)) },
                                            color = pillBg,
                                            shape = RoundedCornerShape(9.dp),
                                            shadowElevation = if (selected) 1.dp else 0.dp
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (selected) Color(0xFF0B2B3B) else Color(0xFF64748B),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFEDF2F7))

                    // Contenido por pestaña
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        when (state.tab) {
                            DbTab.CICLOS -> CiclosTab(state, viewModel)
                            DbTab.ESTANQUES -> EstanquesTab(state)
                            DbTab.INVENTARIO -> InventarioTab(state, viewModel)
                            DbTab.ALIMENTACION -> AlimentacionTab(state, viewModel)
                        }
                    }
                }

                // FAB ergonómico inferior derecho al alcance del pulgar
                if (state.tab != DbTab.ALIMENTACION) {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.emitEvent(DatabaseEvent.ClickNuevo) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 18.dp, bottom = 18.dp),
                        containerColor = Color(0xFF1577C8),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 6.dp
                        ),
                        icon = { PlusIcon(Color.White, modifier = Modifier.size(16.dp)) },
                        text = {
                            Text(
                                text = when (state.tab) {
                                    DbTab.CICLOS -> "Nuevo ciclo"
                                    DbTab.ESTANQUES -> "Estanque"
                                    DbTab.INVENTARIO -> "Agregar ítem"
                                    DbTab.ALIMENTACION -> ""
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }

                // Diálogo limpio para elegir tipo de ítem en Inventario
                if (state.showInventoryTypeDialog) {
                    AlertDialog(
                        onDismissRequest = { viewModel.emitEvent(DatabaseEvent.ToggleShowInventoryTypeDialog(false)) },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(22.dp),
                        title = {
                            Text(
                                "Agregar al inventario",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0B2B3B)
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    "¿Qué tipo de elemento deseas registrar?",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Surface(
                                    onClick = {
                                        viewModel.emitEvent(DatabaseEvent.ToggleShowInventoryTypeDialog(false))
                                        viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.NuevoFeed))
                                    },
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            "Alimento balanceado",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1577C8)
                                        )
                                        Text(
                                            "Control por marca, proteína, pellet y stock en kg",
                                            fontSize = 11.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                                Surface(
                                    onClick = {
                                        viewModel.emitEvent(DatabaseEvent.ToggleShowInventoryTypeDialog(false))
                                        viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.NuevoSupply))
                                    },
                                    color = Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            "Suministro o insumo",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF059669)
                                        )
                                        Text(
                                            "Cal, probióticos, herramientas o equipos",
                                            fontSize = 11.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = { viewModel.emitEvent(DatabaseEvent.ToggleShowInventoryTypeDialog(false)) }) {
                                Text("Cancelar", color = Color(0xFF64748B))
                            }
                        }
                    )
                }
            }
        }
    }
}
