package ucb.piscidata.database.domain.repository

import ucb.piscidata.database.domain.model.CicloRecord
import ucb.piscidata.database.domain.model.FarmModel
import ucb.piscidata.database.domain.model.InvItem
import ucb.piscidata.database.domain.model.Pond

interface DatabaseRepository {
    suspend fun getFarms(): Result<List<FarmModel>>
    suspend fun getCiclos(): Result<List<CicloRecord>>
    suspend fun saveCiclo(ciclo: CicloRecord): Result<Unit>
    suspend fun deleteCiclo(id: String): Result<Unit>
    suspend fun toggleCicloExpand(id: String): Result<List<CicloRecord>>
    suspend fun getPonds(): Result<List<Pond>>
    suspend fun getInventory(): Result<List<InvItem>>
    suspend fun updateInventoryQty(id: Int, newQty: Int): Result<Unit>
}
