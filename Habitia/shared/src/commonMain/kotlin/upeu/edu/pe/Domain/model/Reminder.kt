package upeu.edu.pe.Domain.model


enum class ReminderType {
    NORMAL,
    MEDICATION,
    RECURRING
}

data class Reminder(
    val id: String,
    val title: String,
    val description: String = "",
    val time: String,
    val date: String,
    val recurring: Boolean = false,
    val type: ReminderType = ReminderType.NORMAL,
    val completed: Boolean = false
)