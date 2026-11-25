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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.ReminderCreateDto
import upeu.edu.pe.Data.remote.dto.ReminderResponseDto
import upeu.edu.pe.Data.remote.dto.ReminderUpdateDto
import upeu.edu.pe.Data.remote.mapper.backendReminderId
import upeu.edu.pe.Data.remote.mapper.joinDateTime
import upeu.edu.pe.Data.remote.mapper.toDomain
import upeu.edu.pe.Domain.model.Reminder
import upeu.edu.pe.Domain.repository.ReminderRepository

// ═══════════════════════════════════════════════════════════════════════════
// ApiReminderRepositoryImpl — recordatorios REALES (/Reminders).
// NOTA: este módulo SÍ recibe userId en el create DTO (a diferencia de
// Tasks/Habits) — se toma de SessionManager, nunca hardcodeado.
// El dominio expone getUpcomingReminder(): Flow<Reminder?> — se deriva con
// map() del cache (el próximo NO completado, ordenado por fecha-hora).
// ═══════════════════════════════════════════════════════════════════════════
class ApiReminderRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : ReminderRepository {

    private val cache = MutableStateFlow<List<Reminder>>(emptyList())

    override fun getReminders(): Flow<List<Reminder>> = cache.asStateFlow()

    // getUpcomingReminder(): derivado del cache — próximo no completado por
    // fecha-hora (date+time ordenan lexicográficamente ISO "yyyy-MM-dd" y
    // "HH:mm" → comparación segura sin parsear).
    override fun getUpcomingReminder(): Flow<Reminder?> =
        cache.map { lista ->
            lista.filter { !it.completed }.minByOrNull { it.date + it.time }
        }

    suspend fun refresh() {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            val response =
                httpClient.get("${ApiClient.getBaseUrl()}/Reminders/user/$userId") {
                    bearerAuth(token)
                }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] reminders refresh: HTTP ${response.status.value}")
                return
            }
            val dtos = response.body<List<ReminderResponseDto>>()
            cache.update { dtos.map { it.toDomain() } }
        } catch (e: Exception) {
            println("[Habitia-API] reminders refresh FALLÓ: ${e.message}")
        }
    }

    override suspend fun completeReminder(reminderId: String) {
        // toggle: usa el update DTO completo (el backend lo exige).
        val task = cache.value.firstOrNull { it.id == reminderId } ?: return
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        val nuevoEstado = !task.completed
        try {
            val response = httpClient.put(
                "${ApiClient.getBaseUrl()}/Reminders/user/$userId/${backendReminderId(reminderId)}"
            ) {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    ReminderUpdateDto(
                        title = task.title,
                        description = task.description,
                        reminderDate = joinDateTime(task.date, task.time),
                        repeat = if (task.recurring) "DAILY" else "NONE",
                        completed = nuevoEstado
                    )
                )
            }
            if (response.status.isSuccess()) {
                cache.update { lista ->
                    lista.map { if (it.id == reminderId) it.copy(completed = nuevoEstado) else it }
                }
            } else {
                println("[Habitia-API] completeReminder: HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            println("[Habitia-API] completeReminder FALLÓ: ${e.message}")
        }
    }

    override suspend fun addReminder(reminder: Reminder) {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            val response = httpClient.post("${ApiClient.getBaseUrl()}/Reminders") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    ReminderCreateDto(
                        userId = userId, // ESTE módulo lo exige en el body
                        title = reminder.title,
                        description = reminder.description,
                        reminderDate = joinDateTime(reminder.date, reminder.time),
                        repeat = if (reminder.recurring) "DAILY" else "NONE"
                    )
                )
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] addReminder: HTTP ${response.status.value}")
                return
            }
            val dto = response.body<ReminderResponseDto>()
            // Versión REAL del servidor a la caché (con id del backend).
            cache.update { it + dto.toDomain() }
        } catch (e: Exception) {
            println("[Habitia-API] addReminder FALLÓ: ${e.message}")
        }
    }
}
