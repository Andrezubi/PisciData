package ucb.piscidata.database.presentation.screen.alimentacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ucb.piscidata.database.data.service.FeedingService
import ucb.piscidata.database.domain.model.BadgeVariant
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.FeedingRecord
import ucb.piscidata.database.presentation.viewmodel.DatabaseState
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel
import ucb.piscidata.ui.components.PlusIcon

@Composable
fun AlimentacionTab(
    state: DatabaseState,
    viewModel: DatabaseViewModel,
    feedingService: FeedingService = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedCiclo by remember { mutableStateOf<CicloRecord?>(null) }
    var isCreating by remember { mutableStateOf(false) }

    var feedings by remember { mutableStateOf<List<FeedingRecord>>(emptyList()) }
    var isLoadingFeedings by remember { mutableStateOf(false) }

    fun loadFeedings(ciclo: CicloRecord) {
        isLoadingFeedings = true
        coroutineScope.launch {
            feedingService.getFeedingsByCycle(ciclo.numericId).fold(
                onSuccess = { list ->
                    feedings = list
                    isLoadingFeedings = false
                },
                onFailure = {
                    feedings = emptyList()
                    isLoadingFeedings = false
                }
            )
        }
    }

    // 1. Vista de formulario de nueva ración
    if (isCreating && selectedCiclo != null) {
        AlimentacionFormScreen(
            ciclo = selectedCiclo!!,
            onSaved = {
                isCreating = false
                loadFeedings(selectedCiclo!!)
            },
            onCancel = { isCreating = false },
            feedingService = feedingService
        )
        return
    }

    // 2. Historial de alimentaciones del ciclo seleccionado
    if (selectedCiclo != null) {
        val ciclo = selectedCiclo!!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Barra compacta de retorno + resumen del ciclo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { selectedCiclo = null },
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5ECF2))
                    ) {
                        Text(
                            text = "← Ciclos activos",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            color = Color(0xFF0B2B3B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "${feedings.size} registro(s)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }

                // Tarjeta resumen del ciclo
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "${ciclo.estanque} · ${ciclo.especie}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B2B3B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${ciclo.id} · ${ciclo.peces} peces · ${ciclo.pesoActual}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Activo",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                    }
                }

                if (isLoadingFeedings) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1577C8))
                    }
                } else if (feedings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Sin raciones registradas",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B2B3B)
                            )
                            Text(
                                text = "Toca «+ Registrar ración» abajo a la derecha.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(feedings) { feeding ->
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = feeding.feedingDate,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0B2B3B)
                                            )
                                            Text("·", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                            Text(
                                                text = feeding.feedingTime.take(5),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        Surface(
                                            color = Color(0xFFEFF6FF),
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = "Comida #${feeding.mealNumber ?: 1}",
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1577C8)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = feeding.feedName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0B2B3B)
                                            )
                                            val pelletText = feeding.pelletSizeMm?.let { "$it mm" } ?: "Polvo"
                                            val protText = feeding.proteinPercentage?.let { " · $it% prot" } ?: ""
                                            Text(
                                                text = "Pellet $pelletText$protText",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        Surface(
                                            color = Color(0xFFECFDF5),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "${feeding.quantityKg} kg",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF059669)
                                            )
                                        }
                                    }

                                    if (!feeding.behavior.isNullOrBlank() || !feeding.observations.isNullOrBlank()) {
                                        HorizontalDivider(color = Color(0xFFEDF2F7))
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            if (!feeding.behavior.isNullOrBlank()) {
                                                Text(
                                                    text = " voracidad/conducta: ${feeding.behavior}".trim(),
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF475569)
                                                )
                                            }
                                            if (!feeding.observations.isNullOrBlank()) {
                                                Text(
                                                    text = feeding.observations,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B)
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

            // FAB ergonómico para nueva ración
            ExtendedFloatingActionButton(
                onClick = { isCreating = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 18.dp, bottom = 18.dp),
                containerColor = Color(0xFF1577C8),
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                icon = { PlusIcon(Color.White, modifier = Modifier.size(16.dp)) },
                text = {
                    Text("Registrar ración", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            )
        }
        return
    }

    // 3. Lista inicial: elegir ciclo activo para gestionar raciones
    val activeCycles = state.ciclos.filter { it.status == BadgeVariant.ACTIVO }
    val filteredCycles = activeCycles.filter {
        state.searchQuery.isBlank() ||
            it.estanque.contains(state.searchQuery, true) ||
            it.especie.contains(state.searchQuery, true) ||
            it.id.contains(state.searchQuery, true)
    }

    if (filteredCycles.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay ciclos activos para registrar alimentación.",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Toca un ciclo activo para registrar o consultar sus raciones",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
            items(filteredCycles, key = { it.id }) { ciclo ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            selectedCiclo = ciclo
                            loadFeedings(ciclo)
                        },
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5ECF2))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = ciclo.estanque,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B2B3B)
                                )
                                Text(
                                    text = "${ciclo.id} · ${ciclo.especie}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                text = "Raciones →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1577C8)
                            )
                        }

                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFEDF2F7)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 9.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("PECES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Text(ciclo.peces, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("PESO PROM.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Text(ciclo.pesoActual, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("EDAD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Text("${ciclo.edadDias} d", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
