package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════════
// DTOs de REMINDERS — espejo de los RECORDS de Java del backend.
// reminderDate es LocalDateTime → ISO "yyyy-MM-ddTHH:mm:ss"
// (el dominio lo parte en date + time; el mapper los vuelve a unir).
// repeat: "NONE" | "DAILY" | "WEEKLY" (RepeatType del backend).
// ═══════════════════════════════════════════════════════════════════════════

// → POST /Reminders. OJO: este DTO SÍ incluye userId (a diferencia de
//   Tasks/Habits donde el backend lo resuelve del JWT) — el repositorio lo
//   toma de SessionManager para no hardcodear nada.
@Serializable
data class ReminderCreateDto(
    val userId: Long,
    val title: String,
    val description: String,
    val reminderDate: String,    // ISO "yyyy-MM-ddTHH:mm:ss"
    val repeat: String
)

// ← GET /Reminders/user/{userId}, /user/{userId}/completed/{completed}, POST
@Serializable
data class ReminderResponseDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String,
    val reminderDate: String,    // ISO "yyyy-MM-ddTHH:mm:ss"
    val repeat: String,
    val completed: Boolean
)

// → PUT /Reminders/user/{userId}/{reminderId} (actualización completa)
@Serializable
data class ReminderUpdateDto(
    val title: String,
    val description: String,
    val reminderDate: String,
    val repeat: String,
    val completed: Boolean
)
