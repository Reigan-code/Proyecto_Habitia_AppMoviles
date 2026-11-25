package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════════
// DTOs de USERS — espejo de UserUpdateDto / UserResponseDto del backend.
// OJO: el backend SOLO permite actualizar name + birthDate por PUT /Users/{id}
// (el email es el subject del JWT y NO es editable; el password tiene su
// propio flujo). El UserResponseDto del backend también incluye el HASH del
// password — lo ignoramos con ignoreUnknownKeys y NO lo tocamos.
// ═══════════════════════════════════════════════════════════════════════════

// → PUT /Users/{id} (UserUpdateDto del backend: name ≤100, birthDate
//   LocalDate @PastOrPresent — validar en el cliente antes de enviar).
@Serializable
data class UserUpdateRequestDto(
    val name: String,
    val birthDate: String,      // ISO "yyyy-MM-dd"
    val phone: String? = null
)

// ← GET /Users/{id}, GET /Users/email/{email}, PUT /Users/{id}
// (YA EXISTE en AuthDto.kt: se reutiliza; solo declaramos los campos que el
// front usa y el resto —password hash— lo ignora el Json de ApiClient).
