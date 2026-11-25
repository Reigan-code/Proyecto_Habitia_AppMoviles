// GetHabitsUseCase.kt
package upeu.edu.pe.Domain.usecase

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.Habit
import upeu.edu.pe.Domain.repository.HabitRepository

class GetHabitsUseCase(
    private val repository: HabitRepository
) {
    // EL MODIFICADOR 'operator' ES OBLIGATORIO PARA USAR getHabits() COMO FUNCIÓN
    operator fun invoke(): Flow<List<Habit>> {
        return repository.getHabits()
    }
}