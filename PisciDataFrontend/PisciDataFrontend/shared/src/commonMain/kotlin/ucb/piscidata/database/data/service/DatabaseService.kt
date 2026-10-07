package ucb.piscidata.database.data.service

import kotlinx.coroutines.delay
import ucb.piscidata.database.domain.model.*

class DatabaseService {
    private val ciclos = mutableListOf(
        CicloRecord("CP-2026-018", BadgeVariant.ACTIVO, "Estanque 3", "Tilapia nilótica", "15.400", "2026-08-12", "12", "21.8", "F1", ""),
        CicloRecord("CP-2026-017", BadgeVariant.ACTIVO, "Estanque 1", "Trucha arcoíris", "8.250", "2026-07-28", "10", "10.3", "F2", ""),
        CicloRecord("CP-2026-014", BadgeVariant.EN_SEGUIMIENTO, "Estanque 5", "Cachama blanca", "12.000", "2026-06-05", "8", "12.1", "F2", "Monitoreo de temperatura semanal"),
        CicloRecord("CP-2026-011", BadgeVariant.FINALIZADO, "Estanque 2", "Tilapia roja", "10.800", "2026-03-18", "14", "17.1", "F1", "Ciclo completado exitosamente")
    )

    private val ponds = listOf(
        Pond(1, "Estanque 1", PondShape.RECTANGULAR, largo = 40.0, ancho = 20.0, area = 800.0, profundidad = 1.8, ph = 7.2, claridad = "Alta", temperatura = 24.0, oxigeno = 6.8, cicloActivo = "CP-2026-017", especie = "Trucha arcoíris"),
        Pond(2, "Estanque 2", PondShape.RECTANGULAR, largo = 35.0, ancho = 18.0, area = 630.0, profundidad = 1.6, ph = 7.0, claridad = "Media", temperatura = 25.0, oxigeno = 6.2, cicloActivo = null, especie = null),
        Pond(3, "Estanque 3", PondShape.CIRCULAR, diametro = 30.0, area = 707.0, profundidad = 2.0, ph = 7.4, claridad = "Alta", temperatura = 26.0, oxigeno = 7.1, cicloActivo = "CP-2026-018", especie = "Tilapia nilótica"),
        Pond(4, "Estanque 4", PondShape.IRREGULAR, area = 550.0, profundidad = 1.5, ph = 6.8, claridad = "Baja", temperatura = 27.0, oxigeno = 5.4, cicloActivo = null, especie = null),
        Pond(5, "Estanque 5", PondShape.RECTANGULAR, largo = 45.0, ancho = 22.0, area = 990.0, profundidad = 1.9, ph = 7.1, claridad = "Media", temperatura = 25.0, oxigeno = 6.5, cicloActivo = "CP-2026-014", especie = "Cachama blanca")
    )

    private val inventory = mutableListOf(
        InvItem(1, "Alimento F1", InvCategory.ALIMENTO, 320, "kg", 100, "Bodega A"),
        InvItem(2, "Alimento F2", InvCategory.ALIMENTO, 200, "kg", 80, "Bodega A"),
        InvItem(3, "Alimento F3", InvCategory.ALIMENTO, 55, "kg", 60, "Bodega A"),
        InvItem(4, "Cal agrícola", InvCategory.QUIMICO, 100, "kg", 50, "Bodega B"),
        InvItem(5, "Permanganato KMnO₄", InvCategory.QUIMICO, 8, "kg", 5, "Bodega B"),
        InvItem(6, "Gasolina", InvCategory.COMBUSTIBLE, 50, "L", 20, "Tanque ext."),
        InvItem(7, "Aceite motor", InvCategory.COMBUSTIBLE, 12, "L", 5, "Tanque ext."),
        InvItem(8, "Red de pesca", InvCategory.EQUIPO, 3, "und", 2, "Almacén")
    )

    suspend fun getCiclos(): Result<List<CicloRecord>> {
        delay(300)
        return Result.success(ciclos.toList())
    }

    suspend fun saveCiclo(ciclo: CicloRecord): Result<Unit> {
        delay(300)
        val idx = ciclos.indexOfFirst { it.id == ciclo.id }
        if (idx >= 0) {
            ciclos[idx] = ciclo
        } else {
            ciclos.add(0, ciclo)
        }
        return Result.success(Unit)
    }

    suspend fun deleteCiclo(id: String): Result<Unit> {
        delay(300)
        ciclos.removeAll { it.id == id }
        return Result.success(Unit)
    }

    suspend fun getPonds(): Result<List<Pond>> {
        delay(300)
        return Result.success(ponds)
    }

    suspend fun getInventory(): Result<List<InvItem>> {
        delay(300)
        return Result.success(inventory.toList())
    }

    suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit> {
        delay(100)
        val idx = inventory.indexOfFirst { it.id == id }
        if (idx >= 0) {
            inventory[idx] = inventory[idx].copy(qty = newQty)
        }
        return Result.success(Unit)
    }
}
