package upeu.edu.pe.Presentation.reminders

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import upeu.edu.pe.Domain.model.Reminder
import upeu.edu.pe.Domain.model.ReminderType
import upeu.edu.pe.Domain.repository.ReminderRepository

class RemindersViewModel(
    private val repository: ReminderRepository
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    val reminders: StateFlow<List<Reminder>> = repository.getReminders().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun toggleReminder(reminderId: String) {
        viewModelScope.launch { repository.completeReminder(reminderId) }
    }

    fun addReminder(title: String, description: String, time: String, type: ReminderType) {
        viewModelScope.launch {
            // Normaliza la hora: "9:00" → "09:00", "9" → "09:00". SIN esto
            // el backend falla el parseo de LocalDateTime ("...T9:00") y el
            // POST llega a 400 — el aviso jamás se creaba ("no se crean
            // alertas"). Hora inválida → fallback 09:00 en vez de bloquear.
            val partes = time.trim().split(':')
            val hora = "%02d".format(partes.getOrNull(0)?.trim()?.toIntOrNull()?.coerceIn(0, 23) ?: 9)
            val minuto = "%02d".format(partes.getOrNull(1)?.trim()?.toIntOrNull()?.coerceIn(0, 59) ?: 0)
            val horaNormalizada = "$hora:$minuto"
            repository.addReminder(
                Reminder(
                    id = "", // el id REAL lo asigna el backend en el POST
                    title = title,
                    description = description,
                    time = horaNormalizada,
                    // HOY sin hardcode (kotlinx-datetime en commonMain).
                    date = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString(),
                    recurring = type == ReminderType.RECURRING,
                    type = type,
                    completed = false
                )
            )
        }
    }
}
