package upeu.edu.pe.Data.remote.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════════
// SessionManager — memoria de sesión del usuario autenticado.
//
// ¿Por qué existe?
//  - El backend HABITIA-Back protege TODO con JWT Bearer (SecurityConfig:
//    solo /auth/** es público). Tras el login, cada petición necesita el
//    token → debe vivir en un lugar accesible por todos los repositorios.
//  - Además, la mayoría de endpoints del backend filtran por userId en la
//    ruta (ej. GET /Tasks/user/{userId}), y el login NO devuelve ese id:
//    solo {token, tokenType}. Por eso la sesión guarda también el email y
//    el userId que se resuelve justo después del login con
//    GET /Users/email/{email} → {id}.
//
// ¿Por qué StateFlow y no variables sueltas?
//  - state es reactivo: App.kt puede observar la sesión (collectAsState) y
//    disparar la carga de tareas cuando token+userId están listos, sin
//    callbacks entre capas.
//
// Nota de alcance (primer corte Auth+Tasks): la sesión se guarda en RAM,
// i.e. al cerrar la app hay que volver a loguear. La persistencia con
// DataStore (ya presente en el proyecto) queda documentada como siguiente
// paso en CAMBIOS.md.
// ═══════════════════════════════════════════════════════════════════════════
class SessionManager(
    private val storage: SessionStorage? = null,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {

    // SessionData: valores de la sesión. null = no autenticado todavía.
    data class SessionData(
        val token: String? = null,   // JWT a enviar como "Authorization: Bearer <token>"
        val email: String? = null,   // subject del JWT; clave para GET /Users/email/{email}
        val userId: Long? = null,    // id numérico (_USERS.ID_) que exigen las rutas /user/{userId}
        // Datos REALES del usuario (vienen de GET /Users/email en el login):
        val name: String? = null,      // nombre de registro → saludo en Home y perfil
        val birthDate: String? = null  // AAAA-MM-DD tal como lo guarda el backend
    )

    // _state: valor real, mutable solo dentro de esta clase.
    private val _state = MutableStateFlow(SessionData())

    // state: lectura pública inmutable para UI y repositorios.
    val state: StateFlow<SessionData> = _state.asStateFlow()

    // onLogin(token, email): guarda credenciales del token recibido en
    // POST /auth/login. Se llama ANTES de resolver el userId.
    private fun persist() {
        val snapshot = _state.value
        scope.launch { storage?.save(snapshot) }
    }

    fun onLogin(token: String, email: String) {
        _state.value = SessionData(token = token, email = email)
        persist()
    }

    // onUserResolved(id, name, birthDate): registra el perfil COMPLETO obtenido
    // de GET /Users/email/{email} tras el login. A partir de aquí los
    // repositorios pueden llamar rutas /user/{userId} Y la UI puede mostrar
    // el nombre real del registro (saludo de Home, perfil).
    fun onUserResolved(id: Long, name: String, birthDate: String?) {
        _state.value = _state.value.copy(userId = id, name = name, birthDate = birthDate)
        persist()
    }

    // updateProfile(): replica en la sesión los cambios guardados por
    // PUT /Users/{id} desde el perfil — al distribuirse el StateFlow, el
    // saludo de Home y el VM del perfil se re-crean con los datos nuevos.
    fun updateProfile(name: String, birthDate: String) {
        _state.value = _state.value.copy(name = name, birthDate = birthDate)
        persist()
    }

    // clear(): cierra sesión (logout o credenciales inválidas) y borra todo.
    fun clear() {
        _state.value = SessionData()
        scope.launch { storage?.clear() }
    }

    // isReady(): conveniencia — true cuando hay token Y userId (condición
    // para poder llamar a los endpoints de datos del backend).
    fun isReady(): Boolean = _state.value.token != null && _state.value.userId != null

    suspend fun restore() {
        val loaded = storage?.load() ?: return
        if (loaded.token != null) _state.value = loaded
    }
}
