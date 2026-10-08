package ucb.piscidata.auth.data.service

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ucb.piscidata.auth.data.datasource.AuthRemoteDataSource
import ucb.piscidata.auth.data.dto.CreateUserDto
import ucb.piscidata.auth.data.dto.LoginDto
import ucb.piscidata.auth.data.dto.LoginResponseDto
import ucb.piscidata.auth.domain.model.UserModel
import ucb.piscidata.data.remote.ApiClient

class AuthService : AuthRemoteDataSource {
    private val client = ApiClient.client
    private val baseUrl = "http://10.0.2.2:5007/api/users"

    override suspend fun login(phone: String, pass: String): Result<UserModel> {
        return try {
            if (phone.isBlank() || pass.isBlank()) {
                return Result.failure(Exception("Debe ingresar teléfono y contraseña"))
            }
            val response = client.post("$baseUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginDto(phone = phone, password = pass))
            }
            if (response.status.value in 200..299) {
                // Parse JWT token response or success
                Result.success(
                    UserModel(
                        name = "Usuario Autenticado",
                        phone = phone,
                        role = "Owner",
                        farmName = "Piscigranja Principal"
                    )
                )
            } else {
                Result.failure(Exception("Número de teléfono o contraseña incorrectos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar con el servidor backend (${e.message})"))
        }
    }

    override suspend fun register(firstName: String, lastName: String, phone: String, pass: String, role: String): Result<UserModel> {
        return try {
            if (firstName.isBlank() || lastName.isBlank() || phone.isBlank() || pass.isBlank()) {
                return Result.failure(Exception("Todos los campos son obligatorios"))
            }
            val response = client.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(CreateUserDto(firstName = firstName, lastName = lastName, phone = phone, password = pass, role = role))
            }
            if (response.status.value in 200..299) {
                Result.success(
                    UserModel(
                        name = "$firstName $lastName",
                        phone = phone,
                        role = role,
                        farmName = "Piscigranja Principal"
                    )
                )
            } else {
                Result.failure(Exception("Error al registrar usuario en el servidor"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar con el servidor backend (${e.message})"))
        }
    }
}
