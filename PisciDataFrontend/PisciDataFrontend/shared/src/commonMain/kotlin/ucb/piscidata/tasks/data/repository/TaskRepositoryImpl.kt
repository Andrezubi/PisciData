package ucb.piscidata.tasks.data.repository

import ucb.piscidata.tasks.data.service.TaskService
import ucb.piscidata.tasks.domain.model.TaskModel
import ucb.piscidata.tasks.domain.repository.TaskRepository

class TaskRepositoryImpl(
    private val service: TaskService
) : TaskRepository {
    override suspend fun getTasks(): Result<List<TaskModel>> = service.getTasks()
    override suspend fun toggleTask(id: Long): Result<List<TaskModel>> = service.toggleTask(id)
    override suspend fun addTask(task: TaskModel): Result<List<TaskModel>> = service.addTask(task)
}
