package ucb.piscidata.tasks.presentation.viewmodel

import ucb.piscidata.tasks.domain.model.TaskCategory
import ucb.piscidata.tasks.domain.model.TaskModel
import ucb.piscidata.tasks.domain.model.TaskPriority

enum class TaskFilter {
    TODAS, PENDIENTES, COMPLETADAS
}

data class TaskState(
    val tasks: List<TaskModel> = emptyList(),
    val filter: TaskFilter = TaskFilter.TODAS,
    val showAddDialog: Boolean = false,
    val newTitle: String = "",
    val newDetail: String = "",
    val newCategory: TaskCategory = TaskCategory.ALIMENTACION,
    val newEstanque: String = "Estanque 1",
    val newPriority: TaskPriority = TaskPriority.MEDIA,
    val isLoading: Boolean = false
)
