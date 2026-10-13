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

    private val loginUrl = "http://localhost:5007/api/users/login"
    private val registerUrl = "http://localhost:5007/api/users"

    override suspend fun login(phone: String, pass: String): Result<UserModel> {
        if (phone.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Debe ingresar teléfono y contraseña"))
        }

        return try {
            val response = client.post(loginUrl) {
                contentType(ContentType.Application.Json)
                setBody(LoginDto(phone = phone, password = pass))
            }
            if (response.status.value in 200..299) {
                Result.success(
                    UserModel(
                        name = "Usuario Autenticado",
                        phone = phone,
                        role = "Owner",
                        farmName = "Piscigranja Principal"
                    )
                )
            } else {
                Result.failure(Exception("Credenciales incorrectas o error en el servidor (${response.status.value})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error [${e::class.simpleName}]: ${e.message ?: e.toString()}"))
        }
    }

    override suspend fun register(firstName: String, lastName: String, phone: String, pass: String, role: String): Result<UserModel> {
        if (firstName.isBlank() || lastName.isBlank() || phone.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Todos los campos son obligatorios"))
        }

        return try {
            val response = client.post(registerUrl) {
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
                Result.failure(Exception("Error al registrar usuario (${response.status.value})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error [${e::class.simpleName}]: ${e.message ?: e.toString()}"))
        }
    }
}
