package upeu.edu.pe.Data.remote.mapper

import upeu.edu.pe.Data.remote.dto.TaskCreateDto
import upeu.edu.pe.Data.remote.dto.TaskResponseDto
import upeu.edu.pe.Data.remote.dto.TaskUpdateDto
import upeu.edu.pe.Domain.model.Task
import upeu.edu.pe.Domain.model.TaskPriority

// ═══════════════════════════════════════════════════════════════════════════
// TaskMapper — traduce DTOs del backend (Data/remote/dto) ↔ modelos de
// dominio (Domain/model/Taks.kt).
//
// ¿Por qué un mapper y no usar los DTOs directo en la UI?
//  - Clean Architecture: la UI/ViewModels hablan el lenguaje del dominio
//    (String ids, Boolean completed, categoría local), el backend habla el
//    suyo (Long ids, enum status). El mapper absorbe TODAS las diferencias
//    para que ViewModels y use cases NO cambien ni una línea.
/// ═══════════════════════════════════════════════════════════════════════════

// ═══ DOMINIO → BACKEND ═══

// toCreateDto(): arma el body de POST /Tasks desde una Task del dominio.
//  - id: se IGNORA (el backend genera el Long con su base de datos).
//  - completed/recurring/dueTime/category: NO existen en TaskCreateDto.
//  - priority: TaskPriority y el enum Priority del backend comparten los
//    nombres LOW/MEDIUM/HIGH → basta .name.
fun Task.toCreateDto(): TaskCreateDto = TaskCreateDto(
    title = title,
    description = description,
    dueDate = isoDateOrNull(dueDate),
    priority = priority.name
)

// toUpdateDto(status): arma el body de PUT /Tasks/{id}. status lo decide la
// llamada (toggle completed): 'IN_PROGRESS' intermedia queda para el futuro.
fun Task.toUpdateDto(status: String): TaskUpdateDto = TaskUpdateDto(
    title = title,
    description = description,
    dueDate = isoDateOrNull(dueDate),
    priority = priority.name,
    status = status
)

// ═══ BACKEND → DOMINIO ═══

// toDomain(): convierte la respuesta del backend en Task del dominio.
//  - id: "task-<Long>" (el dominio usa String); el mapper INVERSO simplemente
//    descarta el prefijo.
//  - status → completed: COMPLETED = true (aunque sea IN_PROGRESS se ve como
//    pendiente en la UI, decisión de primera iteración).
//  - category/dueTime/recurring: el backend no los devuelve → se usan los
//    valores de respaldo pasados por el repositorio (su caché local), con
//    "Hogar" como categoría por defecto (la primera pestaña de TasksScreen).
fun TaskResponseDto.toDomain(
    categoryBackup: String = "Hogar",
    dueTimeBackup: String? = null
): Task = Task(
    id = "task-$id",
    title = title,
    description = description,
    category = categoryBackup,
    priority = parsePriority(priority),
    dueDate = dueDate ?: "",
    dueTime = dueTimeBackup,
    completed = status.equals("COMPLETED", ignoreCase = true),
    recurring = false
)

// parsePriority(): enum del backend → enum del dominio, tolerante a mayúsculas
// o valores inesperados (fallback MEDIUM en vez de crashear).
private fun parsePriority(value: String): TaskPriority =
    runCatching { TaskPriority.valueOf(value.uppercase()) }.getOrDefault(TaskPriority.MEDIUM)

// isoDateOrNull(): normaliza dueDate del dominio (String) al formato ISO que
// espera el backend (LocalDate "yyyy-MM-dd"). Si la UI algún día guarda
// "2026-11-04T18:00" se recorta la parte de fecha; si viene vacía → null.
fun isoDateOrNull(rawDate: String): String? {
    val trimmed = rawDate.trim()
    if (trimmed.isEmpty()) return null
    // Corta la parte de hora si existiera (antes de 'T' o ' ')
    val datePart = trimmed.split('T', ' ').first()
    return if (datePart.length >= 10) datePart.take(10) else trimmed
}
