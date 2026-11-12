package ucb.piscidata.database.domain.model

enum class BadgeVariant {
    ACTIVO, EN_SEGUIMIENTO, FINALIZADO;

    fun displayName(): String = when (this) {
        ACTIVO -> "Activo"
        EN_SEGUIMIENTO -> "En seguimiento"
        FINALIZADO -> "Finalizado"
    }
}

data class CicloRecord(
    val id: String, // Codigo CicloProductivo
    val status: BadgeVariant,
    val estanque: String,
    val especie: String,
    val peces: String, // Cantidad Peces Actual
    val pesoActual: String, // Peso Promedio Actual
    val edadDias: Int, // Edad en Dias
    val startDate: String, // Fecha Inicio
    val endDate: String?, // Fecha Fin
    val initialAgeDays: Int, // Edad Inicial
    val initialWeightGrams: String, // Peso Inicial
    val initialFishCount: String, // Cantidad Inicial
    val observations: String, // Observaciones
    val isExpanded: Boolean = false // Desplegable
)
