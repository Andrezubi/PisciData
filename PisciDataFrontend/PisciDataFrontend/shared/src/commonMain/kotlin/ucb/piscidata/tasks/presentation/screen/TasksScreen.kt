package ucb.piscidata.tasks.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.session.SessionManager
import ucb.piscidata.tasks.domain.model.TaskPriority
import ucb.piscidata.tasks.presentation.viewmodel.TaskEvent
import ucb.piscidata.tasks.presentation.viewmodel.TaskFilter
import ucb.piscidata.tasks.presentation.viewmodel.TaskViewModel
import ucb.piscidata.ui.components.DeleteIcon
import ucb.piscidata.ui.components.FarmSelectorHeader
import ucb.piscidata.ui.components.PlusIcon
import ucb.piscidata.ui.components.RefreshIcon

@Composable
fun TasksScreen(
    viewModel: TaskViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val farms by sessionManager.farms.collectAsState()
    val selectedFarmId by sessionManager.selectedFarmId.collectAsState()

    val filtered = state.tasks.filter {
        when (state.filter) {
            TaskFilter.TODAS -> true
            TaskFilter.PENDIENTES -> !it.done
            TaskFilter.COMPLETADAS -> it.done
        }
    }

    val doneCount = state.tasks.count { it.done }
    val totalCount = state.tasks.size
    val targetProgress = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 420),
        label = "TaskProgress"
    )
    val percentInt = (animatedProgress * 100).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Barra superior unificada con contador operativo y botón discreto de refresco
            FarmSelectorHeader(
                farms = farms,
                selectedFarmId = selectedFarmId,
                onSelectFarm = { sessionManager.selectFarm(it) },
                subtitle = if (totalCount > 0) "$doneCount de $totalCount tareas listas" else "Sin tareas hoy",
                trailingActions = {
                    Surface(
                        onClick = { viewModel.emitEvent(TaskEvent.LoadTasks) },
                        color = Color(0xFFF4F7FA),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Color(0xFFE5ECF2)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            RefreshIcon(color = Color(0xFF475569), modifier = Modifier.size(17.dp))
                        }
                    }
                }
            )

            // Sub-barra blanca: barra de progreso compacta + filtros segmentados uniformes
            Surface(color = Color.White) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progreso del día",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "$percentInt% completado",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFFF1F5F9)
                        )
                    }

                    // Selector segmentado limpio de 3 estados (sin sombras grises incoherentes)
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            listOf(
                                TaskFilter.TODAS to "Todas ($totalCount)",
                                TaskFilter.PENDIENTES to "Pendientes (${totalCount - doneCount})",
                                TaskFilter.COMPLETADAS to "Hechas ($doneCount)"
                            ).forEach { (f, label) ->
                                val selected = state.filter == f
                                val pillBg by animateColorAsState(
                                    targetValue = if (selected) Color.White else Color.Transparent,
                                    animationSpec = tween(180),
                                    label = "TaskFilterBg"
                                )
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .clickable { viewModel.emitEvent(TaskEvent.ChangeFilter(f)) },
                                    color = pillBg,
                                    shape = RoundedCornerShape(9.dp),
                                    shadowElevation = if (selected) 1.dp else 0.dp
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) Color(0xFF0B2B3B) else Color(0xFF64748B),
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

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFEDF2F7))

            if (state.error != null) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = state.error!!,
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        fontSize = 12.sp,
                        color = Color(0xFFB91C1C)
                    )
                }
            }

            if (state.isLoading && state.tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1577C8))
                }
            } else if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f).padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Todo despejado por aquí",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B2B3B)
                        )
                        Text(
                            text = "Toca «+ Nueva tarea» para programar una actividad.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { task ->
                        val priorityColor = when (task.priority) {
                            TaskPriority.ALTA -> Color(0xFFDC2626)
                            TaskPriority.MEDIA -> Color(0xFFD97706)
                            TaskPriority.BAJA -> Color(0xFF059669)
                        }
                        val priorityLabel = when (task.priority) {
                            TaskPriority.ALTA -> "Alta"
                            TaskPriority.MEDIA -> "Media"
                            TaskPriority.BAJA -> "Baja"
                        }
                        val cardBg by animateColorAsState(
                            targetValue = if (task.done) Color(0xFFF8FAFC) else Color.White,
                            animationSpec = tween(220),
                            label = "TaskCardBg"
                        )

                        // Toda la tarjeta es táctil para marcar/desmarcar con una sola mano
                        Surface(
                            onClick = { viewModel.emitEvent(TaskEvent.ToggleTask(task.id)) },
                            color = cardBg,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                1.dp,
                                if (task.done) Color(0xFFEDF2F7) else Color(0xFFE5ECF2)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Check circular minimalista estilo Linear / Things 3
                                Box(
                                    modifier = Modifier
                                        .padding(top = 1.dp)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .then(
                                            if (task.done) {
                                                Modifier.background(Color(0xFF10B981))
                                            } else {
                                                Modifier.border(
                                                    width = 1.8.dp,
                                                    color = Color(0xFFCBD5E1),
                                                    shape = CircleShape
                                                )
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (task.done) {
                                        Text(
                                            text = "✓",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = task.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (task.done) Color(0xFF94A3B8) else Color(0xFF0B2B3B),
                                        textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    if (task.detail.isNotBlank()) {
                                        Text(
                                            text = task.detail,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = if (task.done) Color(0xFFCBD5E1) else Color(0xFF64748B)
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = task.estanque,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                        Surface(
                                            color = priorityColor.copy(alpha = 0.10f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(priorityColor)
                                                )
                                                Text(
                                                    text = priorityLabel,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = priorityColor
                                                )
                                            }
                                        }
                                    }
                                }

                                Surface(
                                    onClick = { viewModel.emitEvent(TaskEvent.DeleteTask(task.id)) },
                                    color = Color.Transparent,
                                    shape = CircleShape,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        DeleteIcon(color = Color(0xFF94A3B8), modifier = Modifier.size(15.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB ergonómico al alcance del pulgar
        ExtendedFloatingActionButton(
            onClick = { viewModel.emitEvent(TaskEvent.ShowAddDialog(true)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp),
            containerColor = Color(0xFF1577C8),
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            ),
            icon = { PlusIcon(Color.White, modifier = Modifier.size(16.dp)) },
            text = {
                Text("Nueva tarea", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        )

        if (state.showAddDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.emitEvent(TaskEvent.ShowAddDialog(false)) },
                containerColor = Color.White,
                shape = RoundedCornerShape(22.dp),
                title = {
                    Text(
                        "Nueva tarea",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0B2B3B)
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.newTitle,
                            onValueChange = { viewModel.emitEvent(TaskEvent.UpdateNewTitle(it)) },
                            label = { Text("Actividad a realizar") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.newDetail,
                            onValueChange = { viewModel.emitEvent(TaskEvent.UpdateNewDetail(it)) },
                            label = { Text("Detalle opcional (ej. 5 kg F1)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "Prioridad",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                TaskPriority.ALTA to "Alta",
                                TaskPriority.MEDIA to "Media",
                                TaskPriority.BAJA to "Baja"
                            ).forEach { (p, label) ->
                                val selected = state.newPriority == p
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.emitEvent(TaskEvent.UpdateNewPriority(p)) },
                                    label = { Text(label, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.emitEvent(TaskEvent.AddTask) },
                        enabled = !state.isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            if (state.isSaving) "Guardando…" else "Guardar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.emitEvent(TaskEvent.ShowAddDialog(false)) }) {
                        Text("Cancelar", color = Color(0xFF64748B))
                    }
                }
            )
        }
    }
}
