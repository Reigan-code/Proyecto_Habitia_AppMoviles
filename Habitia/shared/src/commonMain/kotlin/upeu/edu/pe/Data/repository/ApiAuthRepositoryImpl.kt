package upeu.edu.pe.Data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.LoginRequestDto
import upeu.edu.pe.Data.remote.dto.LoginResponseDto
import upeu.edu.pe.Data.remote.dto.UserCreateDto
import upeu.edu.pe.Data.remote.dto.UserResponseDto
import upeu.edu.pe.Domain.repository.AuthRepository

// ═══════════════════════════════════════════════════════════════════════════
// ApiAuthRepositoryImpl — implementación REAL de AuthRepository contra el
// backend HABITIA-Back (Spring Boot, JWT Bearer).
//
// Flujo de login (2 llamadas encadenadas):
//   1) POST /auth/login {email,password} → {token, tokenType}
//   2) GET /Users/email/{email} (con Bearer) → {id, name,...}
//      → session.onUserResolved(id): deja listo el userId numérico que exigen
//        las rutas /user/{userId} de los demás módulos.
//
// ¿Por qué Result y no excepciones?
//  - La UI necesita MENSAJES legibles ("contraseña incorrecta") y no stack
//    traces de Ktor; Result.failure(Exception(mensaje)) lo resuelve limpio.
// ═══════════════════════════════════════════════════════════════════════════
class ApiAuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : AuthRepository {

    // -----------------------------------------------------------------------
    // login(): autentica + resuelve el userId. Deja la sesión lista para que
    // App.kt dispare la carga de tareas (refresh del repositorio de Tasks).
    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            // 1) LOGIN — el backend responde 200 con {token, tokenType} o
            //    401 si las credenciales no son válidas.
            val response = httpClient.post("${ApiClient.getBaseUrl()}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(email = email, password = password))
            }
            if (!response.status.isSuccess()) {
                // Nota: si falla aquí NO guardamos nada en la sesión.
                return Result.failure(Exception(friendlyAuthError(response.status.value, alRegistrar = false)))
            }
            val loginBody = response.body<LoginResponseDto>()

            // 2) SESIÓN CON TOKEN — ya podemos pedir datos protegidos.
            session.onLogin(token = loginBody.token, email = email)

            // 3) RESOLVER userId — el login no lo devuelve; lo pedimos por email.
            //    El '@' del correo es válido en un path segment (RFC 3986), por
            //    eso lo pasamos directo (igual que el endpoint de ejemplo del
            //    profe para room-types).
            val userResponse = httpClient.get("${ApiClient.getBaseUrl()}/Users/email/$email") {
                bearerAuth(loginBody.token)
            }
            if (!userResponse.status.isSuccess()) {
                // Token válido pero el usuario no se pudo consultar: no hay
                // userId → los módulos de datos no pueden operar. Mejor fallar
                // YA con un mensaje claro que dejar una sesión a medias.
                session.clear()
                return Result.failure(Exception("Cuenta válida, pero no se pudieron leer tus datos (HTTP ${userResponse.status.value})."))
            }
            val user = userResponse.body<UserResponseDto>()
            // Ya NO solo el id: el perfil completo (nombre real de registro y
            // fecha de nacimiento) queda en la sesión para la UI (saludo,
            // perfil). Así el usuario "se guarda" con lo que se registró.
            session.onUserResolved(id = user.id, name = user.name, birthDate = user.birthDate)

            Result.success(Unit)
        } catch (e: Exception) {
            // Fallo de red / backend apagado / timeout → sesión limpia + mensaje.
            session.clear()
            Result.failure(Exception("No se pudo conectar con el servidor. ¿Está corriendo el backend HABITIA-Back?"))
        }
    }

    // -----------------------------------------------------------------------
    // register(): crea la cuenta (POST /auth/register) y, si el registro es
    // aceptado (201), automáticamente hace login para dejar la sesión lista.
    override suspend fun register(name: String, email: String, password: String, birthDate: String): Result<Unit> {
        return try {
            val response = httpClient.post("${ApiClient.getBaseUrl()}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(UserCreateDto(name = name, email = email, password = password, birthDate = birthDate))
            }
            if (!response.status.isSuccess()) {
                return Result.failure(Exception(friendlyAuthError(response.status.value, alRegistrar = true)))
            }
            response.body<UserResponseDto>() // confirma el parseo del usuario creado

            // Login automático tras registrarse (mejor experiencia: un paso menos).
            return login(email, password)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar con el servidor. ¿Está corriendo el backend HABITIA-Back?"))
        }
    }

    // -----------------------------------------------------------------------
    // logout(): borra token/email/userId en memoria. La persistencia
    // (continuar sesión tras cerrar la app) queda como siguiente paso.
    override fun logout() {
        session.clear()
    }

    // -----------------------------------------------------------------------
    // friendlyAuthError(): traduce el HTTP status del backend a un mensaje
    // accionable para la UI (los cuerpos de error de Spring Security no son
    // JSON amigable — prefiero mapear por código).
    private fun friendlyAuthError(status: Int, alRegistrar: Boolean): String = when {
        alRegistrar && status == 409 -> "El correo electrónico ya está registrado."
        !alRegistrar && status == 401 -> "Correo o contraseña incorrectos."
        status == 400 -> (if (alRegistrar) "Datos inválidos: revisa nombre, correo y contraseña (mínimo 8 caracteres)." else "Datos inválidos.")
        status == 404 -> "Servicio no encontrado. Revisa BASE_URL en ApiClient."
        else -> (if (alRegistrar) "No se pudo registrar la cuenta" else "No se pudo iniciar sesión") + " (HTTP $status)."
    }
}
