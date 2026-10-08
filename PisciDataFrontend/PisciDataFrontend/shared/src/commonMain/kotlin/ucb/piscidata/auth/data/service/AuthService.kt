package ucb.piscidata.auth.data.service

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ucb.piscidata.auth.data.datasource.AuthRemoteDataSource
import ucb.piscidata.auth.data.dto.CreateUserDto
import ucb.piscidata.auth.data.dto.LoginDto
import ucb.piscidata.auth.domain.model.UserModel
import ucb.piscidata.data.remote.ApiClient

class AuthService : AuthRemoteDataSource {
    private val client = ApiClient.client
    
    private val loginUrls = listOf(
        "https://10.0.2.2:7190/api/users/login",
        "http://10.0.2.2:5007/api/users/login"
    )

    private val registerUrls = listOf(
        "https://10.0.2.2:7190/api/users",
        "http://10.0.2.2:5007/api/users"
    )

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
                    return Result.success(
                        UserModel(
                            name = "Usuario Autenticado",
                            phone = phone,
                            role = "Owner",
                            farmName = "Piscigranja Principal"
                        )
                    )
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        return Result.failure(Exception("No se pudo conectar con el servidor (HTTPS/HTTP). Verifique que el backend esté encendido (${lastException?.message})"))
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
                    return Result.success(
                        UserModel(
                            name = "$firstName $lastName",
                            phone = phone,
                            role = role,
                            farmName = "Piscigranja Principal"
                        )
                    )
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        return Result.failure(Exception("No se pudo registrar en el servidor (HTTPS/HTTP) (${lastException?.message})"))
    }
}
