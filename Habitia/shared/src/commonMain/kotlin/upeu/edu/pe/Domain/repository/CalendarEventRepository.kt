package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.CalendarEvent

// ═══════════════════════════════════════════════════════════════════════════
// CalendarEventRepository — NUEVA interfaz de dominio para el Planner.
// Antes el PlannerViewModel mantenía su lista EN MEMORIA (mock interno):
// ahora la lógica de datos es la misma de los demás módulos (Clean
// Architecture): interfaz aquí, implementación Api en Data.
// ═══════════════════════════════════════════════════════════════════════════
interface CalendarEventRepository {

    // getEvents(): todos los eventos del usuario (la UI filtra por día).
    fun getEvents(): Flow<List<CalendarEvent>>

    // addEvent(): crea un evento en el backend y lo agrega al cache.
    suspend fun addEvent(event: CalendarEvent)

    // deleteEvent(): elimina por id de dominio ("event-<n>").
    suspend fun deleteEvent(eventId: String)

    // updateEvent(): persiste cambios del evento (el calendario usa esto
    // para MOVER un posit a otro día → nueva startDate/endDate).
    suspend fun updateEvent(event: CalendarEvent)

    // refresh(): baja los eventos del usuario (la llama App.kt cuando la
    // sesión queda lista — patrón de los demás módulos).
    suspend fun refresh()
}
