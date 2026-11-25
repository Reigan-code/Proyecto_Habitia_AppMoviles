package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.Reminder

interface ReminderRepository {
    //son sincronas y si bloquean pero llaman de cualquier parte
    fun getReminders(): Flow<List<Reminder>>
    fun getUpcomingReminder(): Flow<Reminder?>
    suspend fun completeReminder(reminderId: String)
    suspend fun addReminder(reminder: Reminder)
}

//Y bueno porque lo del cambio de fun y suspend es que suspend soporta mas peticiones para nuestr Api
//ya que es asincrona y no bloquea vsitas y dentro de la corrutina
