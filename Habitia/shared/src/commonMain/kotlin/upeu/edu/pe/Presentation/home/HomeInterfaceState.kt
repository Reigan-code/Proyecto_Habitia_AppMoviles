package upeu.edu.pe.Presentation.home

import upeu.edu.pe.Domain.model.Habit
import upeu.edu.pe.Domain.model.Reminder
import upeu.edu.pe.Domain.model.Task


data class HomeInterfaceState(
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val upcomingReminder: Reminder? = null,
    val completedTasks: Int = 0,
    val totalTasks: Int = 0,
    val completedHabits: Int = 0,
    val totalHabits: Int = 0,
    val progress: Float = 0f
)