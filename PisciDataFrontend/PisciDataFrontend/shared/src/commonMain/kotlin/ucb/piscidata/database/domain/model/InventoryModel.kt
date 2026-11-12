package ucb.piscidata.database.domain.model

enum class InvCategory {
    ALIMENTO, QUIMICO, COMBUSTIBLE, EQUIPO;

    fun label(): String = when (this) {
        ALIMENTO -> "Alimento"
        QUIMICO -> "Químico"
        COMBUSTIBLE -> "Combustible"
        EQUIPO -> "Equipo"
    }
}

data class InvItem(
    val id: Int,
    val name: String,
    val category: InvCategory,
    val qty: Int,
    val unit: String,
    val minQty: Int,
    val proteinPercentage: Double? = null, // Porcentaje de proteina para alimento
    val pelletSizeMm: Double? = null // Tamaño de pellet para alimento
)
