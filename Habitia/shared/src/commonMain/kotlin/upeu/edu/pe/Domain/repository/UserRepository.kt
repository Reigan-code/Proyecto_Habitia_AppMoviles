package upeu.edu.pe.Domain.repository

// ═══════════════════════════════════════════════════════════════════════════
// UserRepository — perfil del usuario contra el backend (PUT /Users/{id}).
// El backend SOLO actualiza name + birthDate (el email es el subject del
// JWT y no es editable). Devuelve true si el PUT fue 2xx.
// ═══════════════════════════════════════════════════════════════════════════
interface UserRepository {
    suspend fun updateProfile(userId: Long, name: String, birthDate: String): Boolean
}
