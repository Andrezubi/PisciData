package ucb.piscidata.auth.data.service

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.mp.KoinPlatform
import ucb.piscidata.auth.data.datasource.AuthRemoteDataSource
import ucb.piscidata.auth.data.dto.CreateUserDto
import ucb.piscidata.auth.data.dto.LoginDto
import ucb.piscidata.auth.data.dto.LoginResponseDto
import ucb.piscidata.auth.data.dto.UserResponseDto
import ucb.piscidata.auth.data.mapper.toModel
import ucb.piscidata.auth.domain.model.UserModel
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.data.remote.RemoteBackendConfig
import ucb.piscidata.session.SessionManager

class AuthService : AuthRemoteDataSource {
    private val client = ApiClient.client
    private val sessionManager: SessionManager by lazy { KoinPlatform.getKoin().get() }

    private val loginUrls: List<String>
        get() = RemoteBackendConfig.urlsFor("/api/users/login")

    private val registerUrls: List<String>
        get() = RemoteBackendConfig.urlsFor("/api/users")

    override suspend fun login(phone: String, pass: String): Result<UserModel> {
        if (phone.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Debe ingresar teléfono y contraseña"))
        }

        var lastException: Exception? = null
        for (url in loginUrls) {
            try {
                val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(LoginDto(phone = phone, password = pass))
                }
                if (response.status.value in 200..299) {
                    val loginResp: LoginResponseDto = response.body()
                    sessionManager.setToken(loginResp.token)
                    ApiClient.sessionManager = sessionManager

                    return Result.success(
                        UserModel(
                            id = 0,
                            name = "Usuario Autenticado",
                            phone = phone,
                            role = "Owner",
                            farmName = "Piscigranja Principal"
                        )
                    )
                }
                if (response.status.value == 401) {
                    return Result.failure(Exception("El número de teléfono o la contraseña no coinciden."))
                }
                if (response.status.value in 400..499) {
                    return Result.failure(Exception("Revisa el formato de tu número y contraseña."))
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        return Result.failure(Exception("No pudimos conectar con el servidor. Revisa tu conexión a internet e inténtalo de nuevo."))
    }

    override suspend fun register(firstName: String, lastName: String, phone: String, pass: String, role: String): Result<UserModel> {
        if (firstName.isBlank() || lastName.isBlank() || phone.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Todos los campos son obligatorios"))
        }

        var lastException: Exception? = null
        for (url in registerUrls) {
            try {
                val response = client.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(CreateUserDto(firstName = firstName, lastName = lastName, phone = phone, password = pass, role = role))
                }
                if (response.status.value in 200..299) {
                    val userDto: UserResponseDto = response.body()
                    return Result.success(userDto.toModel())
                }
                if (response.status.value in 400..499) {
                    return Result.failure(Exception("No se pudo registrar: verifica que el teléfono no exista y la contraseña tenga al menos 6 caracteres."))
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        return Result.failure(Exception("No pudimos conectar con el servidor para crear tu cuenta. Revisa tu conexión a internet."))
    }
}
