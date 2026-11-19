package ucb.piscidata.profile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.data.remote.RemoteBackendConfig
import ucb.piscidata.profile.presentation.viewmodel.ProfileEvent
import ucb.piscidata.profile.presentation.viewmodel.ProfileViewModel
import ucb.piscidata.session.SessionManager
import ucb.piscidata.ui.components.FarmSelectorHeader

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject(),
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val farms by sessionManager.farms.collectAsState()
    val selectedFarmId by sessionManager.selectedFarmId.collectAsState()
    val activeRemoteUrl by RemoteBackendConfig.remoteBaseUrl.collectAsState()
    var remoteUrlInput by remember(activeRemoteUrl) { mutableStateOf(activeRemoteUrl) }
    var remoteStatusMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.emitEvent(ProfileEvent.LoadProfile(1))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
    ) {
        FarmSelectorHeader(
            farms = farms,
            selectedFarmId = selectedFarmId,
            onSelectFarm = { sessionManager.selectFarm(it) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero header with brand gradient
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0B2B3B), Color(0xFF1577C8))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(76.dp),
                            shape = CircleShape,
                            color = Color(0xFF4EC5C1)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val initials = if (state.firstName.isNotBlank() && state.lastName.isNotBlank()) {
                                    "${state.firstName.first()}${state.lastName.first()}"
                                } else {
                                    "PD"
                                }
                                Text(
                                    initials.uppercase(),
                                    color = Color(0xFF0B2B3B),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        val fullName = if (state.firstName.isNotBlank()) "${state.firstName} ${state.lastName}" else "Cargando..."
                        Text(fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Surface(
                            color = Color(0x33FFFFFF),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Rol: ${state.role}",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Personal info card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Datos personales", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        TextButton(onClick = { viewModel.emitEvent(ProfileEvent.ToggleEditMode) }) {
                            Text(
                                if (state.editMode) "Cancelar" else "Editar",
                                color = Color(0xFF1577C8),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state.isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF1577C8))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = state.firstName,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdateFirstName(it)) },
                                label = { Text("Nombre") },
                                enabled = state.editMode,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = state.lastName,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdateLastName(it)) },
                                label = { Text("Apellido") },
                                enabled = state.editMode,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = state.phone,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdatePhone(it)) },
                                label = { Text("Teléfono / Celular") },
                                enabled = state.editMode,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = state.role,
                                onValueChange = {},
                                label = { Text("Rol (Solo lectura)") },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        if (state.editMode) {
                            Button(
                                onClick = { viewModel.emitEvent(ProfileEvent.SaveProfile) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF19A974))
                            ) {
                                Text("Guardar cambios", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    if (state.error != null) {
                        Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                    if (state.successMessage != null) {
                        Text(state.successMessage!!, color = Color(0xFF19A974), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Remote Backend Connection Card (ngrok / Cloudflare / LAN)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Conexión al servidor", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        Surface(
                            color = if (activeRemoteUrl.isNotBlank()) Color(0xFFE6F7F0) else Color(0xFFE8F4FC),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (activeRemoteUrl.isNotBlank()) "Remoto activo" else "Local 127.0.0.1",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeRemoteUrl.isNotBlank()) Color(0xFF19A974) else Color(0xFF1577C8)
                            )
                        }
                    }
                    Text(
                        "Pega tu URL de ngrok o túnel HTTPS para usar PisciData sin cable USB. Los puertos locales quedan como respaldo automático.",
                        fontSize = 11.sp,
                        color = Color(0xFF6B818B)
                    )
                    OutlinedTextField(
                        value = remoteUrlInput,
                        onValueChange = { remoteUrlInput = it },
                        label = { Text("URL remota (ej. https://xxxx.ngrok-free.app)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                RemoteBackendConfig.setRemoteBaseUrl(remoteUrlInput)
                                remoteStatusMsg = if (remoteUrlInput.isBlank()) {
                                    "Usando conexión local (127.0.0.1:5007)."
                                } else {
                                    "Servidor remoto aplicado: ${RemoteBackendConfig.activeBaseUrl}"
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
                        ) {
                            Text("Aplicar URL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        if (activeRemoteUrl.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    remoteUrlInput = ""
                                    RemoteBackendConfig.setRemoteBaseUrl("")
                                    remoteStatusMsg = "Vuelto a modo local (127.0.0.1:5007)."
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Local", fontSize = 12.sp, color = Color(0xFF6B818B))
                            }
                        }
                    }
                    if (remoteStatusMsg != null) {
                        Text(remoteStatusMsg!!, fontSize = 11.sp, color = Color(0xFF19A974), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Logout
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEEEE)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cerrar sesión", color = Color(0xFFE05A5A), fontWeight = FontWeight.Bold)
            }
        }
    }
}
