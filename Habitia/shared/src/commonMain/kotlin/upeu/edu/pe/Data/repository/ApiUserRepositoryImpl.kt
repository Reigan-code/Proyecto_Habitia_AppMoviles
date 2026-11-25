package upeu.edu.pe.Data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.UserUpdateRequestDto
import upeu.edu.pe.Domain.repository.UserRepository

// ═══════════════════════════════════════════════════════════════════════════
// ApiUserRepositoryImpl — edición de perfil REAL contra PUT /Users/{id}.
// Tras un PUT exitoso la llamada responde el UserResponseDto actualizado;
// el ViewModel es quien replica name/birthDate en SessionManager (única
// fuente de verdad de la sesión).
// ═══════════════════════════════════════════════════════════════════════════
class ApiUserRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : UserRepository {

    override suspend fun updateProfile(userId: Long, name: String, birthDate: String): Boolean {
        val token = session.state.value.token ?: return false
        return try {
            val response = httpClient.put("${ApiClient.getBaseUrl()}/Users/$userId") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(UserUpdateRequestDto(name = name, birthDate = birthDate))
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] updateProfile: HTTP ${response.status.value}")
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            println("[Habitia-API] updateProfile FALLÓ: ${e.message}")
            false
        }
    }
}
