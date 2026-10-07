package ucb.piscidata.tasks.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ucb.piscidata.tasks.domain.model.TaskCategory
import ucb.piscidata.tasks.domain.model.TaskPriority
import ucb.piscidata.tasks.presentation.viewmodel.TaskEvent
import ucb.piscidata.tasks.presentation.viewmodel.TaskFilter
import ucb.piscidata.tasks.presentation.viewmodel.TaskViewModel

@Composable
fun TasksScreen(
    viewModel: TaskViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    val filtered = state.tasks.filter {
        when (state.filter) {
            TaskFilter.TODAS -> true
            TaskFilter.PENDIENTES -> !it.done
            TaskFilter.COMPLETADAS -> it.done
        }
    }

    val doneCount = state.tasks.count { it.done }
    val totalCount = state.tasks.size
    val progress = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tareas del día", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                    Text("Gestión operativa diaria", fontSize = 12.sp, color = Color(0xFF6B818B))
                }
                Button(
                    onClick = { viewModel.emitEvent(TaskEvent.ShowAddDialog(true)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Nueva", fontSize = 12.sp, color = Color.White)
                }
            }

            // Progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = Color(0xFF19A974),
                    trackColor = Color(0xFFDCE8ED),
                )
                Text(
                    text = "$doneCount de $totalCount completadas",
                    fontSize = 12.sp,
                    color = Color(0xFF6B818B)
                )
            }

            // Filter pills
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    TaskFilter.TODAS to "Todas",
                    TaskFilter.PENDIENTES to "Pendientes",
                    TaskFilter.COMPLETADAS to "Completadas"
                ).forEach { (f, label) ->
                    val selected = state.filter == f
                    Button(
                        onClick = { viewModel.emitEvent(TaskEvent.ChangeFilter(f)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Color(0xFF1577C8) else Color(0xFFF0F4F6)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = if (selected) Color.White else Color(0xFF6B818B)
                        )
                    }
                }
            }

            // Tasks list
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { task ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.done,
                                onCheckedChange = { viewModel.emitEvent(TaskEvent.ToggleTask(task.id)) }
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = task.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (task.done) Color(0xFF91A5AD) else Color(0xFF0B2B3B),
                                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                                )
                                if (task.detail.isNotBlank()) {
                                    Text(task.detail, fontSize = 12.sp, color = Color(0xFF6B818B))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        color = Color(0xFFE8F4FC),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = task.estanque,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            color = Color(0xFF1577C8)
                                        )
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
                title = { Text("Nueva Tarea") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state.newTitle,
                            onValueChange = { viewModel.emitEvent(TaskEvent.UpdateNewTitle(it)) },
                            label = { Text("Título de la tarea") }
                        )
                        OutlinedTextField(
                            value = state.newDetail,
                            onValueChange = { viewModel.emitEvent(TaskEvent.UpdateNewDetail(it)) },
                            label = { Text("Detalle (opcional)") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.emitEvent(TaskEvent.AddTask) }) {
                        Text("Agregar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.emitEvent(TaskEvent.ShowAddDialog(false)) }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
