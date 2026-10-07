package ucb.piscidata.tasks.domain.repository

import ucb.piscidata.tasks.domain.model.TaskModel

interface TaskRepository {
    suspend fun getTasks(): Result<List<TaskModel>>
    suspend fun toggleTask(id: Long): Result<List<TaskModel>>
    suspend fun addTask(task: TaskModel): Result<List<TaskModel>>
}
