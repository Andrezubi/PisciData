package ucb.piscidata.database.presentation.screen.alimentacion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ucb.piscidata.database.data.dto.CreateFeedingRequestDto
import ucb.piscidata.database.data.dto.FeedResponseDto
import ucb.piscidata.database.data.service.FeedingService
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.FeedingRecommendation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentacionFormScreen(
    ciclo: CicloRecord,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    feedingService: FeedingService = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    var recommendation by remember { mutableStateOf<FeedingRecommendation?>(null) }
    var availableFeeds by remember { mutableStateOf<List<FeedResponseDto>>(emptyList()) }
    var selectedFeed by remember { mutableStateOf<FeedResponseDto?>(null) }
    var isFeedDropdownExpanded by remember { mutableStateOf(false) }

    var useWeightMethod by remember { mutableStateOf(true) }

    var feedingDate by remember { mutableStateOf("2026-11-19") }
    var feedingTime by remember { mutableStateOf("08:00") }
    var quantityKg by remember { mutableStateOf("") }
    var behavior by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }

    var isLoadingRec by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(ciclo.numericId) {
        isLoadingRec = true
        feedingService.getRecommendation(ciclo.numericId).onSuccess { rec ->
            recommendation = rec
            useWeightMethod = rec?.hasBiometrics == true
        }
        feedingService.getAvailableFeeds().onSuccess { feeds ->
            availableFeeds = feeds
            if (feeds.isNotEmpty()) {
                selectedFeed = feeds.first()
            }
        }
        isLoadingRec = false
    }

    // Active recommendation based on toggle
    val activeRec = if (useWeightMethod && recommendation?.hasBiometrics == true) {
        recommendation
    } else {
        recommendation?.alternativeAgeRecommendation ?: recommendation
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("← Cancelar", color = Color(0xFF1577C8), fontWeight = FontWeight.SemiBold)
            }
            Text(
                text = "Registrar Alimentación",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B2B3B)
            )
            Spacer(modifier = Modifier.width(60.dp))
        }

        // Cycle summary header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${ciclo.id} • ${ciclo.estanque}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B2B3B)
                    )
                    Text(
                        text = "Especie: ${ciclo.especie} • Peces: ${ciclo.peces}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B818B)
                    )
                }
            }
        }

        // RECOMMENDATION CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FD)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFB9DCF5), RoundedCornerShape(14.dp))
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
                    Text(
                        text = "💡 Sugerencia Nutricional",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1577C8)
                    )

                    // Toggle button between Weight and Age if biometrics are available
                    if (recommendation?.hasBiometrics == true && recommendation?.alternativeAgeRecommendation != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE2EEF8),
                            modifier = Modifier.clickable { useWeightMethod = !useWeightMethod }
                        ) {
                            Text(
                                text = if (useWeightMethod) "Modo: Por Peso ▾" else "Modo: Por Edad ▾",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1577C8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (isLoadingRec) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterHorizontally),
                        color = Color(0xFF1577C8)
                    )
                } else if (activeRec != null) {
                    val methodLabel = if (activeRec.method == "BY_WEIGHT") "Calculado por Peso (Última Biometría)" else "Calculado por Edad de los peces"
                    Text(
                        text = methodLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF5A7584)
                    )

                    // Details grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Peces vivos", fontSize = 11.sp, color = Color(0xFF6B818B))
                            Text("${activeRec.currentFishCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        }
                        Column {
                            Text("Peso prom.", fontSize = 11.sp, color = Color(0xFF6B818B))
                            Text("${activeRec.averageWeightGrams ?: "--"} g", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        }
                        Column {
                            Text("Biomasa viva", fontSize = 11.sp, color = Color(0xFF6B818B))
                            Text("${activeRec.currentBiomassKg ?: "--"} kg", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        }
                        Column {
                            Text("Edad", fontSize = 11.sp, color = Color(0xFF6B818B))
                            Text("${activeRec.currentAgeDays} días", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFD6E8F5))

                    // Portion and Meals recommendation highlighted
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Ración recomendada:", fontSize = 12.sp, color = Color(0xFF32576C))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${activeRec.portionPerMealKg} kg",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1577C8)
                                )
                                Text(" / por comida", fontSize = 12.sp, color = Color(0xFF6B818B), modifier = Modifier.padding(bottom = 2.dp))
                            }
                            Text(
                                text = "Total día: ${activeRec.dailyTotalKg} kg (${activeRec.dailyMeals} comidas/día)",
                                fontSize = 11.sp,
                                color = Color(0xFF6B818B)
                            )
                        }

                        Button(
                            onClick = { quantityKg = activeRec.portionPerMealKg.toString() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Usar sugerencia", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    // Ideal Feed Specifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val pelletText = if (activeRec.isPowder) "Polvo" else "${activeRec.idealPelletSizeMinMm ?: ""} - ${activeRec.idealPelletSizeMaxMm ?: ""} mm"
                        val protText = "${activeRec.idealProteinMinPercentage ?: ""} - ${activeRec.idealProteinMaxPercentage ?: ""}%"

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("Pellet ideal", fontSize = 10.sp, color = Color(0xFF6B818B))
                                Text(pelletText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("Proteína ideal", fontSize = 10.sp, color = Color(0xFF6B818B))
                                Text(protText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                            }
                        }

                        activeRec.idealFeedPhase?.let { phase ->
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text("Fase", fontSize = 10.sp, color = Color(0xFF6B818B))
                                    Text(phase, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No se encontraron tablas de referencia para calcular la sugerencia.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B818B)
                    )
                }
            }
        }

        // REGISTRATION FORM
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Datos de Alimentación",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B)
                )

                // Feed Selector (ComboBox)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tipo de alimento", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF32576C))
                    ExposedDropdownMenuBox(
                        expanded = isFeedDropdownExpanded,
                        onExpandedChange = { isFeedDropdownExpanded = !isFeedDropdownExpanded }
                    ) {
                        val currentFeedLabel = selectedFeed?.let {
                            val pellet = if (it.pelletSizeMm != null) "${it.pelletSizeMm} mm" else "Polvo"
                            val prot = if (it.proteinPercentage != null) "${it.proteinPercentage}%" else "--"
                            "${it.brand} ${it.productName ?: ""} (Pellet: $pellet, Prot: $prot)"
                        } ?: "Seleccionar alimento..."

                        OutlinedTextField(
                            value = currentFeedLabel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFeedDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(8.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = isFeedDropdownExpanded,
                            onDismissRequest = { isFeedDropdownExpanded = false }
                        ) {
                            availableFeeds.forEach { feed ->
                                val pellet = if (feed.pelletSizeMm != null) "${feed.pelletSizeMm} mm" else "Polvo"
                                val prot = if (feed.proteinPercentage != null) "${feed.proteinPercentage}%" else "--"
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "${feed.brand} ${feed.productName ?: ""}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0B2B3B)
                                            )
                                            Text(
                                                text = "Pellet: $pellet • Proteína: $prot • Fase: ${feed.phase ?: "--"}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF6B818B)
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedFeed = feed
                                        isFeedDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Date & Time row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Fecha (YYYY-MM-DD)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF32576C))
                        OutlinedTextField(
                            value = feedingDate,
                            onValueChange = { feedingDate = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Hora (HH:mm)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF32576C))
                        OutlinedTextField(
                            value = feedingTime,
                            onValueChange = { feedingTime = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                }

                // Quantity Kg (allows 0)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Cantidad suministrada (kg)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF32576C))
                    OutlinedTextField(
                        value = quantityKg,
                        onValueChange = { quantityKg = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0.0 (Puede ser 0)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Text("El número de comida del día se calculará automáticamente.", fontSize = 11.sp, color = Color(0xFF6B818B))
                }

                // Behavior (optional)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Comportamiento de los peces (opcional)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF32576C))
                    OutlinedTextField(
                        value = behavior,
                        onValueChange = { behavior = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Voraces, comieron con normalidad, lentos...") },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                // Observations (optional)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Observaciones extra (opcional)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF32576C))
                    OutlinedTextField(
                        value = observations,
                        onValueChange = { observations = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Notas adicionales...") },
                        shape = RoundedCornerShape(8.dp),
                        minLines = 2
                    )
                }

                errorMessage?.let { err ->
                    Text(text = err, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar", color = Color(0xFF6B818B))
                    }

                    Button(
                        onClick = {
                            val feed = selectedFeed
                            if (feed == null) {
                                errorMessage = "Por favor selecciona un alimento."
                                return@Button
                            }

                            val qty = quantityKg.toDoubleOrNull()
                            if (qty == null || qty < 0.0) {
                                errorMessage = "Por favor ingresa una cantidad válida en kg (puede ser 0 o mayor)."
                                return@Button
                            }

                            if (feedingDate.isBlank()) {
                                errorMessage = "La fecha es requerida."
                                return@Button
                            }

                            errorMessage = null
                            isSaving = true

                            val request = CreateFeedingRequestDto(
                                productionCycleId = ciclo.numericId,
                                feedId = feed.id,
                                feedingDate = feedingDate.trim(),
                                feedingTime = if (feedingTime.length == 5) "${feedingTime.trim()}:00" else feedingTime.trim(),
                                quantityKg = qty,
                                mealNumber = null, // Backend assigns automatically based on existing meals for that day
                                behavior = behavior.ifBlank { null },
                                observations = observations.ifBlank { null }
                            )

                            coroutineScope.launch {
                                feedingService.createFeeding(request).fold(
                                    onSuccess = {
                                        isSaving = false
                                        onSaved()
                                    },
                                    onFailure = { ex ->
                                        isSaving = false
                                        errorMessage = ex.message ?: "Error al guardar el registro."
                                    }
                                )
                            }
                        },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                        } else {
                            Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
