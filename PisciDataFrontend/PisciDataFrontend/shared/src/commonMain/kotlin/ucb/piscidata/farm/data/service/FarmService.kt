package ucb.piscidata.farm.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.farm.data.datasource.FarmRemoteDataSource
import ucb.piscidata.farm.data.dto.FarmDto
import ucb.piscidata.farm.data.mapper.toModel
import ucb.piscidata.farm.domain.model.Farm

class FarmService : FarmRemoteDataSource {
    private val client = ApiClient.client
    private val farmsUrl = "http://127.0.0.1:5007/api/Farms/my"

    override suspend fun getMyFarms(): Result<List<Farm>> {
        return try {
            val response = client.get(farmsUrl)
            if (response.status.value in 200..299) {
                val farmsDto = response.body<List<FarmDto>>()
                Result.success(farmsDto.map { it.toModel() })
            } else {
                Result.failure(Exception("Error al obtener granjas (${response.status.value})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error [${e::class.simpleName}]: ${e.message ?: e.toString()}"))
        }
    }
}
