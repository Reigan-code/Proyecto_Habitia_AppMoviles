package upeu.edu.pe.Domain.usecase

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.Reminder
import upeu.edu.pe.Domain.repository.ReminderRepository

class GetUpcomingReminderUseCase(
    private val repository: ReminderRepository
) {
    // Retorna directamente el Flow del repositorio
    operator fun invoke(): Flow<Reminder?> {
        return repository.getUpcomingReminder()
    }
}