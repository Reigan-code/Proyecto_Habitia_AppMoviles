package upeu.edu.pe.Data.remote.auth

// Contrato de persistencia para la sesión. Implementaciones por plataforma:
//   - Android: DataStoreSessionStorage (androidMain) — sobrevive al cierre.
//   - iOS: NoopSessionStorage (iosMain) — en memoria (sin cambios vs antes).
interface SessionStorage {
    suspend fun load(): SessionManager.SessionData?
    suspend fun save(data: SessionManager.SessionData)
    suspend fun clear()
}
