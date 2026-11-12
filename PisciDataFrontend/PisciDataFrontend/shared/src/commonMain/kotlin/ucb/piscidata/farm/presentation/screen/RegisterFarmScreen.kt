package ucb.piscidata.farm.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.farm.presentation.components.PlatformMapPickerDialog
import ucb.piscidata.farm.presentation.components.rememberCurrentLocationRequester
import ucb.piscidata.farm.presentation.viewmodel.RegisterFarmEvent
import ucb.piscidata.farm.presentation.viewmodel.RegisterFarmViewModel

@Composable
fun RegisterFarmScreen(
    userId: Int,
    phone: String,
    pass: String,
    viewModel: RegisterFarmViewModel = koinViewModel(),
    onFarmRegisteredSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId, phone, pass) {
        viewModel.emitEvent(RegisterFarmEvent.Init(userId, phone, pass))
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onFarmRegisteredSuccess()
        }
    }

    val requestCurrentLocation = rememberCurrentLocationRequester(
        onLocationObtained = { lat, lng ->
            viewModel.emitEvent(RegisterFarmEvent.OnLocationSelected(lat, lng))
        },
        onError = { msg ->
            viewModel.emitEvent(RegisterFarmEvent.SetError(msg))
        }
    )

    if (state.isMapPickerOpen) {
        PlatformMapPickerDialog(
            initialLatitude = state.latitude,
            initialLongitude = state.longitude,
            onDismissRequest = {
                viewModel.emitEvent(RegisterFarmEvent.SetMapPickerOpen(false))
            },
            onLocationConfirmed = { lat, lng ->
                viewModel.emitEvent(RegisterFarmEvent.OnLocationSelected(lat, lng))
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .safeContentPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PisciData",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0B2B3B)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F1F8)
                    ) {
                        Text(
                            text = "Paso 2 de 2",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1577C8)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Registrar tu Piscigranja",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B2B3B)
                    )
                    Text(
                        text = "Cada usuario necesita una granja asociada para comenzar a gestionar estanques y ciclos.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B818B)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.emitEvent(RegisterFarmEvent.OnNameChanged(it)) },
                        label = { Text("Nombre de la Piscigranja *") },
                        placeholder = { Text("Ej: Piscigranja El Valle") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.emitEvent(RegisterFarmEvent.OnDescriptionChanged(it)) },
                        label = { Text("Descripción (Opcional)") },
                        placeholder = { Text("Ej: Granja especializada en truchas arcoíris") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = state.address,
                        onValueChange = { viewModel.emitEvent(RegisterFarmEvent.OnAddressChanged(it)) },
                        label = { Text("Dirección o Referencia (Opcional)") },
                        placeholder = { Text("Ej: Km 14 carretera a Coroico") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Geographic location section
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFDCE4E8), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF9FBFC)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ubicación Geográfica",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B2B3B)
                                )
                                if (state.latitude != null && state.longitude != null) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFE8F5E9)
                                    ) {
                                        Text(
                                            text = "Fijada",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }

                            // Read-only coordinates view
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Latitud", fontSize = 11.sp, color = Color(0xFF6B818B))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFEEF2F5),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = state.latitude?.toString() ?: "No fijada",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (state.latitude != null) Color(0xFF0B2B3B) else Color(0xFF9EADB4)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Longitud", fontSize = 11.sp, color = Color(0xFF6B818B))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFEEF2F5),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = state.longitude?.toString() ?: "No fijada",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (state.longitude != null) Color(0xFF0B2B3B) else Color(0xFF9EADB4)
                                        )
                                    }
                                }
                            }

                            // Location action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = requestCurrentLocation,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1577C8)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "📍 Mi GPS",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Button(
                                    onClick = { viewModel.emitEvent(RegisterFarmEvent.SetMapPickerOpen(true)) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B2B3B)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "🗺️ En el mapa",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = { viewModel.emitEvent(RegisterFarmEvent.OnSubmitClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Finalizar y Entrar a Piscidata",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
