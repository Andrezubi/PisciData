package ucb.piscidata.chat.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.chat.domain.model.MessageSender
import ucb.piscidata.chat.presentation.viewmodel.ChatEffect
import ucb.piscidata.chat.presentation.viewmodel.ChatEvent
import ucb.piscidata.chat.presentation.viewmodel.ChatViewModel
import ucb.piscidata.session.SessionManager
import ucb.piscidata.ui.components.FarmSelectorHeader
import ucb.piscidata.ui.components.MicIcon
import ucb.piscidata.ui.components.SendIcon

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
    var expandedDropdown by remember { mutableStateOf(false) }

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
        val total = state.messages.size + if (state.isLoading) 1 else 0
        if (total > 0) {
            listState.animateScrollToItem(total - 1)
        }
    }

    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            while (true) {
                delay(1000)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Farm Selector Header
            FarmSelectorHeader(
                farms = farms,
                selectedFarmId = selectedFarmId ?: 0,
                onSelectFarm = { sessionManager.selectFarm(it) }
            )

            // Header with Conversation Selector & New Conversation Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Asistente IA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B2B3B)
                    )

                    // Conversation Selector Box
                    Box {
                        Surface(
                            onClick = { expandedDropdown = true },
                            color = Color(0xFFE8F4FC),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = state.selectedConversation?.title ?: "Seleccionar conversación...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1577C8),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "▾",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1577C8)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            state.conversations.forEach { conv ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = conv.title,
                                            fontSize = 13.sp,
                                            fontWeight = if (conv.id == state.conversationId) FontWeight.Bold else FontWeight.Normal,
                                            color = if (conv.id == state.conversationId) Color(0xFF1577C8) else Color(0xFF0B2B3B)
                                        )
                                    },
                                    onClick = {
                                        expandedDropdown = false
                                        viewModel.emitEvent(ChatEvent.OnSelectConversation(conv.id))
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // New Conversation Button
                Button(
                    onClick = { viewModel.emitEvent(ChatEvent.OnOpenNewConversationDialog) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text("+ Nueva", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            HorizontalDivider(color = Color(0xFFDCE8ED))

            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF5F8FA))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.messages) { msg ->
                    when (msg.sender) {
                        MessageSender.IA -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF1577C8)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("IA", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (msg.isConfirmCard) {
                                    Surface(
                                        color = Color(0xFFE8F8F1),
                                        shape = RoundedCornerShape(4.dp, 24.dp, 24.dp, 24.dp),
                                        modifier = Modifier.widthIn(max = 280.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = "He identificado los siguientes datos:",
                                                fontSize = 13.sp,
                                                color = Color(0xFF16313D)
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Surface(color = Color(0xFFE8F4FC), shape = RoundedCornerShape(6.dp)) {
                                                    Text(
                                                        text = "Estanque: 3",
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF1577C8)
                                                    )
                                                }
                                                Surface(color = Color(0xFFE8F8F1), shape = RoundedCornerShape(6.dp)) {
                                                    Text(
                                                        text = "Especie: Tilapia",
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF19A974)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "¿Confirmas que deseas crear este ciclo productivo?",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF16313D)
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = { viewModel.emitEvent(ChatEvent.OnConfirmAction) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF19A974)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                                ) {
                                                    Text("Confirmar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                                OutlinedButton(
                                                    onClick = {},
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                                ) {
                                                    Text("Corregir", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B818B))
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Surface(
                                        color = Color(0xFFE8F8F1),
                                        shape = RoundedCornerShape(4.dp, 24.dp, 24.dp, 24.dp),
                                        modifier = Modifier.widthIn(max = 260.dp)
                                    ) {
                                        Text(
                                            text = msg.text ?: "",
                                            modifier = Modifier.padding(12.dp),
                                            fontSize = 13.sp,
                                            color = Color(0xFF16313D)
                                        )
                                    }
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
                                    shape = RoundedCornerShape(24.dp, 4.dp, 24.dp, 24.dp),
                                    modifier = Modifier.widthIn(max = 260.dp)
                                ) {
                                    Text(
                                        text = msg.text ?: "",
                                        modifier = Modifier.padding(12.dp),
                                        fontSize = 13.sp,
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
                                    color = Color(0xFF1360A8),
                                    shape = RoundedCornerShape(24.dp, 4.dp, 24.dp, 24.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Audio (${msg.durationSeconds ?: 0}s)",
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Burbuja indicadora de carga / pensando
                if (state.isLoading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = Color(0xFF1577C8)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("IA", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                color = Color(0xFFE8F8F1),
                                shape = RoundedCornerShape(4.dp, 24.dp, 24.dp, 24.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = Color(0xFF1577C8)
                                    )
                                    Text(
                                        text = "PisciData está pensando...",
                                        fontSize = 12.sp,
                                        color = Color(0xFF16313D)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.inputMessage,
                        onValueChange = { viewModel.emitEvent(ChatEvent.OnInputChanged(it)) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un mensaje...", color = Color(0xFF91A5AD)) },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        enabled = !state.isLoading
                    )
                    if (state.inputMessage.isBlank()) {
                        IconButton(
                            onClick = { viewModel.emitEvent(ChatEvent.OnStartRecording) },
                            enabled = !state.isLoading,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFE8F4FC), CircleShape)
                        ) {
                            MicIcon(Color(0xFF1577C8))
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.emitEvent(ChatEvent.OnSendText) },
                            enabled = !state.isLoading,
                            modifier = Modifier
                                .size(44.dp)
                                .background(if (state.isLoading) Color.LightGray else Color(0xFF1577C8), CircleShape)
                        ) {
                            SendIcon(Color.White)
                        }
                    }
                }
            }
        }
    }

    // Dialog for creating a new conversation
    if (state.showNewConversationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.emitEvent(ChatEvent.OnCloseNewConversationDialog) },
            title = { Text("Nueva Conversación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingresa el título para la nueva conversación:", fontSize = 13.sp)
                    OutlinedTextField(
                        value = state.newConversationTitleInput,
                        onValueChange = { viewModel.emitEvent(ChatEvent.OnNewConversationTitleChanged(it)) },
                        placeholder = { Text("Ej. Manejo de Estanque 3") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.emitEvent(ChatEvent.OnCreateConversation) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8))
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.emitEvent(ChatEvent.OnCloseNewConversationDialog) }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
