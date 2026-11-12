package ucb.piscidata.database.domain.repository

import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.SpeciesModel

interface ProductionCycleRepository {
    suspend fun getCiclos(farmId: Int): Result<List<CicloRecord>>
    suspend fun createProductionCycle(pondId: Int, speciesId: Int, startDate: String, initialFishCount: Int?, initialAverageWeightGrams: Double?, initialAgeDays: Int?, observations: String?): Result<Unit>
    suspend fun finalizeProductionCycle(cycleId: Int, pondId: Int, speciesId: Int, startDate: String): Result<Unit>
    suspend fun saveCiclo(ciclo: CicloRecord): Result<Unit>
    suspend fun deleteCiclo(id: String): Result<Unit>
    suspend fun toggleCicloExpand(id: String): Result<List<CicloRecord>>
    suspend fun getSpecies(): Result<List<SpeciesModel>>
}
