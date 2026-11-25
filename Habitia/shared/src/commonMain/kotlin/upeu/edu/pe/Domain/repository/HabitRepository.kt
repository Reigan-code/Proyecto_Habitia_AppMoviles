package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.Habit

interface HabitRepository {
    fun getHabits(): Flow<List<Habit>>
    suspend fun completeHabit(habitId: String, completed: Boolean)

    // addHabit: crea un hǭbito en el backend (POST /Habits) y refresca la
    // cachǸ. Tiene default vac��o para que los mocks legacy no se rompan.
    suspend fun addHabit(habit: Habit) {}
}