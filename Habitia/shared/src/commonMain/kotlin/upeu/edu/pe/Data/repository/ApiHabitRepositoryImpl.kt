package upeu.edu.pe.Data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.HabitCompletionRequestDto
import upeu.edu.pe.Data.remote.dto.HabitCreateDto
import upeu.edu.pe.Data.remote.dto.HabitResponseDto
import upeu.edu.pe.Data.remote.mapper.backendHabitId
import upeu.edu.pe.Data.remote.mapper.computeStreak
import upeu.edu.pe.Data.remote.mapper.toCreateDto
import upeu.edu.pe.Data.remote.mapper.toDomain
import upeu.edu.pe.Domain.model.Habit
import upeu.edu.pe.Domain.repository.HabitRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

// ═══════════════════════════════════════════════════════════════════════════
// ApiHabitRepositoryImpl — hábitos REALES (/Habits + /HabitCompletions).
//
// refresh() (2 llamadas por hábito, escala OK para proyecto de curso):
//   1) GET /Habits/user/{userId} → lista de hábitos
//   2) POR CADA hábito: GET /HabitCompletions/habit/{id} → computa
//      'completed' de HOY y 'currentStreak' (días seguidos hasta hoy)
// completeHabit(id, flag): PUT o POST de la completion de HOY:
//   - GET /HabitCompletions/habit/{id}/date/{hoy} → si EXISTE → PUT /{id}
//   - si NO existe → POST /HabitCompletions {habitId, date, completed}
// ═══════════════════════════════════════════════════════════════════════════
class ApiHabitRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : HabitRepository {

    private val cache = MutableStateFlow<List<Habit>>(emptyList())

    override fun getHabits(): Flow<List<Habit>> = cache.asStateFlow()

    suspend fun refresh() {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            val habitsResponse =
                httpClient.get("${ApiClient.getBaseUrl()}/Habits/user/$userId") {
                    bearerAuth(token)
                }
            if (!habitsResponse.status.isSuccess()) return
            val habits = habitsResponse.body<List<HabitResponseDto>>()

            // Para cada hábito descarga sus completions y deriva streak/hoy.
            val mapped = habits.map { dto ->
                val compResponse =
                    httpClient.get("${ApiClient.getBaseUrl()}/HabitCompletions/habit/${dto.id}") {
                        bearerAuth(token)
                    }
                val completions: List<Pair<String, Boolean>> =
                    if (compResponse.status.isSuccess()) {
                        compResponse.body<List<upeu.edu.pe.Data.remote.dto.HabitCompletionResponseDto>>()
                            .map { it.date.take(10) to it.completed }
                    } else emptyList()
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
                val completedToday = completions
                    .firstOrNull { it.first == today }?.second ?: false
                dto.toDomain(completedToday = completedToday, streak = computeStreak(completions))
            }
            cache.update { mapped }
        } catch (e: Exception) {
            println("[Habitia-API] habits refresh FALLÓ: ${e.message}")
        }
    }

    override suspend fun completeHabit(habitId: String, completed: Boolean) {
        val s = session.state.value
        val token = s.token ?: return
        try {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
            val backendId = backendHabitId(habitId)
            val request = HabitCompletionRequestDto(habitId = backendId, date = today, completed = completed)

            // ¿Ya existe una completion de HOY? → PUT; si no → POST.
            val existing = httpClient.get(
                "${ApiClient.getBaseUrl()}/HabitCompletions/habit/$backendId/date/$today"
            ) { bearerAuth(token) }

            val response = if (existing.status.isSuccess()) {
                val dto = existing.body<upeu.edu.pe.Data.remote.dto.HabitCompletionResponseDto>()
                httpClient.put("${ApiClient.getBaseUrl()}/HabitCompletions/${dto.id}") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                    setBody(request)
                }
            } else {
                httpClient.post("${ApiClient.getBaseUrl()}/HabitCompletions") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                    setBody(request)
                }
            }
            if (response.status.isSuccess()) {
                // RECALCULO INMEDIATO: baja las completions del hábito y
                // actualiza streak + completado-hoy en caché (así la UI
                // muestra "Racha 1d" al toque, sin esperar el refresh).
                val compResponse = httpClient.get(
                    "${ApiClient.getBaseUrl()}/HabitCompletions/habit/$backendId"
                ) { bearerAuth(token) }
                if (compResponse.status.isSuccess()) {
                    val completions =
                        compResponse.body<List<upeu.edu.pe.Data.remote.dto.HabitCompletionResponseDto>>()
                            .map { it.date.take(10) to it.completed }
                    val todayISO = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
                    val completedToday = completions
                        .firstOrNull { it.first == todayISO }?.second ?: false
                    cache.update { lista ->
                        lista.map {
                            if (it.id == habitId) it.copy(
                                completed = completedToday,
                                currentStreak = computeStreak(completions)
                            ) else it
                        }
                    }
                }
            } else {
                println("[Habitia-API] completeHabit: HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            println("[Habitia-API] completeHabit FALLÓ: ${e.message}")
        }
    }

    // addHabit: crea el ritual/hábito en el backend (POST /Habits) y refresca
    // la caché para que la UI lo muestre sin reiniciar la app.
    override suspend fun addHabit(habit: Habit) {
        val token = session.state.value.token ?: return
        try {
            val response = httpClient.post("${ApiClient.getBaseUrl()}/Habits") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(habit.toCreateDto())
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] addHabit: HTTP ${response.status.value}")
                return
            }
            refresh()
        } catch (e: Exception) {
            println("[Habitia-API] addHabit FALLO: ${e.message}")
        }
    }
}
