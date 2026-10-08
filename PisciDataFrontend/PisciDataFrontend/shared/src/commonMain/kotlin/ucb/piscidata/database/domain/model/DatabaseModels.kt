package ucb.piscidata.database.domain.model

data class FarmModel(
    val id: Int,
    val name: String
)

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

enum class PondShape {
    RECTANGULAR, CIRCULAR, IRREGULAR
}

data class Pond(
    val id: Int,
    val code: String, // Codigo de estanque
    val nombre: String,
    val forma: PondShape,
    val largo: Double? = null,
    val ancho: Double? = null,
    val diametro: Double? = null,
    val area: Double,
    val profundidad: Double,
    val transparenciaCm: Double?, // Transparencia en cm
    val ph: Double,
    val claridad: String,
    val temperatura: Double,
    val oxigeno: Double,
    val cicloActivo: String?,
    val especie: String?
)

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
