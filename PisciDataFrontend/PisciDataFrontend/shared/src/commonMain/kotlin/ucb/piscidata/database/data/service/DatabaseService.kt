package ucb.piscidata.database.data.service

import kotlinx.coroutines.delay
import ucb.piscidata.database.domain.model.*

class DatabaseService {
    private val farms = listOf(
        FarmModel(1, "Piscigranja El Manantial"),
        FarmModel(2, "Piscigranja Norte - San Juan")
    )

    private val ciclos = mutableListOf(
        CicloRecord(
            id = "CP-2026-018",
            status = BadgeVariant.ACTIVO,
            estanque = "Estanque 3",
            especie = "Tilapia nilótica",
            peces = "15.400",
            pesoActual = "145.2 g",
            edadDias = 56,
            startDate = "2026-08-12",
            endDate = null,
            initialAgeDays = 15,
            initialWeightGrams = "12 g",
            initialFishCount = "16.000",
            observations = "Crecimiento óptimo, consumo de alimento estable.",
            isExpanded = false
        ),
        CicloRecord(
            id = "CP-2026-017",
            status = BadgeVariant.ACTIVO,
            estanque = "Estanque 1",
            especie = "Trucha arcoíris",
            peces = "8.250",
            pesoActual = "210.5 g",
            edadDias = 70,
            startDate = "2026-07-28",
            endDate = null,
            initialAgeDays = 20,
            initialWeightGrams = "10 g",
            initialFishCount = "8.500",
            observations = "Monitoreo riguroso de oxígeno disuelto.",
            isExpanded = false
        ),
        CicloRecord(
            id = "CP-2026-014",
            status = BadgeVariant.EN_SEGUIMIENTO,
            estanque = "Estanque 5",
            especie = "Cachama blanca",
            peces = "12.000",
            pesoActual = "98.4 g",
            edadDias = 122,
            startDate = "2026-06-05",
            endDate = null,
            initialAgeDays = 10,
            initialWeightGrams = "8 g",
            initialFishCount = "12.500",
            observations = "Monitoreo de temperatura semanal.",
            isExpanded = false
        )
    )

    private val ponds = listOf(
        Pond(1, "EST-01", "Estanque 1", PondShape.RECTANGULAR, largo = 40.0, ancho = 20.0, area = 800.0, profundidad = 1.8, transparenciaCm = 45.0, ph = 7.2, claridad = "Alta", temperatura = 24.0, oxigeno = 6.8, cicloActivo = "CP-2026-017", especie = "Trucha arcoíris"),
        Pond(2, "EST-02", "Estanque 2", PondShape.RECTANGULAR, largo = 35.0, ancho = 18.0, area = 630.0, profundidad = 1.6, transparenciaCm = 50.0, ph = 7.0, claridad = "Media", temperatura = 25.0, oxigeno = 6.2, cicloActivo = null, especie = null),
        Pond(3, "EST-03", "Estanque 3", PondShape.CIRCULAR, diametro = 30.0, area = 707.0, profundidad = 2.0, transparenciaCm = 40.0, ph = 7.4, claridad = "Alta", temperatura = 26.0, oxigeno = 7.1, cicloActivo = "CP-2026-018", especie = "Tilapia nilótica"),
        Pond(4, "EST-04", "Estanque 4", PondShape.IRREGULAR, area = 550.0, profundidad = 1.5, transparenciaCm = 30.0, ph = 6.8, claridad = "Baja", temperatura = 27.0, oxigeno = 5.4, cicloActivo = null, especie = null),
        Pond(5, "EST-05", "Estanque 5", PondShape.RECTANGULAR, largo = 45.0, ancho = 22.0, area = 990.0, profundidad = 1.9, transparenciaCm = 48.0, ph = 7.1, claridad = "Media", temperatura = 25.0, oxigeno = 6.5, cicloActivo = "CP-2026-014", especie = "Cachama blanca")
    )

    private val inventory = mutableListOf(
        InvItem(1, "Alimento F1", InvCategory.ALIMENTO, 320, "kg", 100, proteinPercentage = 45.0, pelletSizeMm = 2.0),
        InvItem(2, "Alimento F2", InvCategory.ALIMENTO, 200, "kg", 80, proteinPercentage = 40.0, pelletSizeMm = 4.0),
        InvItem(3, "Alimento F3", InvCategory.ALIMENTO, 55, "kg", 60, proteinPercentage = 35.0, pelletSizeMm = 6.0),
        InvItem(4, "Cal agrícola", InvCategory.QUIMICO, 100, "kg", 50),
        InvItem(5, "Permanganato KMnO₄", InvCategory.QUIMICO, 8, "kg", 5),
        InvItem(6, "Gasolina", InvCategory.COMBUSTIBLE, 50, "L", 20),
        InvItem(7, "Aceite motor", InvCategory.COMBUSTIBLE, 12, "L", 5),
        InvItem(8, "Red de pesca", InvCategory.EQUIPO, 3, "und", 2)
    )

    suspend fun getFarms(): Result<List<FarmModel>> = Result.success(farms)

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

    suspend fun toggleCicloExpand(id: String): Result<List<CicloRecord>> {
        delay(100)
        val idx = ciclos.indexOfFirst { it.id == id }
        if (idx >= 0) {
            ciclos[idx] = ciclos[idx].copy(isExpanded = !ciclos[idx].isExpanded)
        }
        return Result.success(ciclos.toList())
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
