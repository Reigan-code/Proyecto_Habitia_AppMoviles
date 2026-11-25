package upeu.edu.pe.Domain.model


enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val category: String,
    val priority: TaskPriority,
    val dueDate: String,
    val dueTime: String? = null,
    val completed: Boolean = false,
    val recurring: Boolean = false
)