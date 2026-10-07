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
    val id: String,
    val status: BadgeVariant,
    val estanque: String,
    val especie: String,
    val peces: String,
    val inicio: String,
    val pesoInicial: String,
    val densidad: String,
    val alimento: String,
    val observaciones: String
)

enum class PondShape {
    RECTANGULAR, CIRCULAR, IRREGULAR
}

data class Pond(
    val id: Int,
    val nombre: String,
    val forma: PondShape,
    val largo: Double? = null,
    val ancho: Double? = null,
    val diametro: Double? = null,
    val area: Double,
    val profundidad: Double,
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
    val location: String
)
