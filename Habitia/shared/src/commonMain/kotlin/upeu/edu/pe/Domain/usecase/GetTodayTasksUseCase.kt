package upeu.edu.pe.Domain.usecase

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.Task
import upeu.edu.pe.Domain.repository.TaskRepository

class GetTodayTasksUseCase(
    private val repository: TaskRepository
) {
    //nvoca o defin la funcion del repositorio para obtener y devolver las tareas de hoy como un flujo de datos(flow)
    operator fun invoke(): Flow<List<Task>> = repository.getTodayTasks()
}
