package ucb.piscidata.database.domain.usecase

import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.repository.ProductionCycleRepository

class GetCiclosUseCase(private val repository: ProductionCycleRepository) {
    suspend operator fun invoke(farmId: Int): Result<List<CicloRecord>> = repository.getCiclos(farmId)
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
