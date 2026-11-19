package ucb.piscidata.tasks.data.service

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.data.remote.RemoteBackendConfig
import ucb.piscidata.session.SessionManager
import ucb.piscidata.tasks.domain.model.TaskCategory
import ucb.piscidata.tasks.domain.model.TaskModel
import ucb.piscidata.tasks.domain.model.TaskPriority

@Serializable
data class TaskResponseDto(
    val id: Int,
    val farmId: Int,
    val pondId: Int? = null,
    val pondName: String? = null,
    val title: String,
    val description: String? = null,
    val scheduledDate: String? = null,
    val completedDate: String? = null,
    val priority: String = "Normal",
    val completed: Boolean = false
)

@Serializable
data class CreateTaskRequestDto(
    val farmId: Int,
    val pondId: Int? = null,
    val title: String,
    val description: String? = null,
    val priority: String = "Normal"
)

class TaskService {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private fun farmId(): Int = sessionManager.selectedFarmId.value

    suspend fun getTasks(): Result<List<TaskModel>> {
        val token = sessionManager.token.value
        val currentFarmId = farmId()
        val path = if (currentFarmId > 0) "/api/tasks/farm/$currentFarmId" else "/api/tasks"
        var lastError: Throwable? = null

        for (url in RemoteBackendConfig.urlsFor(path)) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (response.status.value in 200..299) {
                    val dtos: List<TaskResponseDto> = response.body()
                    return Result.success(dtos.map { it.toModel() })
                }
                lastError = Exception("Error HTTP ${response.status.value} al cargar tareas")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("No se pudo conectar al servidor de tareas"))
    }

    suspend fun toggleTask(id: Long): Result<List<TaskModel>> {
        val token = sessionManager.token.value
        var lastError: Throwable? = null

        for (url in RemoteBackendConfig.urlsFor("/api/tasks/$id/toggle")) {
            try {
                val response = client.patch(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (response.status.value in 200..299) {
                    return getTasks()
                }
                lastError = Exception("Error HTTP ${response.status.value} al actualizar tarea")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("No se pudo actualizar la tarea"))
    }

    suspend fun addTask(task: TaskModel): Result<List<TaskModel>> {
        val token = sessionManager.token.value
        val currentFarmId = farmId()
        if (currentFarmId <= 0) {
            return Result.failure(Exception("Selecciona una granja primero para crear tareas."))
        }
        val body = CreateTaskRequestDto(
            farmId = currentFarmId,
            pondId = null,
            title = task.title.trim(),
            description = task.detail.trim().ifBlank { null },
            priority = when (task.priority) {
                TaskPriority.ALTA -> "Alta"
                TaskPriority.MEDIA -> "Normal"
                TaskPriority.BAJA -> "Baja"
            }
        )
        var lastError: Throwable? = null

        for (url in RemoteBackendConfig.urlsFor("/api/tasks")) {
            try {
                val response = client.post(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                if (response.status.value in 200..299) {
                    return getTasks()
                }
                lastError = Exception("Error HTTP ${response.status.value} al crear tarea")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("No se pudo crear la tarea"))
    }

    suspend fun deleteTask(id: Long): Result<List<TaskModel>> {
        val token = sessionManager.token.value
        var lastError: Throwable? = null

        for (url in RemoteBackendConfig.urlsFor("/api/tasks/$id")) {
            try {
                val response = client.delete(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (response.status.value in 200..299) {
                    return getTasks()
                }
                lastError = Exception("Error HTTP ${response.status.value} al eliminar tarea")
            } catch (e: Exception) {
                lastError = e
            }
        }
        return Result.failure(lastError ?: Exception("No se pudo eliminar la tarea"))
    }

    private fun TaskResponseDto.toModel(): TaskModel {
        val combined = "$title ${description.orEmpty()}".lowercase()
        val inferredCategory = when {
            combined.contains("aliment") || combined.contains("ración") || combined.contains("racion") || combined.contains("kg") -> TaskCategory.ALIMENTACION
            combined.contains("biometr") || combined.contains("pesar") || combined.contains("muestra") -> TaskCategory.BIOMETRIA
            combined.contains("ph") || combined.contains("oxígeno") || combined.contains("oxigeno") || combined.contains("agua") || combined.contains("parámetro") -> TaskCategory.PARAMETROS
            else -> TaskCategory.OTRO
        }
        val mappedPriority = when (priority.trim().lowercase()) {
            "alta", "high" -> TaskPriority.ALTA
            "baja", "low" -> TaskPriority.BAJA
            else -> TaskPriority.MEDIA
        }
        return TaskModel(
            id = id.toLong(),
            done = completed,
            category = inferredCategory,
            title = title,
            detail = description.orEmpty(),
            estanque = pondName?.ifBlank { "General" } ?: "General",
            priority = mappedPriority
        )
    }
}
