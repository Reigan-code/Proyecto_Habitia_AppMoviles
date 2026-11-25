package upeu.edu.pe.Data.remote.mapper

import upeu.edu.pe.Data.remote.dto.HabitCompletionRequestDto
import upeu.edu.pe.Data.remote.dto.HabitResponseDto
import upeu.edu.pe.Domain.model.Habit
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn

// ═══════════════════════════════════════════════════════════════════════════
// HabitMapper — dominio (Habit.kt) ↔ DTOs del backend.
//
// CAMPOS SOLO DEL FRONT: 'completed' (de HOY) y 'currentStreak' NO existen
// en la tabla habits del backend — se derivan de HabitCompletions:
//  - completed  = la completion de HOY (GET /habit/{id}/date/{hoy})
//  - streak     = días consecutivos completados hasta HOY, calculado con
//                 la lista completa de completions del hábito.
// icon: local (el backend no lo modela; el front usa el nombre como clave).
// ═══════════════════════════════════════════════════════════════════════════

// toDomain(): une el hábito con sus completions ya cargadas.
// completions: lista de pares (fechaISO, completed) ordenable.
fun HabitResponseDto.toDomain(
    completedToday: Boolean,
    streak: Int
): Habit = Habit(
    id = "habit-$id",
    name = name,
    frequency = frequency,
    completed = completedToday,
    currentStreak = streak,
    icon = name // el ícono de la UI se elige por nombre (ritualEmojis en Home)
)

// toCreateDto(): para POST /Habits.
fun Habit.toCreateDto(): upeu.edu.pe.Data.remote.dto.HabitCreateDto =
    upeu.edu.pe.Data.remote.dto.HabitCreateDto(
        name = name,
        description = "",
        frequency = frequency.ifBlank { "DAILY" },
        // HOY en zona local (kotlinx.datetime: Clock.System.todayIn —
        // LocalDate.now() NO existe en commonMain, solo java.time en JVM).
        startDate = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString(),
        endDate = null,
        active = true
    )

// toCompletionRequest(): body para POST/PUT de /HabitCompletions.
fun habitCompletionRequest(habitId: Long, dateISO: String, completed: Boolean) =
    HabitCompletionRequestDto(habitId = habitId, date = dateISO, completed = completed)

// computeStreak(): días consecutivos completados contando HACIA ATRÁS desde
// hoy. Solo considera fechas con completed=true. Si hoy no está marcado,
// el streak continúa desde AYER (tolerancia del día en curso).
fun computeStreak(completions: List<Pair<String, Boolean>>): Int {
    val completedDates = completions
        .filter { it.second }
        .map { runCatching { LocalDate.parse(it.first) }.getOrNull() }
        .filterNotNull()
        .toSet()
    if (completedDates.isEmpty()) return 0

    // HOY en zona local (kotlinx.datetime — multiplataforma).
    var streak = 0
    var cursor = Clock.System.todayIn(TimeZone.currentSystemDefault())
    // Si HOY no está completado aún, empieza a contar desde AYER.
    if (cursor !in completedDates) cursor = cursor.minus(1, DateTimeUnit.DAY)
    while (cursor in completedDates) {
        streak++
        // minus(1, DAY) de kotlinx-datetime (minusDays es de java.time,
        // prohibido en commonMain).
        cursor = cursor.minus(1, DateTimeUnit.DAY)
    }
    return streak
}

// backendHabitId(): "habit-42" → 42 (el backend usa Long).
fun backendHabitId(domainId: String): Long =
    domainId.removePrefix("habit-").toLongOrNull() ?: -1L
