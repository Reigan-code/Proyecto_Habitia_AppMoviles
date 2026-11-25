package upeu.edu.pe.Presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upeu.edu.pe.Domain.repository.HabitRepository
import upeu.edu.pe.Domain.repository.ReminderRepository
import upeu.edu.pe.Domain.repository.TaskRepository
import upeu.edu.pe.Domain.usecase.CompleteTaskUseCase
import upeu.edu.pe.Domain.usecase.GetHabitsUseCase
import upeu.edu.pe.Domain.usecase.GetTodayTasksUseCase
import upeu.edu.pe.Domain.usecase.GetUpcomingReminderUseCase

class HomeViewModel(
    private val getTodayTasks: GetTodayTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val getUpcomingReminder: GetUpcomingReminderUseCase,
    private val getHabits: GetHabitsUseCase,
    private val taskRepo: TaskRepository,
    private val habitRepo: HabitRepository,
    private val reminderRepo: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeInterfaceState())
    val uiState: StateFlow<HomeInterfaceState> = _uiState.asStateFlow()

    init {
        observeTasks()
        observeHabits()
        observeReminder()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            getTodayTasks().collect { taskList ->
                _uiState.value = _uiState.value.copy(
                    tasks = taskList,
                    totalTasks = taskList.size,
                    completedTasks = taskList.count { it.completed },
                    isLoading = false
                )
                updateProgress()
            }
        }
    }

    private fun observeHabits() {
        viewModelScope.launch {
            getHabits().collect { habitList ->
                _uiState.value = _uiState.value.copy(
                    habits = habitList,
                    totalHabits = habitList.size,
                    completedHabits = habitList.count { it.completed }
                )
                updateProgress()
            }
        }
    }

    private fun observeReminder() {
        viewModelScope.launch {
            getUpcomingReminder().collect { reminderItem ->
                _uiState.value = _uiState.value.copy(upcomingReminder = reminderItem)
            }
        }
    }

    private fun updateProgress() {
        val state = _uiState.value
        val total = state.totalTasks + state.totalHabits
        val completed = state.completedTasks + state.completedHabits
        _uiState.value = state.copy(progress = if (total > 0) completed.toFloat() / total else 0f)
    }

    fun completeTask(taskId: String, @Suppress("UNUSED_PARAMETER") completed: Boolean) {
        viewModelScope.launch { completeTaskUseCase(taskId) }
    }

    fun completeReminder(reminderId: String) {
        viewModelScope.launch { reminderRepo.completeReminder(reminderId) }
    }

    fun toggleHabit(habitId: String) {
        viewModelScope.launch {
            val current = _uiState.value.habits.find { it.id == habitId }
            if (current != null) habitRepo.completeHabit(habitId, !current.completed)
        }
    }

    // createRitual: persiste un ritual nuevo en el backend (POST /Habits) y
    // refresca la lista (el Flow de getHabits() actualiza uiState.habits).
    fun createRitual(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            habitRepo.addHabit(
                upeu.edu.pe.Domain.model.Habit(
                    id = "",
                    name = name.trim(),
                    frequency = "DAILY",
                    completed = false,
                    currentStreak = 0
                )
            )
        }
    }
}
