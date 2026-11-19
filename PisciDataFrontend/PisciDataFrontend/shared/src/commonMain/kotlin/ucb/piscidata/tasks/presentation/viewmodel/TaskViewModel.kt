package ucb.piscidata.tasks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.session.SessionManager
import ucb.piscidata.tasks.domain.model.TaskModel
import ucb.piscidata.tasks.domain.usecase.AddTaskUseCase
import ucb.piscidata.tasks.domain.usecase.DeleteTaskUseCase
import ucb.piscidata.tasks.domain.usecase.GetTasksUseCase
import ucb.piscidata.tasks.domain.usecase.ToggleTaskUseCase

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TaskEffect>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            sessionManager.selectedFarmId.collectLatest {
                loadTasks()
            }
        }
    }

    fun emitEvent(event: TaskEvent) {
        when (event) {
            TaskEvent.LoadTasks -> loadTasks()
            is TaskEvent.ToggleTask -> toggleTask(event.id)
            is TaskEvent.DeleteTask -> deleteTask(event.id)
            is TaskEvent.ChangeFilter -> _state.update { it.copy(filter = event.filter) }
            is TaskEvent.ShowAddDialog -> _state.update { it.copy(showAddDialog = event.show, error = null) }
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
            _state.update { it.copy(isLoading = true, error = null) }
            getTasksUseCase()
                .onSuccess { tasks ->
                    _state.update { it.copy(tasks = tasks, isLoading = false, error = null) }
                }
                .onFailure { err ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = err.message ?: "No se pudieron cargar las tareas"
                        )
                    }
                }
        }
    }

    private fun toggleTask(id: Long) {
        // Optimistic update for snappy UI
        val previous = _state.value.tasks
        _state.update { s ->
            s.copy(tasks = s.tasks.map { if (it.id == id) it.copy(done = !it.done) else it })
        }
        viewModelScope.launch {
            toggleTaskUseCase(id)
                .onSuccess { tasks ->
                    _state.update { it.copy(tasks = tasks, error = null) }
                }
                .onFailure { err ->
                    _state.update {
                        it.copy(
                            tasks = previous,
                            error = err.message ?: "Error al actualizar la tarea"
                        )
                    }
                }
        }
    }

    private fun deleteTask(id: Long) {
        viewModelScope.launch {
            deleteTaskUseCase(id)
                .onSuccess { tasks ->
                    _state.update { it.copy(tasks = tasks, error = null) }
                }
                .onFailure { err ->
                    _state.update { it.copy(error = err.message ?: "No se pudo eliminar la tarea") }
                }
        }
    }

    private fun addTask() {
        val s = _state.value
        if (s.newTitle.isBlank()) {
            _state.update { it.copy(error = "Escribe un título para la tarea") }
            return
        }
        val task = TaskModel(
            id = 0L,
            done = false,
            category = s.newCategory,
            title = s.newTitle.trim(),
            detail = s.newDetail.trim(),
            estanque = s.newEstanque,
            priority = s.newPriority
        )
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            addTaskUseCase(task)
                .onSuccess { tasks ->
                    _state.update {
                        it.copy(
                            tasks = tasks,
                            showAddDialog = false,
                            newTitle = "",
                            newDetail = "",
                            isSaving = false,
                            error = null
                        )
                    }
                }
                .onFailure { err ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = err.message ?: "No se pudo guardar la tarea"
                        )
                    }
                }
        }
    }
}
