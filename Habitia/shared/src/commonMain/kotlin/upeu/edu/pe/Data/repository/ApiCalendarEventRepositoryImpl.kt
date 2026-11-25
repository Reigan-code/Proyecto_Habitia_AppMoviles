package upeu.edu.pe.Data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
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
import upeu.edu.pe.Data.remote.dto.CalendarEventDto
import upeu.edu.pe.Data.remote.dto.CalendarEventRequestDto
import upeu.edu.pe.Data.remote.mapper.backendEventId
import upeu.edu.pe.Data.remote.mapper.calendarEndISO
import upeu.edu.pe.Data.remote.mapper.calendarStartISO
import upeu.edu.pe.Data.remote.mapper.toDomain
import upeu.edu.pe.Domain.model.CalendarEvent
import upeu.edu.pe.Domain.repository.CalendarEventRepository

// ═══════════════════════════════════════════════════════════════════════════
// ApiCalendarEventRepositoryImpl — eventos del Planner REALES
// (/CalendarEvents). El backend devuelve la ENTIDAD directamente (con un
// objeto 'user' anidado que el Json ignora con ignoreUnknownKeys).
// OJO: el POST/PUT del backend recibe la entidad — enviamos solo los campos
// del dominio; userId lo resuelve el backend desde el JWT (patrón Tasks).
// ═══════════════════════════════════════════════════════════════════════════
class ApiCalendarEventRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : CalendarEventRepository {

    private val cache = MutableStateFlow<List<CalendarEvent>>(emptyList())

    override fun getEvents(): Flow<List<CalendarEvent>> = cache.asStateFlow()

    override suspend fun refresh() {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            val response =
                httpClient.get("${ApiClient.getBaseUrl()}/CalendarEvents/user/$userId") {
                    bearerAuth(token)
                }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] calendar refresh: HTTP ${response.status.value}")
                return
            }
            val dtos = response.body<List<CalendarEventDto>>()
            cache.update { dtos.map { it.toDomain() } }
        } catch (e: Exception) {
            println("[Habitia-API] calendar refresh FALLÓ: ${e.message}")
        }
    }

    override suspend fun addEvent(event: CalendarEvent) {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            val response = httpClient.post("${ApiClient.getBaseUrl()}/CalendarEvents") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    CalendarEventRequestDto(
                        title = event.title,
                        description = event.description,
                        startDate = calendarStartISO(event.date, event.startTime),
                        endDate = calendarEndISO(event.date, event.endTime),
                        // El backend NO resuelve el dueño del JWT en este
                        // módulo: el user{id} viaja en el body de la entidad.
                        user = upeu.edu.pe.Data.remote.dto.CalendarEventUserDto(id = userId)
                    )
                )
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] addEvent: HTTP ${response.status.value}")
                return
            }
            val dto = response.body<CalendarEventDto>()
            cache.update { it + dto.toDomain() }
        } catch (e: Exception) {
            println("[Habitia-API] addEvent FALLÓ: ${e.message}")
        }
    }

    override suspend fun updateEvent(event: CalendarEvent) {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            val response = httpClient.put(
                "${ApiClient.getBaseUrl()}/CalendarEvents/${backendEventId(event.id)}"
            ) {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    CalendarEventRequestDto(
                        title = event.title,
                        description = event.description,
                        startDate = calendarStartISO(event.date, event.startTime),
                        endDate = calendarEndISO(event.date, event.endTime),
                        // El backend NO resuelve el dueño del JWT en este
                        // módulo: el user{id} viaja en el body de la entidad.
                        user = upeu.edu.pe.Data.remote.dto.CalendarEventUserDto(id = userId)
                    )
                )
            }
            if (response.status.isSuccess()) {
                cache.update { lista -> lista.map { if (it.id == event.id) event else it } }
            } else {
                println("[Habitia-API] updateEvent: HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            println("[Habitia-API] updateEvent FALLÓ: ${e.message}")
        }
    }

    override suspend fun deleteEvent(eventId: String) {
        val s = session.state.value
        val token = s.token ?: return
        try {
            val response =
                httpClient.delete("${ApiClient.getBaseUrl()}/CalendarEvents/${backendEventId(eventId)}") {
                    bearerAuth(token)
                }
            if (response.status.isSuccess()) {
                cache.update { lista -> lista.filterNot { it.id == eventId } }
            }
        } catch (e: Exception) {
            println("[Habitia-API] deleteEvent FALLÓ: ${e.message}")
        }
    }
}
