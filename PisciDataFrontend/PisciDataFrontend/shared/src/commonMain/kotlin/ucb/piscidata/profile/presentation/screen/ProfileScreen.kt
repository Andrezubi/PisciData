package ucb.piscidata.profile.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val useLocalOnly by RemoteBackendConfig.useLocalOnly.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.emitEvent(ProfileEvent.LoadProfile(1))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        FarmSelectorHeader(
            farms = farms,
            selectedFarmId = selectedFarmId,
            onSelectFarm = { sessionManager.selectFarm(it) },
            subtitle = "Cuenta y conexión"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta compacta horizontal de identidad (ahorra ~110dp verticales)
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initials = if (state.firstName.isNotBlank() && state.lastName.isNotBlank()) {
                                "${state.firstName.first()}${state.lastName.first()}"
                            } else {
                                "PD"
                            }
                            Text(
                                initials.uppercase(),
                                color = Color(0xFF1577C8),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val fullName = if (state.firstName.isNotBlank()) {
                            "${state.firstName} ${state.lastName}"
                        } else {
                            "Cargando perfil…"
                        }
                        Text(
                            text = fullName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0B2B3B)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = state.role.ifBlank { "Productor" },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                            }
                            if (state.phone.isNotBlank()) {
                                Text(
                                    text = "· ${state.phone}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }

            // Datos personales editables
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                modifier = Modifier.fillMaxWidth()
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
                        Text(
                            "Datos personales",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B2B3B)
                        )
                        Surface(
                            onClick = { viewModel.emitEvent(ProfileEvent.ToggleEditMode) },
                            color = if (state.editMode) Color(0xFFF1F5F9) else Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (state.editMode) "Cancelar" else "Editar",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                fontSize = 12.sp,
                                color = if (state.editMode) Color(0xFF64748B) else Color(0xFF1577C8),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(90.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1577C8))
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = state.firstName,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdateFirstName(it)) },
                                label = { Text("Nombre") },
                                enabled = state.editMode,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = state.lastName,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdateLastName(it)) },
                                label = { Text("Apellido") },
                                enabled = state.editMode,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = state.phone,
                                onValueChange = { viewModel.emitEvent(ProfileEvent.UpdatePhone(it)) },
                                label = { Text("Teléfono") },
                                enabled = state.editMode,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        if (state.editMode) {
                            Button(
                                onClick = { viewModel.emitEvent(ProfileEvent.SaveProfile) },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
                            ) {
                                Text("Guardar cambios", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    if (state.error != null) {
                        Text(state.error!!, color = Color(0xFFDC2626), fontSize = 12.sp)
                    }
                    if (state.successMessage != null) {
                        Text(state.successMessage!!, color = Color(0xFF059669), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Modo de conexión híbrida Nube (Railway) vs Local (USB)
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                modifier = Modifier.fillMaxWidth()
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
                        Text(
                            "Servidor de datos",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B2B3B)
                        )
                        Surface(
                            color = if (!useLocalOnly) Color(0xFFECFDF5) else Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (!useLocalOnly) "Híbrido · Nube 24/7" else "Solo Local USB",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!useLocalOnly) Color(0xFF059669) else Color(0xFF1577C8)
                            )
                        }
                    }
                    Text(
                        "En modo Híbrido detecta tu PC local al instante si está conectada y usa la nube 24/7 cuando estás en campo.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF64748B)
                    )

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            listOf(
                                false to "Nube 24/7 + Local",
                                true to "Solo Local USB"
                            ).forEach { (localOnlyOption, label) ->
                                val selected = useLocalOnly == localOnlyOption
                                val bg by animateColorAsState(
                                    targetValue = if (selected) Color.White else Color.Transparent,
                                    animationSpec = tween(180),
                                    label = "ConnModeBg"
                                )
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { RemoteBackendConfig.setUseLocalOnly(localOnlyOption) },
                                    color = bg,
                                    shape = RoundedCornerShape(9.dp),
                                    shadowElevation = if (selected) 1.dp else 0.dp
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 9.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) Color(0xFF0B2B3B) else Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botón limpio de cierre de sesión
            Surface(
                onClick = onLogout,
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Cerrar sesión",
                        color = Color(0xFFDC2626),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
