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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.chat.domain.model.MessageSender
import ucb.piscidata.chat.presentation.viewmodel.ChatEvent
import ucb.piscidata.chat.presentation.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            var secs = 0
            while (true) {
                delay(1000)
                secs++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Asistente IA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B)
                )
                Text(
                    text = "Carlos Mendoza (Admin)",
                    fontSize = 12.sp,
                    color = Color(0xFF6B818B)
                )
            }
            Surface(
                color = Color(0xFFE8F4FC),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Nuevo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1577C8)
                    )
                }
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
                    maxLines = 3
                )
                if (state.inputMessage.isBlank()) {
                    IconButton(
                        onClick = { viewModel.emitEvent(ChatEvent.OnStartRecording) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFE8F4FC), CircleShape)
                    ) {
                        Text("🎤", fontSize = 18.sp)
                    }
                } else {
                    IconButton(
                        onClick = { viewModel.emitEvent(ChatEvent.OnSendText) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF1577C8), CircleShape)
                    ) {
                        Text("➤", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
