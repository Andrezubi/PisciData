package ucb.piscidata.database.presentation.screen

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
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.database.domain.model.*
import ucb.piscidata.database.presentation.viewmodel.*

@Composable
fun DatabaseScreen(
    viewModel: DatabaseViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    when (val view = state.view) {
        is DbView.Nuevo -> {
            CicloFormScreen(
                initial = CicloRecord(
                    id = viewModel.generateNewCicloId(),
                    status = BadgeVariant.ACTIVO,
                    estanque = "Estanque 1",
                    especie = "",
                    peces = "",
                    inicio = "2026-10-07",
                    pesoInicial = "",
                    densidad = "",
                    alimento = "F1",
                    observaciones = ""
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
                // Header
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
                            Text("Administra los registros acuícolas", fontSize = 12.sp, color = Color(0xFF6B818B))
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                Box(modifier = Modifier.fillMaxSize().weight(1f)) {
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

@Composable
fun CiclosTab(state: DatabaseState, viewModel: DatabaseViewModel) {
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
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(ciclo.id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1577C8))
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
                        InfoRow("Estanque", ciclo.estanque)
                        InfoRow("Especie", ciclo.especie)
                        InfoRow("Peces sembrados", ciclo.peces)
                        InfoRow("Inicio", ciclo.inicio)
                        if (ciclo.pesoInicial.isNotBlank()) InfoRow("Peso inicial", "${ciclo.pesoInicial} g")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.emitEvent(DatabaseEvent.ChangeView(DbView.Editar(ciclo))) }) {
                            Text("✏️")
                        }
                        IconButton(onClick = { viewModel.emitEvent(DatabaseEvent.DeleteCiclo(ciclo.id)) }) {
                            Text("🗑️")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = Color(0xFF6B818B))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0B2B3B))
    }
}

@Composable
fun EstanquesTab(state: DatabaseState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.ponds) { pond ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(pond.nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        Surface(
                            color = if (pond.cicloActivo != null) Color(0xFFE8F8F1) else Color(0xFFDCE8ED),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (pond.cicloActivo != null) "Activo" else "Sin ciclo",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (pond.cicloActivo != null) Color(0xFF19A974) else Color(0xFF6B818B)
                            )
                        }
                    }
                    InfoRow("Forma", pond.forma.name)
                    InfoRow("Área", "${pond.area} m²")
                    InfoRow("Profundidad", "${pond.profundidad} m")
                    InfoRow("pH", pond.ph.toString())
                    InfoRow("Temperatura", "${pond.temperatura}°C")
                    InfoRow("Oxígeno disuelto", "${pond.oxigeno} mg/L")
                }
            }
        }
    }
}

@Composable
fun InventarioTab(state: DatabaseState, viewModel: DatabaseViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.inventory) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        Text("${item.qty} ${item.unit}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1577C8))
                    }
                    InfoRow("Categoría", item.category.label())
                    InfoRow("Ubicación", item.location)
                    InfoRow("Mínimo requerido", "${item.minQty} ${item.unit}")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.emitEvent(DatabaseEvent.UpdateInventoryQty(item.id, item.qty - 1)) },
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("-")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.emitEvent(DatabaseEvent.UpdateInventoryQty(item.id, item.qty + 1)) },
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("+")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CicloFormScreen(
    initial: CicloRecord,
    mode: String,
    onSave: (CicloRecord) -> Unit,
    onCancel: () -> Unit
) {
    var estanque by remember { mutableStateOf(initial.estanque) }
    var especie by remember { mutableStateOf(initial.especie) }
    var peces by remember { mutableStateOf(initial.peces) }
    var inicio by remember { mutableStateOf(initial.inicio) }
    var pesoInicial by remember { mutableStateOf(initial.pesoInicial) }
    var densidad by remember { mutableStateOf(initial.densidad) }
    var alimento by remember { mutableStateOf(initial.alimento) }
    var observaciones by remember { mutableStateOf(initial.observaciones) }
    var status by remember { mutableStateOf(initial.status) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("← Cancelar", color = Color(0xFF1577C8))
            }
            Text(
                text = if (mode == "nuevo") "Nuevo Ciclo" else "Editar Ciclo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B2B3B)
            )
            Button(
                onClick = {
                    onSave(
                        CicloRecord(
                            id = initial.id,
                            status = status,
                            estanque = estanque,
                            especie = especie,
                            peces = peces,
                            inicio = inicio,
                            pesoInicial = pesoInicial,
                            densidad = densidad,
                            alimento = alimento,
                            observaciones = observaciones
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
            ) {
                Text("Guardar")
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = estanque,
                onValueChange = { estanque = it },
                label = { Text("Estanque") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = especie,
                onValueChange = { especie = it },
                label = { Text("Especie") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = peces,
                onValueChange = { peces = it },
                label = { Text("Peces sembrados") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = inicio,
                onValueChange = { inicio = it },
                label = { Text("Fecha de inicio (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pesoInicial,
                onValueChange = { pesoInicial = it },
                label = { Text("Peso inicial (g)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = densidad,
                onValueChange = { densidad = it },
                label = { Text("Densidad (peces/m²)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
