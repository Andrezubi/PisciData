package ucb.piscidata.tasks.presentation.viewmodel

import ucb.piscidata.tasks.domain.model.TaskCategory
import ucb.piscidata.tasks.domain.model.TaskModel
import ucb.piscidata.tasks.domain.model.TaskPriority

sealed interface TaskEvent {
    object LoadTasks : TaskEvent
    data class ToggleTask(val id: Long) : TaskEvent
    data class ChangeFilter(val filter: TaskFilter) : TaskEvent
    data class ShowAddDialog(val show: Boolean) : TaskEvent
    data class UpdateNewTitle(val title: String) : TaskEvent
    data class UpdateNewDetail(val detail: String) : TaskEvent
    data class UpdateNewCategory(val category: TaskCategory) : TaskEvent
    data class UpdateNewEstanque(val estanque: String) : TaskEvent
    data class UpdateNewPriority(val priority: TaskPriority) : TaskEvent
    object AddTask : TaskEvent
}
