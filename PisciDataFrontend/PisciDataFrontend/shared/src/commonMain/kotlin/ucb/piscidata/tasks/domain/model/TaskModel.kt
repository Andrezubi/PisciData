package ucb.piscidata.tasks.domain.model

enum class TaskCategory {
    ALIMENTACION, BIOMETRIA, PARAMETROS, OTRO;

    fun label(): String = when (this) {
        ALIMENTACION -> "Alimentación"
        BIOMETRIA -> "Biometría"
        PARAMETROS -> "Parámetros"
        OTRO -> "Otro"
    }
}

enum class TaskPriority {
    ALTA, MEDIA, BAJA
}

data class TaskModel(
    val id: Long,
    val done: Boolean,
    val category: TaskCategory,
    val title: String,
    val detail: String,
    val estanque: String,
    val priority: TaskPriority
)
