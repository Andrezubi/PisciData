package ucb.piscidata.auth.data.service

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ucb.piscidata.auth.data.datasource.AuthRemoteDataSource
import ucb.piscidata.auth.data.dto.CreateUserDto
import ucb.piscidata.auth.data.dto.LoginDto
import ucb.piscidata.auth.domain.model.UserModel
import ucb.piscidata.data.remote.ApiClient
import ucb.piscidata.session.SessionManager
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AuthService(
    private val sessionManager: SessionManager
) : AuthRemoteDataSource {
    init {
        ApiClient.sessionManager = sessionManager
    }

    private val client = ApiClient.client

    private val loginUrl = "http://127.0.0.1:5007/api/users/login"
    private val registerUrl = "http://127.0.0.1:5007/api/users"

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeUserIdFromToken(token: String): Int? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = parts[1]
            val normalized = payload.replace('-', '+').replace('_', '/')
            val padded = when (normalized.length % 4) {
                2 -> normalized + "=="
                3 -> normalized + "="
                else -> normalized
            }
            val decoded = Base64.decode(padded).decodeToString()
            val json = Json.parseToJsonElement(decoded).jsonObject
            val userId = json["UserId"]?.jsonPrimitive?.content
                ?: json["userId"]?.jsonPrimitive?.content
                ?: json["nameid"]?.jsonPrimitive?.content
                ?: json["http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier"]?.jsonPrimitive?.content
                ?: json["sub"]?.jsonPrimitive?.content
            userId?.toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

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
                val body = response.body<Map<String, String>>()
                val token = body["token"] ?: body["Token"]
                if (token != null) {
                    sessionManager.setToken(token)
                    ApiClient.sessionManager = sessionManager
                    val userId = decodeUserIdFromToken(token)
                    if (userId != null) {
                        sessionManager.setUserId(userId)
                    }
                }
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
