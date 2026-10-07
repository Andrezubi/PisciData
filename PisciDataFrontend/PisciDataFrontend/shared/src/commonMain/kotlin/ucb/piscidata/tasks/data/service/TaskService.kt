package ucb.piscidata.tasks.data.service

import kotlinx.coroutines.delay
import ucb.piscidata.tasks.domain.model.TaskCategory
import ucb.piscidata.tasks.domain.model.TaskModel
import ucb.piscidata.tasks.domain.model.TaskPriority

class TaskService {
    private val tasks = mutableListOf(
        TaskModel(1, false, TaskCategory.ALIMENTACION, "Dar alimento F1 al Estanque 3", "5 kg — ración matutina", "Estanque 3", TaskPriority.ALTA),
        TaskModel(2, false, TaskCategory.ALIMENTACION, "Dar alimento F2 al Estanque 5", "7 kg — ración matutina", "Estanque 5", TaskPriority.ALTA),
        TaskModel(3, false, TaskCategory.BIOMETRIA, "Biometría del Estanque 2", "Pesar muestra de 30 peces", "Estanque 2", TaskPriority.MEDIA),
        TaskModel(4, false, TaskCategory.PARAMETROS, "Medir parámetros del agua", "pH, temperatura y O₂ disuelto", "Estanque 1", TaskPriority.MEDIA),
        TaskModel(5, false, TaskCategory.ALIMENTACION, "Dar alimento F1 al Estanque 3", "5 kg — ración vespertina", "Estanque 3", TaskPriority.BAJA),
        TaskModel(6, false, TaskCategory.OTRO, "Revisar red del Estanque 4", "Inspección visual perimetral", "Estanque 4", TaskPriority.BAJA)
    )

    suspend fun getTasks(): Result<List<TaskModel>> {
        delay(200)
        return Result.success(tasks.toList())
    }

    suspend fun toggleTask(id: Long): Result<List<TaskModel>> {
        delay(100)
        val idx = tasks.indexOfFirst { it.id == id }
        if (idx >= 0) {
            tasks[idx] = tasks[idx].copy(done = !tasks[idx].done)
        }
        return Result.success(tasks.toList())
    }

    suspend fun addTask(task: TaskModel): Result<List<TaskModel>> {
        delay(200)
        tasks.add(0, task)
        return Result.success(tasks.toList())
    }
}
