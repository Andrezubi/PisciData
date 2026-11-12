package ucb.piscidata.database.domain.model

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
