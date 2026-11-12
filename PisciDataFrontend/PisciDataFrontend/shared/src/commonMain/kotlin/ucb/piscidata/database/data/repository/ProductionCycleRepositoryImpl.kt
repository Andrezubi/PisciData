package ucb.piscidata.database.data.repository

import ucb.piscidata.database.data.service.ProductionCycleService
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.SpeciesModel
import ucb.piscidata.database.domain.repository.ProductionCycleRepository

class ProductionCycleRepositoryImpl(
    private val service: ProductionCycleService
) : ProductionCycleRepository {
    override suspend fun getCiclos(farmId: Int): Result<List<CicloRecord>> = service.getCiclos(farmId)
    override suspend fun createProductionCycle(
        pondId: Int, speciesId: Int, startDate: String,
        initialFishCount: Int?, initialAverageWeightGrams: Double?, initialAgeDays: Int?, observations: String?
    ): Result<Unit> = service.createProductionCycle(pondId, speciesId, startDate, initialFishCount, initialAverageWeightGrams, initialAgeDays, observations)

    override suspend fun finalizeProductionCycle(cycleId: Int, pondId: Int, speciesId: Int, startDate: String): Result<Unit> =
        service.finalizeProductionCycle(cycleId, pondId, speciesId, startDate)

    override suspend fun saveCiclo(ciclo: CicloRecord): Result<Unit> = service.saveCiclo(ciclo)
    override suspend fun deleteCiclo(id: String): Result<Unit> = service.deleteCiclo(id)
    override suspend fun toggleCicloExpand(id: String): Result<List<CicloRecord>> = service.toggleCicloExpand(id)
    override suspend fun getSpecies(): Result<List<SpeciesModel>> = service.getSpecies()
}
