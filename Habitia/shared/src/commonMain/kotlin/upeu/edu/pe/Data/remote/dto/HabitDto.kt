package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════════
// DTOs de HABITS + HABIT_COMPLETIONS — espejo del backend HABITIA-Back.
// HabitRequestDTO/HabitResponseDTO (DTOs/) y HabitCompletionRequestDTO/
// HabitCompletionResponseDTO.
// ═══════════════════════════════════════════════════════════════════════════

// → POST /Habits (creación de un hábito). El backend NO recibe userId aquí:
//   como Tasks, el dueño se resuelve desde el JWT (patrón del profe).
@Serializable
data class HabitCreateDto(
    val name: String,
    val description: String,
    val frequency: String,       // "DAILY" | "WEEKLY" | "MONTHLY"
    val startDate: String,       // ISO "yyyy-MM-dd"
    val endDate: String?,        // opcional en el backend
    val active: Boolean = true
)

// ← GET /Habits/user/{userId}, POST, PUT /Habits/{id}
@Serializable
data class HabitResponseDto(
    val id: Long,
    val userId: Long,
    val name: String,
    val description: String,
    val frequency: String,
    val startDate: String,
    val endDate: String?,
    val active: Boolean
)

// → POST /HabitCompletions y PUT /HabitCompletions/{id}:
//   marca/cambia el estado de UN hábito en UNA fecha concreta.
//   (A diferencia de Tasks, aquí sí viaja habitId en el body).
@Serializable
data class HabitCompletionRequestDto(
    val habitId: Long,
    val date: String,            // ISO "yyyy-MM-dd"
    val completed: Boolean
)

// ← GET /HabitCompletions/{id}, /habit/{habitId}, /habit/{habitId}/date/{date}
@Serializable
data class HabitCompletionResponseDto(
    val id: Long,
    val habitId: Long,
    val date: String,
    val completed: Boolean
)
