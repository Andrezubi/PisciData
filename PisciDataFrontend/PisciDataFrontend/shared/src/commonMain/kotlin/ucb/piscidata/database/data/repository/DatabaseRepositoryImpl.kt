package ucb.piscidata.database.data.repository

import ucb.piscidata.database.data.service.DatabaseService
import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.database.domain.model.Pond
import ucb.piscidata.database.domain.repository.DatabaseRepository

class DatabaseRepositoryImpl(
    private val service: DatabaseService
) : DatabaseRepository {
    override suspend fun getCiclos(): Result<List<CicloRecord>> = service.getCiclos()
    override suspend fun saveCiclo(ciclo: CicloRecord): Result<Unit> = service.saveCiclo(ciclo)
    override suspend fun deleteCiclo(id: String): Result<Unit> = service.deleteCiclo(id)
    override suspend fun getPonds(): Result<List<Pond>> = service.getPonds()
    override suspend fun getInventory(): Result<List<InvItem>> = service.getInventory()
    override suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit> = service.updateInventoryQty(id, newQty)
}
