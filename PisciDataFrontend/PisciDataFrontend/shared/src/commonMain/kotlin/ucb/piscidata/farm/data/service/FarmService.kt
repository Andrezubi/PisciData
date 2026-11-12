package ucb.piscidata.farm.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.farm.data.datasource.FarmRemoteDataSource
import ucb.piscidata.farm.data.dto.CreateFarmRequestDto
import ucb.piscidata.farm.data.dto.FarmDto
import ucb.piscidata.farm.data.mapper.toModel
import ucb.piscidata.farm.domain.model.Farm
import ucb.piscidata.session.SessionManager

class FarmService : FarmRemoteDataSource {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private val myFarmsUrls = listOf(
        "https://127.0.0.1:7190/api/farms/my",
        "http://127.0.0.1:5007/api/farms/my"
    )

    private val createFarmUrls = listOf(
        "https://127.0.0.1:7190/api/farms",
        "http://127.0.0.1:5007/api/farms"
    )

    override suspend fun getMyFarms(): Result<List<Farm>> {
        val token = sessionManager.token.value
        var lastException: Exception? = null
        for (url in myFarmsUrls) {
            try {
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                if (response.status.value in 200..299) {
                    val farmsDto: List<FarmDto> = response.body()
                    return Result.success(farmsDto.map { it.toModel() })
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        return Result.failure(Exception("Error al obtener granjas (${lastException?.message})"))
    }

    override suspend fun createFarm(
        ownerUserId: Int,
        name: String,
        description: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?
    ): Result<Farm> {
        val token = sessionManager.token.value
        val requestDto = CreateFarmRequestDto(
            ownerUserId = ownerUserId,
            name = name,
            description = description,
            address = address,
            latitude = latitude,
            longitude = longitude
        )
        var lastException: Exception? = null
        for (url in createFarmUrls) {
            try {
                val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                    setBody(requestDto)
                }
                if (response.status.value in 200..299) {
                    val createdDto: FarmDto = response.body()
                    return Result.success(createdDto.toModel())
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        return Result.failure(Exception("Error al registrar granja en el backend (${lastException?.message})"))
    }
}
