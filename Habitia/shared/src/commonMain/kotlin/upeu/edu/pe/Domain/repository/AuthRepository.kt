package upeu.edu.pe.Domain.repository

// ═══════════════════════════════════════════════════════════════════════════
// AuthRepository — contrato de dominio para autenticación.
//
// ¿Por qué una interfaz en Domain?
//  - Clean Architecture: el dominio DEFINE qué debe poder hacerse (login,
//    register, logout) sin saber CÓMO (API HTTP hoy, mocks para tests mañana).
//  - Las implementaciones viven en Data (Data/repository/ApiAuthRepositoryImpl
//    con Ktor); App.kt inyecta la implementación al ViewModel.
// ═══════════════════════════════════════════════════════════════════════════
interface AuthRepository {

    // login(): valida credenciales contra POST /auth/login del backend.
    // Si funciona, la sesión queda lista (token + userId) en SessionManager.
    // Result: success = credenciales válidas; failure = mensaje para la UI.
    suspend fun login(email: String, password: String): Result<Unit>

    // register(): crea la cuenta en POST /auth/register y, si el backend la
    // acepta, hace login inmediato (mismo flujo que pedidos: registrar → use).
    suspend fun register(name: String, email: String, password: String, birthDate: String): Result<Unit>

    // logout(): borra la sesión en memoria (SessionManager.clear).
    fun logout()
}
