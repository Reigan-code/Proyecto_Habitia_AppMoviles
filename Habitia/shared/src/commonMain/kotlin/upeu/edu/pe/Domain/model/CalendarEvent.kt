package upeu.edu.pe.Domain.model

data class CalendarEvent(
    val id: String,
    val title: String,
    val description: String = "",
    val date: String,
    val startTime: String,
    val endTime: String
)

