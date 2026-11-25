package upeu.edu.pe.Data.mocks

import upeu.edu.pe.Domain.model.Reminder
import upeu.edu.pe.Domain.model.ReminderType

object MockReminders {

    val reminders = listOf(

        Reminder(
            id = "reminder-001",
            title = "Tomar medicamento",
            description = "Recordatorio de medicación",
            time = "20:00",
            date = "2026-09-07",
            recurring = true,
            type = ReminderType.MEDICATION
        ),

        Reminder(
            id = "reminder-002",
            title = "Preparar mochila",
            time = "21:00",
            date = "2026-09-07"
        ),

        Reminder(
            id = "reminder-003",
            title = "Revisar agenda",
            time = "22:00",
            date = "2026-09-07",
            recurring = true,
            type = ReminderType.RECURRING
        )
    )
}