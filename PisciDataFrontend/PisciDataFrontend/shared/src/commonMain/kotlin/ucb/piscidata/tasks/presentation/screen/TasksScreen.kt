package ucb.piscidata.tasks.presentation.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.session.SessionManager
import ucb.piscidata.tasks.domain.model.TaskPriority
import ucb.piscidata.tasks.presentation.viewmodel.TaskEvent
import ucb.piscidata.tasks.presentation.viewmodel.TaskFilter
import ucb.piscidata.tasks.presentation.viewmodel.TaskViewModel
import ucb.piscidata.ui.components.FarmSelectorHeader

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
        animationSpec = tween(durationMillis = 450),
        label = "TaskProgress"
    )
    val percentInt = (animatedProgress * 100).toInt()

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Tareas del día",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0B2B3B)
                        )
                        Text(
                            "Gestión operativa sincronizada",
                            fontSize = 12.sp,
                            color = Color(0xFF6B818B)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.emitEvent(TaskEvent.LoadTasks) },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("↻", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1577C8))
                        }
                        Button(
                            onClick = { viewModel.emitEvent(TaskEvent.ShowAddDialog(true)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text("+ Nueva", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Progress Hero Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
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
                            Text(
                                text = "Progreso operativo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B2B3B)
                            )
                            Surface(
                                color = Color(0xFFE6F7F0),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "$percentInt% · $doneCount de $totalCount",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF19A974)
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = Color(0xFF19A974),
                            trackColor = Color(0xFFE3EDF2)
                        )
                    }
                }

                // Filter segmented pills
                Surface(
                    color = Color(0xFFE9F0F4),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            TaskFilter.TODAS to "Todas",
                            TaskFilter.PENDIENTES to "Pendientes",
                            TaskFilter.COMPLETADAS to "Completadas"
                        ).forEach { (f, label) ->
                            val selected = state.filter == f
                            val bg by animateColorAsState(
                                targetValue = if (selected) Color(0xFF1577C8) else Color.Transparent,
                                animationSpec = tween(220),
                                label = "FilterBg"
                            )
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.emitEvent(TaskEvent.ChangeFilter(f)) },
                                color = bg,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFF5A707A)
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.error != null) {
                    Surface(
                        color = Color(0xFFFFF0F0),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = state.error!!,
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            fontSize = 12.sp,
                            color = Color(0xFFE05A5A)
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
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Sin tareas en esta vista",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B2B3B)
                            )
                            Text(
                                text = "Presiona \"+ Nueva\" para registrar una actividad.",
                                fontSize = 12.sp,
                                color = Color(0xFF6B818B)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered, key = { it.id }) { task ->
                            val priorityColor = when (task.priority) {
                                TaskPriority.ALTA -> Color(0xFFE05A5A)
                                TaskPriority.MEDIA -> Color(0xFFE08B00)
                                TaskPriority.BAJA -> Color(0xFF19A974)
                            }
                            val cardBg by animateColorAsState(
                                targetValue = if (task.done) Color(0xFFF7FAF8) else Color.White,
                                animationSpec = tween(240),
                                label = "TaskCardBg"
                            )

                            Card(
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                shape = RoundedCornerShape(14.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (task.done) 0.dp else 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min)
                                ) {
                                    // Priority indicator bar
                                    Box(
                                        modifier = Modifier
                                            .width(5.dp)
                                            .fillMaxHeight()
                                            .background(if (task.done) Color(0xFF19A974) else priorityColor)
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = task.done,
                                            onCheckedChange = { viewModel.emitEvent(TaskEvent.ToggleTask(task.id)) },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF19A974),
                                                uncheckedColor = Color(0xFF91A5AD)
                                            )
                                        )
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = task.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (task.done) Color(0xFF8AA0AA) else Color(0xFF0B2B3B),
                                                textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            if (task.detail.isNotBlank()) {
                                                Text(
                                                    text = task.detail,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF6B818B)
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Surface(
                                                    color = Color(0xFFE6F2FB),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = task.estanque,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF1577C8)
                                                    )
                                                }
                                                Surface(
                                                    color = priorityColor.copy(alpha = 0.12f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = task.priority.name,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = priorityColor
                                                    )
                                                }
                                            }
                                        }
                                        IconButton(
                                            onClick = { viewModel.emitEvent(TaskEvent.DeleteTask(task.id)) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Text("✕", fontSize = 13.sp, color = Color(0xFF91A5AD))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (state.showAddDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.emitEvent(TaskEvent.ShowAddDialog(false)) },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Text(
                            "Nueva Tarea",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B2B3B)
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = state.newTitle,
                                onValueChange = { viewModel.emitEvent(TaskEvent.UpdateNewTitle(it)) },
                                label = { Text("Título de la tarea") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = state.newDetail,
                                onValueChange = { viewModel.emitEvent(TaskEvent.UpdateNewDetail(it)) },
                                label = { Text("Detalle (ej. 5 kg F1, 30 peces)") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("Prioridad", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B818B))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(
                                    TaskPriority.ALTA to "Alta",
                                    TaskPriority.MEDIA to "Normal",
                                    TaskPriority.BAJA to "Baja"
                                ).forEach { (p, label) ->
                                    val selected = state.newPriority == p
                                    FilterChip(
                                        selected = selected,
                                        onClick = { viewModel.emitEvent(TaskEvent.UpdateNewPriority(p)) },
                                        label = { Text(label, fontSize = 11.sp) }
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
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (state.isSaving) "Guardando..." else "Agregar", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.emitEvent(TaskEvent.ShowAddDialog(false)) }) {
                            Text("Cancelar", color = Color(0xFF6B818B))
                        }
                    }
                )
            }
        }
    }
}
