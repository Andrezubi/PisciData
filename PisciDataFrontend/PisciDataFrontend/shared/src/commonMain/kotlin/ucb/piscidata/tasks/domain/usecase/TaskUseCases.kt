package ucb.piscidata.tasks.domain.usecase

import ucb.piscidata.tasks.domain.model.TaskModel
import ucb.piscidata.tasks.domain.repository.TaskRepository

class GetTasksUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(): Result<List<TaskModel>> = repository.getTasks()
}

class ToggleTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long): Result<List<TaskModel>> = repository.toggleTask(id)
}

class AddTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: TaskModel): Result<List<TaskModel>> = repository.addTask(task)
}
