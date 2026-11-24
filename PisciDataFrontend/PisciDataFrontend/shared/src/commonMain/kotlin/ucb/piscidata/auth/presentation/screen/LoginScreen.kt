package ucb.piscidata.auth.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import piscidatafrontend.shared.generated.resources.Res
import piscidatafrontend.shared.generated.resources.img_login_bg
import ucb.piscidata.auth.presentation.viewmodel.AuthEvent
import ucb.piscidata.auth.presentation.viewmodel.AuthViewModel
import ucb.piscidata.ui.components.FloatingNotificationBanner
import ucb.piscidata.ui.components.NotificationKind
import ucb.piscidata.ui.components.PisciDataLogo
import ucb.piscidata.ui.components.classifyNotification

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var visible by remember { mutableStateOf(false) }
    var bannerMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(state.error) {
        bannerMessage = state.error
        if (state.error != null) {
            delay(5000)
            bannerMessage = null
        }
    }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onLoginSuccess()
        }
    }

    val isCredentialWarning = state.error != null && classifyNotification(state.error) == NotificationKind.WARNING

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
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
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.img_login_bg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0B2B3B).copy(alpha = 0.5f))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PisciDataLogo(darkTheme = true, modifier = Modifier.size(42.dp))
                            Text(
                                text = "PisciData",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 3 }
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xDD073044)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "PRODUCCIÓN INTELIGENTE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4EC5C1)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Cultiva con precisión. Crece con confianza.",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Bienvenido",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B2B3B)
                            )
                            Text(
                                text = "Gestiona tu piscigranja con datos precisos y decisiones inteligentes.",
                                fontSize = 14.sp,
                                color = Color(0xFF6B818B)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            OutlinedTextField(
                                value = state.phone,
                                onValueChange = { viewModel.emitEvent(AuthEvent.OnPhoneChanged(it)) },
                                label = { Text("Número de Teléfono") },
                                isError = isCredentialWarning,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = state.password,
                                onValueChange = { viewModel.emitEvent(AuthEvent.OnPasswordChanged(it)) },
                                label = { Text("Contraseña") },
                                isError = isCredentialWarning,
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { viewModel.emitEvent(AuthEvent.OnLoginClicked) },
                                enabled = !state.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Text(
                                        text = "Iniciar sesión",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onNavigateToRegister,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "¿No tienes cuenta? Regístrate",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1577C8)
                                )
                            }
                        }
                    }

                    Text(
                        text = "© 2026 PisciData · Gestión acuícola inteligente",
                        fontSize = 11.sp,
                        color = Color(0xFF91A5AD),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // Notificación flotante superior diferenciada (Ámbar para datos / Coral para red)
        FloatingNotificationBanner(
            message = bannerMessage,
            onDismiss = { bannerMessage = null },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )
    }
}
