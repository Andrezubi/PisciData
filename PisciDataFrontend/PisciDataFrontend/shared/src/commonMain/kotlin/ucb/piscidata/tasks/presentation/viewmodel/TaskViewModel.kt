package ucb.piscidata.tasks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.tasks.domain.model.TaskModel
import ucb.piscidata.tasks.domain.usecase.AddTaskUseCase
import ucb.piscidata.tasks.domain.usecase.GetTasksUseCase
import ucb.piscidata.tasks.domain.usecase.ToggleTaskUseCase

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val addTaskUseCase: AddTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TaskEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadTasks()
    }

    fun emitEvent(event: TaskEvent) {
        when (event) {
            TaskEvent.LoadTasks -> loadTasks()
            is TaskEvent.ToggleTask -> toggleTask(event.id)
            is TaskEvent.ChangeFilter -> _state.update { it.copy(filter = event.filter) }
            is TaskEvent.ShowAddDialog -> _state.update { it.copy(showAddDialog = event.show) }
            is TaskEvent.UpdateNewTitle -> _state.update { it.copy(newTitle = event.title) }
            is TaskEvent.UpdateNewDetail -> _state.update { it.copy(newDetail = event.detail) }
            is TaskEvent.UpdateNewCategory -> _state.update { it.copy(newCategory = event.category) }
            is TaskEvent.UpdateNewEstanque -> _state.update { it.copy(newEstanque = event.estanque) }
            is TaskEvent.UpdateNewPriority -> _state.update { it.copy(newPriority = event.priority) }
            TaskEvent.AddTask -> addTask()
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            getTasksUseCase().onSuccess { tasks ->
                _state.update { it.copy(tasks = tasks) }
            }
        }
    }

    private fun toggleTask(id: Long) {
        viewModelScope.launch {
            toggleTaskUseCase(id).onSuccess { tasks ->
                _state.update { it.copy(tasks = tasks) }
            }
        }
    }

    private fun addTask() {
        val s = _state.value
        if (s.newTitle.isBlank()) return
        val task = TaskModel(
            id = kotlin.random.Random.nextLong(1000L, 999999L),
            done = false,
            category = s.newCategory,
            title = s.newTitle.trim(),
            detail = s.newDetail.trim(),
            estanque = s.newEstanque,
            priority = s.newPriority
        )
        viewModelScope.launch {
            addTaskUseCase(task).onSuccess { tasks ->
                _state.update {
                    it.copy(
                        tasks = tasks,
                        showAddDialog = false,
                        newTitle = "",
                        newDetail = ""
                    )
                }
            }
        }
    }
}
