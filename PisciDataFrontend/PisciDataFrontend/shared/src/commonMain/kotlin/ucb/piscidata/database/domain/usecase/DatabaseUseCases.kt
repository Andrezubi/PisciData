package ucb.piscidata.database.domain.usecase

import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.FarmModel
import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.database.domain.model.Pond
import ucb.piscidata.database.domain.repository.DatabaseRepository

class GetFarmsUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(): Result<List<FarmModel>> = repository.getFarms()
}

class GetCiclosUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(): Result<List<CicloRecord>> = repository.getCiclos()
}

class SaveCicloUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(ciclo: CicloRecord): Result<Unit> = repository.saveCiclo(ciclo)
}

class DeleteCicloUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteCiclo(id)
}

class ToggleCicloExpandUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(id: String): Result<List<CicloRecord>> = repository.toggleCicloExpand(id)
}

class GetPondsUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(): Result<List<Pond>> = repository.getPonds()
}

class GetInventoryUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(): Result<List<InvItem>> = repository.getInventory()
}

class UpdateInventoryQtyUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke(id: Int, newQty: Int): Result<Unit> = repository.updateInventoryQty(id, newQty)
}
