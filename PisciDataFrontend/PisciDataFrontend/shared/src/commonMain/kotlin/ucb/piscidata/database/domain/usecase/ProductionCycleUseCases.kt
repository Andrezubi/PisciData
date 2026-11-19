package ucb.piscidata.database.domain.usecase

import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.SpeciesModel
import ucb.piscidata.database.domain.repository.ProductionCycleRepository

class GetCiclosUseCase(private val repository: ProductionCycleRepository) {
    suspend operator fun invoke(farmId: Int): Result<List<CicloRecord>> = repository.getCiclos(farmId)
}

class CreateProductionCycleUseCase(private val repository: ProductionCycleRepository) {
    suspend operator fun invoke(
        pondId: Int, speciesId: Int, startDate: String,
        initialFishCount: Int?, initialAverageWeightGrams: Double?, initialAgeDays: Int?, observations: String?
    ): Result<Unit> = repository.createProductionCycle(pondId, speciesId, startDate, initialFishCount, initialAverageWeightGrams, initialAgeDays, observations)
}

class FinalizeProductionCycleUseCase(private val repository: ProductionCycleRepository) {
    suspend operator fun invoke(cycleId: Int, pondId: Int, speciesId: Int, startDate: String): Result<Unit> =
        repository.finalizeProductionCycle(cycleId, pondId, speciesId, startDate)
}

class SaveCicloUseCase(private val repository: ProductionCycleRepository) {
    suspend operator fun invoke(ciclo: CicloRecord): Result<Unit> = repository.saveCiclo(ciclo)
}

class DeleteCicloUseCase(private val repository: ProductionCycleRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteCiclo(id)
}

class ToggleCicloExpandUseCase(private val repository: ProductionCycleRepository) {
    suspend operator fun invoke(id: String): Result<List<CicloRecord>> = repository.toggleCicloExpand(id)
}

class GetSpeciesUseCase(private val repository: ProductionCycleRepository) {
    suspend operator fun invoke(): Result<List<SpeciesModel>> = repository.getSpecies()
}
