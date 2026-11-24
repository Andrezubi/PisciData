package ucb.piscidata.chat.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.chat.domain.model.ConversationModel
import ucb.piscidata.chat.domain.model.MessageSender
import ucb.piscidata.chat.presentation.audio.rememberAudioRecorder
import ucb.piscidata.chat.presentation.viewmodel.ChatEffect
import ucb.piscidata.chat.presentation.viewmodel.ChatEvent
import ucb.piscidata.chat.presentation.viewmodel.ChatViewModel
import ucb.piscidata.session.SessionManager
import ucb.piscidata.ui.components.FarmSelectorHeader
import ucb.piscidata.ui.components.MarkdownContent
import ucb.piscidata.ui.components.MenuLinesIcon
import ucb.piscidata.ui.components.MicIcon
import ucb.piscidata.ui.components.PisciDataLogo
import ucb.piscidata.ui.components.PlusIcon
import ucb.piscidata.ui.components.SendIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val farms by sessionManager.farms.collectAsState()
    val selectedFarmId by sessionManager.selectedFarmId.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val audioRecorder = rememberAudioRecorder()

    val isFreshWelcome = state.messages.size <= 1 &&
        state.messages.none { it.sender == MessageSender.USER || it.sender == MessageSender.AUDIO } &&
        !state.isLoading

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ChatEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(state.messages.size, state.isLoading) {
        if (!isFreshWelcome) {
            val total = state.messages.size + if (state.isLoading) 1 else 0
            if (total > 0) {
                listState.animateScrollToItem(total - 1)
            }
        }
    }

    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            while (true) {
                delay(1000)
                viewModel.emitEvent(ChatEvent.OnTickRecording)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Única barra superior minimalista: Historial [☰] + Cápsula de Granja + [+ Nueva]
            FarmSelectorHeader(
                farms = farms,
                selectedFarmId = selectedFarmId,
                onSelectFarm = { sessionManager.selectFarm(it) },
                subtitle = state.selectedConversation?.title ?: "Asistente IA",
                leadingAction = {
                    Surface(
                        onClick = { viewModel.emitEvent(ChatEvent.OnToggleHistorySheet(true)) },
                        color = Color(0xFFF4F7FA),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            MenuLinesIcon(color = Color(0xFF0B2B3B), modifier = Modifier.size(17.dp))
                        }
                    }
                },
                trailingActions = {
                    Surface(
                        onClick = { viewModel.emitEvent(ChatEvent.OnOpenNewConversationDialog) },
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFD6E6FA))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            PlusIcon(color = Color(0xFF1577C8), modifier = Modifier.size(13.dp))
                            Text(
                                text = "Nueva",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1577C8)
                            )
                        }
                    }
                }
            )

            // Lienzo principal estilo ChatGPT / Claude
            if (isFreshWelcome) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Surface(
                            color = Color(0xFFF4F7FA),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, Color(0xFFE5ECF2))
                        ) {
                            Box(modifier = Modifier.padding(10.dp)) {
                                PisciDataLogo(darkTheme = false, modifier = Modifier.size(44.dp))
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "¿Qué revisamos hoy?",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0B2B3B),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Consulta tus estanques, ciclos, alimento o registra datos hablando.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }

                        val suggestions = listOf(
                            Triple("Estado de estanques", "Oxígeno, pH y capacidad", "Muéstrame el estado actual de mis estanques"),
                            Triple("Ciclos activos", "Biomasa, edad y especie", "¿Cuáles son mis ciclos de producción activos?"),
                            Triple("Stock e insumos", "Alimento disponible hoy", "Revisa mi inventario de alimentos y suministros"),
                            Triple("Raciones del día", "Historial de alimentación", "¿Qué registros de alimentación tenemos?")
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            suggestions.chunked(2).forEach { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEach { (title, sub, prompt) ->
                                        Surface(
                                            onClick = { viewModel.emitEvent(ChatEvent.OnSendQuickPrompt(prompt)) },
                                            modifier = Modifier.weight(1f),
                                            color = Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(16.dp),
                                            border = BorderStroke(1.dp, Color(0xFFE5ECF2))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 12.dp),
                                                verticalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Text(
                                                    text = title,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0B2B3B),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = sub,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF64748B),
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
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(state.messages, key = { "${it.id}_${it.hashCode()}" }) { msg ->
                        when (msg.sender) {
                            MessageSender.IA -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        PisciDataLogo(darkTheme = false, modifier = Modifier.size(24.dp))
                                        Text(
                                            text = "PisciData",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0B2B3B)
                                        )
                                    }

                                    if (msg.isConfirmCard) {
                                        Box(modifier = Modifier.padding(start = 32.dp)) {
                                            ConfirmActionBubble(
                                                onConfirm = { viewModel.emitEvent(ChatEvent.OnConfirmAction) }
                                            )
                                        }
                                    } else {
                                        MarkdownContent(
                                            content = msg.text ?: "",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 32.dp, end = 4.dp)
                                        )
                                    }
                                }
                            }

                            MessageSender.USER -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Surface(
                                        color = Color(0xFF1577C8),
                                        shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
                                        modifier = Modifier.widthIn(max = 290.dp)
                                    ) {
                                        Text(
                                            text = msg.text ?: "",
                                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            MessageSender.AUDIO -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Surface(
                                        color = Color(0xFF0F5C9E),
                                        shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            MicIcon(color = Color.White, modifier = Modifier.size(15.dp))
                                            Text(
                                                text = "Nota de voz (${msg.durationSeconds ?: 0}s)",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (state.isLoading) {
                        item {
                            val thinkPulse = rememberInfiniteTransition(label = "ThinkPulse")
                            val logoScale by thinkPulse.animateFloat(
                                initialValue = 0.92f,
                                targetValue = 1.06f,
                                animationSpec = infiniteRepeatable(tween(680), RepeatMode.Reverse),
                                label = "ThinkScale"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PisciDataLogo(
                                    darkTheme = false,
                                    modifier = Modifier.size(24.dp).scale(logoScale)
                                )
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF1577C8)
                                )
                                Text(
                                    text = "Consultando tu piscigranja…",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }

            // Banner compacto de error con botón Reintentar
            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.errorMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFFB91C1C),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (state.lastFailedPrompt != null && !state.isLoading) {
                            TextButton(
                                onClick = { viewModel.emitEvent(ChatEvent.OnRetryLastMessage) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Reintentar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1577C8))
                            }
                        }
                    }
                }
            }

            // Indicador flotante de grabación o transcripción activa
            if (state.isRecording || state.isTranscribing) {
                Surface(
                    color = if (state.isRecording) Color(0xFFFEF2F2) else Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        1.dp,
                        if (state.isRecording) Color(0xFFFECACA) else Color(0xFFBFDBFE)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (state.isTranscribing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF1577C8)
                                )
                                Text(
                                    text = "Transcribiendo nota de voz…",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1577C8)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                                Text(
                                    text = "Grabando · suelta para enviar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                        if (state.isRecording) {
                            Text(
                                text = formatRecordTime(state.recordSeconds),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }

            // Cápsula inferior flotante de escritura estilo ChatGPT / Linear
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF4F7FA),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = state.inputMessage,
                            onValueChange = { viewModel.emitEvent(ChatEvent.OnInputChanged(it)) },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    "Pregunta sobre tu granja…",
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFF0B2B3B)
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = Color(0xFF1577C8)
                            ),
                            maxLines = 4,
                            enabled = !state.isLoading && !state.isTranscribing
                        )

                        if (state.inputMessage.isBlank()) {
                            val pulseTransition = rememberInfiniteTransition(label = "MicPulse")
                            val micScale by pulseTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = if (state.isRecording) 1.15f else 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(500),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "MicScale"
                            )
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .scale(micScale)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            state.isRecording -> Color(0xFFEF4444)
                                            state.isLoading || state.isTranscribing -> Color(0xFFE2E8F0)
                                            else -> Color.White
                                        }
                                    )
                                    .pointerInput(state.isLoading, state.isTranscribing) {
                                        if (state.isLoading || state.isTranscribing) return@pointerInput
                                        detectTapGestures(
                                            onPress = {
                                                viewModel.emitEvent(ChatEvent.OnStartRecording)
                                                audioRecorder.startRecording(
                                                    onError = {
                                                        viewModel.emitEvent(ChatEvent.OnCancelRecording)
                                                    }
                                                )
                                                val released = tryAwaitRelease()
                                                if (released) {
                                                    audioRecorder.stopRecording(
                                                        onAudioReady = { wavBytes ->
                                                            viewModel.emitEvent(ChatEvent.OnAudioRecorded(wavBytes))
                                                        },
                                                        onError = {
                                                            viewModel.emitEvent(ChatEvent.OnCancelRecording)
                                                        }
                                                    )
                                                } else {
                                                    audioRecorder.cancelRecording()
                                                    viewModel.emitEvent(ChatEvent.OnCancelRecording)
                                                }
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.isTranscribing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF1577C8)
                                    )
                                } else {
                                    MicIcon(
                                        color = if (state.isRecording) Color.White else Color(0xFF475569),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.emitEvent(ChatEvent.OnSendText) },
                                enabled = !state.isLoading && !state.isTranscribing,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (state.isLoading || state.isTranscribing) Color(0xFFCBD5E1)
                                        else Color(0xFF1577C8),
                                        CircleShape
                                    )
                            ) {
                                SendIcon(Color.White, modifier = Modifier.size(17.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Panel desplegable (Bottom Sheet) de conversaciones anteriores
    if (state.showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.emitEvent(ChatEvent.OnToggleHistorySheet(false)) },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp, max = 520.dp)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Historial de chats",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0B2B3B)
                        )
                        Text(
                            text = "${state.conversations.size} conversación(es)",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Surface(
                        onClick = { viewModel.emitEvent(ChatEvent.OnOpenNewConversationDialog) },
                        color = Color(0xFF1577C8),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            PlusIcon(Color.White, modifier = Modifier.size(13.dp))
                            Text("Nueva", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFEDF2F7))

                if (state.conversations.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aún no tienes conversaciones guardadas.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.conversations, key = { it.id }) { conv ->
                            ConversationHistoryItem(
                                conv = conv,
                                isSelected = conv.id == state.conversationId,
                                onClick = { viewModel.emitEvent(ChatEvent.OnSelectConversation(conv.id)) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo para nombrar nueva conversación
    if (state.showNewConversationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.emitEvent(ChatEvent.OnCloseNewConversationDialog) },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Nueva conversación",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Nombre opcional para ubicarla rápido en tu historial:",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                    OutlinedTextField(
                        value = state.newConversationTitleInput,
                        onValueChange = { viewModel.emitEvent(ChatEvent.OnNewConversationTitleChanged(it)) },
                        placeholder = { Text("Ej. Biometría Estanque 2") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.emitEvent(ChatEvent.OnCreateConversation) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Iniciar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.emitEvent(ChatEvent.OnCloseNewConversationDialog) }
                ) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            }
        )
    }
}

@Composable
private fun ConversationHistoryItem(
    conv: ConversationModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color(0xFF1577C8) else Color(0xFFE5ECF2)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = conv.title,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = Color(0xFF0B2B3B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val formattedDate = formatShortDate(conv.createdAt)
                if (formattedDate.isNotBlank()) {
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
            if (isSelected) {
                Text(
                    text = "✓ Activa",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1577C8)
                )
            }
        }
    }
}

@Composable
private fun ConfirmActionBubble(onConfirm: () -> Unit) {
    Surface(
        color = Color(0xFFF0FDF4),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier.widthIn(max = 290.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "¿Confirmas proceder con esta operación en tu piscigranja?",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0B2B3B)
            )
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF19A974)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Confirmar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

private fun formatRecordTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "$mins:${secs.toString().padStart(2, '0')}"
}

private fun formatShortDate(iso: String): String {
    if (iso.length < 10) return ""
    val datePart = iso.take(10).split("-")
    val timePart = if (iso.length >= 16) " · ${iso.substring(11, 16)}" else ""
    return if (datePart.size == 3) "${datePart[2]}/${datePart[1]}/${datePart[0]}$timePart" else iso.take(10)
}
