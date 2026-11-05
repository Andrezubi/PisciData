package ucb.piscidata.profile.data.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.mp.KoinPlatform
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.profile.data.datasource.ProfileRemoteDataSource
import ucb.piscidata.profile.data.dto.UpdateUserRequestDto
import ucb.piscidata.profile.data.dto.UserProfileDto
import ucb.piscidata.profile.data.mapper.toModel
import ucb.piscidata.profile.domain.model.UserProfileModel
import ucb.piscidata.session.SessionManager

class ProfileService : ProfileRemoteDataSource {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private val userUrls = listOf(
        "https://127.0.0.1:7190/api/users",
        "http://127.0.0.1:5007/api/users"
    )

    override suspend fun getUserProfile(id: Int): Result<UserProfileModel> {
        val token = sessionManager.token.value
        for (baseUrl in userUrls) {
            try {
                val url = "$baseUrl/$id"
                val response = client.get(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                }
                if (response.status.value in 200..299) {
                    val dto: UserProfileDto = response.body()
                    return Result.success(dto.toModel())
                }
            } catch (_: Exception) {}
        }
        // Fallback mock profile if offline
        return Result.success(
            UserProfileModel(
                id = id,
                firstName = "Carlos",
                lastName = "Mendoza",
                phone = "76424237",
                role = "Admin",
                createdAt = "2026-01-10"
            )
        )
    }

    override suspend fun updateUserProfile(id: Int, firstName: String, lastName: String, phone: String, role: String): Result<UserProfileModel> {
        val token = sessionManager.token.value
        for (baseUrl in userUrls) {
            try {
                val url = "$baseUrl/$id"
                val response = client.put(url) {
                    if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(UpdateUserRequestDto(firstName, lastName, phone, role))
                }
                if (response.status.value in 200..299) {
                    val dto: UserProfileDto = response.body()
                    return Result.success(dto.toModel())
                }
            } catch (_: Exception) {}
        }
        return Result.success(
            UserProfileModel(
                id = id,
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                role = role,
                createdAt = "2026-01-10"
            )
        )
    }
}
