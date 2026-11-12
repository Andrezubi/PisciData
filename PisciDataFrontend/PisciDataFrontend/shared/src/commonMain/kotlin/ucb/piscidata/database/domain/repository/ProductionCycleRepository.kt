package ucb.piscidata.database.domain.repository

import ucb.piscidata.database.domain.model.CicloRecord

interface ProductionCycleRepository {
    suspend fun getCiclos(farmId: Int): Result<List<CicloRecord>>
    suspend fun saveCiclo(ciclo: CicloRecord): Result<Unit>
    suspend fun deleteCiclo(id: String): Result<Unit>
    suspend fun toggleCicloExpand(id: String): Result<List<CicloRecord>>
}
