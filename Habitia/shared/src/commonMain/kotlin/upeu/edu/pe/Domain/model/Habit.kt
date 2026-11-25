package upeu.edu.pe.Domain.model

data class Habit(
    val id: String,
    val name: String,
    val frequency: String,
    val completed: Boolean = false,
    val currentStreak: Int = 0,
    val icon: String = ""
)