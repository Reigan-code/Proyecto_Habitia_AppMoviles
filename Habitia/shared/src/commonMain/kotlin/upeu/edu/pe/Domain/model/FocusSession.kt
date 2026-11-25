package upeu.edu.pe.Domain.model


data class FocusSession(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val completed: Boolean = false,
    val date: String
)