package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════════
// DTOs de TASK — espejo 1:1 de DTOs/TaskCreateDto, TaskUpdateDto y
// TaskResponseDto del backend HABITIA-Back.
//
// Diferencias IMPORTANTES contra el modelo de dominio (Domain/model/Taks.kt):
//  - id         : Long (backend)  vs String (dominio) → se adapta en el mapper
//  - userId     : el backend lo resuelve DESDE EL JWT, no se envía en el body
//                 de creación (TaskServiceImpl.crear usa el usuario autenticado)
//  - status     : enum PENDING / IN_PROGRESS / COMPLETED; el dominio usa el
//                 Boolean 'completed' → mapper: completed ↔ COMPLETED/PENDING
//  - priority   : enum LOW / MEDIUM / HIGH (misma forma que TaskPriority)
//  - dueDate    : LocalDate serializado como ISO "yyyy-MM-dd" (como MockTasks)
//  - El backend NO tiene category, dueTime ni recurring: esos tres campos son
//    locales de la app; el repositorio los conserva en memoria (ver
//    ApiTaskRepositoryImpl).
// ═══════════════════════════════════════════════════════════════════════════

// TaskCreateDto → POST /Tasks. El backend pone PENDING por defecto y asigna
// el dueño desde el JWT; por eso aquí NO existe ni userId ni status.
@Serializable
data class TaskCreateDto(
    val title: String,              // obligatorio, ≤150 caracteres
    val description: String,        // opcional en el backend
    val dueDate: String?,           // ISO "yyyy-MM-dd" o null
    val priority: String            // "LOW" | "MEDIUM" | "HIGH"
)

// TaskUpdateDto → PUT /Tasks/{id}. Para actualizar hay que mandar el estado
// completo (title/description/dueDate/priority/status): el servicio hace
// update masivo de los campos no nulos.
@Serializable
data class TaskUpdateDto(
    val title: String,
    val description: String,
    val dueDate: String?,
    val priority: String,
    val status: String              // "PENDING" | "IN_PROGRESS" | "COMPLETED"
)

// TaskResponseDto ← GET /Tasks/user/{userId}, GET /Tasks/{id}, POST, PUT.
@Serializable
data class TaskResponseDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String,
    val dueDate: String?,           // ISO "yyyy-MM-dd" (o null)
    val priority: String,
    val status: String
)
