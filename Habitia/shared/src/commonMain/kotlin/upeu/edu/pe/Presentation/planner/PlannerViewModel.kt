package upeu.edu.pe.Presentation.planner

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.google.GoogleCalendarClient
import upeu.edu.pe.Data.remote.google.GoogleEventDto
import upeu.edu.pe.Data.remote.mapper.calendarEndISO
import upeu.edu.pe.Data.remote.mapper.calendarStartISO
import upeu.edu.pe.Data.remote.mapper.toDomain
import upeu.edu.pe.Data.repository.ApiCalendarEventRepositoryImpl
import upeu.edu.pe.Data.repository.ApiTaskRepositoryImpl
import upeu.edu.pe.Domain.model.CalendarEvent
import upeu.edu.pe.Domain.repository.CalendarEventRepository
import upeu.edu.pe.Domain.repository.TaskRepository

// ═══════════════════════════════════════════════════════════════════════════
// PlannerViewModel — CALENDARIO COMPLETO estilo Google Calendar.
//
// Maneja TRES fuentes de posits:
//   1. Eventos del backend (/CalendarEvents) — movibles (PUT startDate/endDate)
//      y eliminables.
//   2. Tareas del backend (/Tasks, por dueDate) — movibles (updateTask con
//      dueDate nuevo), completables y eliminables.
//   3. Eventos de GOOGLE CALENDAR PÚBLICO (API key) — read-only.
// Además: navegación por MESES (mes anterior/siguiente) y día seleccionado.
// ═══════════════════════════════════════════════════════════════════════════
class PlannerViewModel(
    private val eventRepository: CalendarEventRepository,
    private val taskRepository: TaskRepository? = null,
    private val googleClient: GoogleCalendarClient? = null
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    // ── MES Y DÍA ──
    private val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val _selectedMonth = MutableStateFlow(hoy.monthNumber)   // 1..12
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()
    private val _selectedYear = MutableStateFlow(hoy.year)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()
    private val _selectedDay = MutableStateFlow(hoy.toString())
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    fun selectDay(isoDay: String) { _selectedDay.value = isoDay }

    fun previousMonth() {
        if (_selectedMonth.value == 1) {
            _selectedMonth.value = 12
            _selectedYear.value -= 1
        } else {
            _selectedMonth.value -= 1
        }
    }

    fun nextMonth() {
        if (_selectedMonth.value == 12) {
            _selectedMonth.value = 1
            _selectedYear.value += 1
        } else {
            _selectedMonth.value += 1
        }
    }

    // ── POSITS: EVENTOS DEL BACKEND ──
    // (la implementación Api devuelve asStateFlow — el cast es seguro)
    @Suppress("UNCHECKED_CAST")
    val events: StateFlow<List<CalendarEvent>>
        get() = eventRepository.getEvents() as StateFlow<List<CalendarEvent>>

    // ── POSITS: TAREAS DEL BACKEND (por dueDate) ──
    @Suppress("UNCHECKED_CAST")
    val tasks: StateFlow<List<upeu.edu.pe.Domain.model.Task>>
        get() = (taskRepository?.getTasks() as? StateFlow<List<upeu.edu.pe.Domain.model.Task>>)
            ?: MutableStateFlow(emptyList())

    // ── POSITS: GOOGLE CALENDAR ──
    private val _googleEvents = MutableStateFlow<List<GoogleEventDto>>(emptyList())
    val googleEvents: StateFlow<List<GoogleEventDto>> = _googleEvents.asStateFlow()

    private val _googleStatus = MutableStateFlow<String?>(null)
    val googleStatus: StateFlow<String?> = _googleStatus.asStateFlow()

    private var lastGoogleCalendarId: String? = null

    // fetchGoogle(): sincroniza los eventos del mes visible. calendarId
    // típico: "primary" del dueño del calendario público o su correo si
    // compartió con "ver público".
    fun fetchGoogle(calendarId: String) {
        val client = googleClient ?: return
        val year = _selectedYear.value
        val month = _selectedMonth.value
        val monthStart = "${"%04d".format(year)}-${"%02d".format(month)}-01"
        val monthEnd = "${"%04d".format(year)}-${"%02d".format(month)}-28T23:59:59Z"
        viewModelScope.launch {
            _googleStatus.value = "Sincronizando…"
            val items = client.fetchEvents(
                calendarId = calendarId.ifBlank { "primary" },
                timeMinISO = "${monthStart}T00:00:00Z",
                timeMaxISO = monthEnd
            )
            lastGoogleCalendarId = calendarId
            _googleEvents.value = items
            _googleStatus.value = if (items.isEmpty()) {
                "Sin eventos públicos en este mes (revisa que el calendario sea público y la key válida)."
            } else {
                "${items.size} eventos de Google sincronizados ✓"
            }
        }
    }

    fun refetchGoogleForMonth() {
        lastGoogleCalendarId?.let { fetchGoogle(it) }
    }

    fun clearGoogleStatus() { _googleStatus.value = null }

    // ── EVENTOS ──
    fun addEvent(title: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            eventRepository.addEvent(
                CalendarEvent(
                    id = "", // el id REAL lo asigna el backend en el POST
                    title = title,
                    description = "Planificado desde la agenda",
                    date = _selectedDay.value,
                    startTime = startTime,
                    endTime = endTime
                )
            )
        }
    }

    // moveEvent(eventId, nuevoDía): recalcula startDate/endDate manteniendo
    // la DURACIÓN y persiste con PUT /CalendarEvents/{id}.
    fun moveEvent(eventId: String, newDateISO: String) {
        viewModelScope.launch {
            val ev = events.value.firstOrNull { it.id == eventId } ?: return@launch
            val durMin = minutes(ev.endTime) - minutes(ev.startTime)
            val nuevoFin = addMinutes(ev.startTime, durMin)
            eventRepository.updateEvent(
                ev.copy(date = newDateISO, endTime = nuevoFin)
            )
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch { eventRepository.deleteEvent(eventId) }
    }

    // ── TAREAS ──
    // moveTask(taskId, nuevoDía): cambia dueDate vía PUT /Tasks/{id}.
    fun moveTask(taskId: String, newDateISO: String) {
        viewModelScope.launch {
            val t = tasks.value.firstOrNull { it.id == taskId } ?: return@launch
            taskRepository?.updateTask(t.copy(dueDate = newDateISO))
        }
    }

    fun toggleTask(taskId: String) {
        viewModelScope.launch {
            val t = tasks.value.firstOrNull { it.id == taskId } ?: return@launch
            taskRepository?.updateTask(t.copy(completed = !t.completed))
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch { taskRepository?.deleteTask(taskId) }
    }

    // refresh(): baja eventos + tareas (App.kt lo llama al loguear).
    suspend fun refresh() = eventRepository.refresh()

    // recompute helpers (kotlinx datetime no tiene LocalTime completo en 0.6):
    private fun minutes(hhmm: String): Int {
        val parts = hhmm.split(':')
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h * 60 + m
    }

    private fun addMinutes(hhmm: String, deltaMin: Int): String {
        val total = (minutes(hhmm) + deltaMin).coerceAtLeast(0)
        val h = (total / 60) % 24
        val m = total % 60
        return "%02d:%02d".format(h, m)
    }
}
