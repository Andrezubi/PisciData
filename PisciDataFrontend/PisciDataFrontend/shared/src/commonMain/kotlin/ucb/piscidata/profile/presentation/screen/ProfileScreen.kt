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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.session.SessionManager
import ucb.piscidata.profile.presentation.viewmodel.ProfileEvent
import ucb.piscidata.profile.presentation.viewmodel.ProfileViewModel
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

    // Load profile for user id 1 (or decoded from session) on first composition
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
            // Hero header
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = Color(0xFF1577C8)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initials = if (state.firstName.isNotBlank() && state.lastName.isNotBlank()) {
                                "${state.firstName.first()}${state.lastName.first()}"
                            } else {
                                "US"
                            }
                            Text(initials.uppercase(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    val fullName = if (state.firstName.isNotBlank()) "${state.firstName} ${state.lastName}" else "Cargando..."
                    Text(fullName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                    Surface(
                        color = Color(0xFFE8F4FC),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Rol: ${state.role}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1577C8)
                        )
                    }
                }
            }

            // Personal info card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
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
                        Text("Datos personales", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        TextButton(onClick = { viewModel.emitEvent(ProfileEvent.ToggleEditMode) }) {
                            Text(if (state.editMode) "Cancelar" else "Editar", color = Color(0xFF1577C8))
                        }
                    }

                    if (state.isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state.firstName,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdateFirstName(it)) },
                                label = { Text("Nombre") },
                                enabled = state.editMode,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = state.lastName,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdateLastName(it)) },
                                label = { Text("Apellido") },
                                enabled = state.editMode,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = state.phone,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdatePhone(it)) },
                                label = { Text("Teléfono / Celular") },
                                enabled = state.editMode,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = state.role,
                                onValueChange = {},
                                label = { Text("Rol (Solo lectura)") },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        if (state.editMode) {
                            Button(
                                onClick = { viewModel.emitEvent(ProfileEvent.SaveProfile) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
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

            // Logout
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEEEE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar sesión", color = Color(0xFFE05A5A), fontWeight = FontWeight.Bold)
            }
        }
    }
}
