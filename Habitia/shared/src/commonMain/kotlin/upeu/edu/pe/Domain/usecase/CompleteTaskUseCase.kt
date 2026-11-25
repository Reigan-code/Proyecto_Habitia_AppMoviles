package upeu.edu.pe.Domain.usecase

import upeu.edu.pe.Domain.repository.TaskRepository

class CompleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String) {
        repository.completeTask(taskId)
    }
}
