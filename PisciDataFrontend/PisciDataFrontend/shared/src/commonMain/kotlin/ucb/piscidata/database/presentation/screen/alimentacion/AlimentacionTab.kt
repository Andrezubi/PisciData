package ucb.piscidata.database.presentation.screen.alimentacion

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ucb.piscidata.database.data.service.FeedingService
import ucb.piscidata.database.domain.model.BadgeVariant
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.FeedingRecord
import ucb.piscidata.database.presentation.screen.components.InfoRow
import ucb.piscidata.database.presentation.viewmodel.DatabaseState
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel

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

    // 1. Creation form view
    if (isCreating && selectedCiclo != null) {
        AlimentacionFormScreen(
            ciclo = selectedCiclo!!,
            onSaved = {
                isCreating = false
                loadFeedings(selectedCiclo!!)
            },
            onCancel = {
                isCreating = false
            },
            feedingService = feedingService
        )
        return
    }

    // 2. Feedings history of selected cycle
    if (selectedCiclo != null) {
        val ciclo = selectedCiclo!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F8FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Back button and New Record button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { selectedCiclo = null },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("← Volver a ciclos", color = Color(0xFF1577C8), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Button(
                    onClick = { isCreating = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("+ Nueva Alimentación", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Cycle summary banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${ciclo.id} • ${ciclo.estanque}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B2B3B)
                        )
                        Surface(
                            color = Color(0xFFE8F8F1),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Activo",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF19A974)
                            )
                        }
                    }
                    Text(
                        text = "Especie: ${ciclo.especie} • Peces actuales: ${ciclo.peces} • Peso prom.: ${ciclo.pesoActual}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B818B)
                    )
                }
            }

            // Feedings List
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🍽️ No hay alimentaciones registradas",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B2B3B)
                        )
                        Text(
                            text = "Presiona '+ Nueva Alimentación' para registrar la primera comida de este ciclo.",
                            fontSize = 12.sp,
                            color = Color(0xFF6B818B),
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(feedings) { feeding ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Header: Date, Time & Meal Number
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
                                        Text("•", color = Color(0xFF91A5AD), fontSize = 11.sp)
                                        Text(
                                            text = feeding.feedingTime.take(5),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF6B818B)
                                        )
                                    }

                                    Surface(
                                        color = Color(0xFFE8F4FC),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Comida #${feeding.mealNumber ?: 1}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1577C8)
                                        )
                                    }
                                }

                                // Food Brand & Name
                                Text(
                                    text = feeding.feedName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1577C8)
                                )

                                // Specifications: Pellet Size, Protein Percentage, Quantity Kg
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        val pelletText = feeding.pelletSizeMm?.let { "$it mm" } ?: "Polvo"
                                        Surface(
                                            color = Color(0xFFF0F4F7),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "Pellet: $pelletText",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                color = Color(0xFF5A7584)
                                            )
                                        }

                                        feeding.proteinPercentage?.let { prot ->
                                            Surface(
                                                color = Color(0xFFF0F4F7),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "Prot: $prot%",
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF5A7584)
                                                )
                                            }
                                        }
                                    }

                                    // Quantity Given
                                    Text(
                                        text = "${feeding.quantityKg} kg",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF19A974)
                                    )
                                }

                                // Fish behavior if present
                                if (!feeding.behavior.isNullOrBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Comportamiento:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B818B))
                                        Text(feeding.behavior, fontSize = 11.sp, color = Color(0xFF0B2B3B))
                                    }
                                }

                                // Observations if present
                                if (!feeding.observations.isNullOrBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Observaciones:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B818B))
                                        Text(feeding.observations, fontSize = 11.sp, color = Color(0xFF0B2B3B))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return
    }

    // 3. Active cycles list view (first screen shown)
    val activeCycles = state.ciclos.filter { it.status == BadgeVariant.ACTIVO }
    val filteredCycles = activeCycles.filter {
        state.searchQuery.isBlank() ||
                it.estanque.contains(state.searchQuery, true) ||
                it.especie.contains(state.searchQuery, true) ||
                it.id.contains(state.searchQuery, true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Selecciona un ciclo activo para ver y gestionar su alimentación:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B818B)
        )

        if (filteredCycles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron ciclos activos.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B818B)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCycles) { ciclo ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCiclo = ciclo
                                loadFeedings(ciclo)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ciclo.id,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1577C8)
                                    )
                                    Text("•", color = Color(0xFF91A5AD), fontSize = 12.sp)
                                    Text(
                                        text = ciclo.estanque,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF0B2B3B)
                                    )
                                }

                                Surface(
                                    color = Color(0xFFE8F8F1),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Activo",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF19A974)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                InfoRow("Especie", ciclo.especie)
                                InfoRow("Peces", ciclo.peces)
                                InfoRow("Peso prom.", ciclo.pesoActual)
                                InfoRow("Edad", "${ciclo.edadDias} d")
                            }

                            HorizontalDivider(color = Color(0xFFF0F4F7))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ver alimentaciones →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1577C8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
