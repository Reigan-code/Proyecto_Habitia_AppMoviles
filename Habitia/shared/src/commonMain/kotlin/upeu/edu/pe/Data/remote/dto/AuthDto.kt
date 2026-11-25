package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════════
// DTOs de AUTENTICACIÓN — espejo 1:1 de los DTOs del backend HABITIA-Back
// (AuthController/dtoLOGIN + DTOs/UserCreateDto + DTOs/UserResponseDto).
//
// Regla del mapeo: los nombres de propiedades DEBEN coincidir con los campos
// JSON que Spring genera (Jackson usa los getters; por convención camelCase
// idéntico). birthDate se envía/recibe como ISO "yyyy-MM-dd" (LocalDate de
// Jackson con JavaTimeModule: escribe String ISO por defecto).
// ═══════════════════════════════════════════════════════════════════════════

// LoginRequestDto → POST /auth/login. Campos validados por el backend
// (@NotBlank email + password).
@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

// LoginResponseDto ← respuesta de POST /auth/login. token es el JWT que hay
// que enviar en "Authorization: Bearer <token>" (tokenType == "Bearer").
@Serializable
data class LoginResponseDto(
    val token: String,
    val tokenType: String
)

// UserCreateDto → POST /auth/register. El backend exige (@NotBlank/@NotNull):
// name (≤100), email (formato válido, ≤254), password (8..72) y birthDate.
// Se validan también desde la UI para fallar rápido sin gastar una llamada.
@Serializable
data class UserCreateDto(
    val name: String,
    val email: String,
    val password: String,
    val birthDate: String
)

// UserResponseDto ← respuesta de POST /auth/register y de GET /Users/{id} y
// de GET /Users/email/{email}. id es el userId (Long) que guardará el
// SessionManager para llamar a las rutas /user/{userId} de los demás módulos.
@Serializable
data class UserResponseDto(
    val id: Long,
    val name: String,
    val email: String,
    val birthDate: String?, // opcional por tolerancia, aunque el backend siempre lo envía
    val phone: String? = null
)
