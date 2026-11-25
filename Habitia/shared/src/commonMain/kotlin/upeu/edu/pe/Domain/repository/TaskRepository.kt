package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.Task

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTodayTasks(): Flow<List<Task>>
    suspend fun completeTask(taskId: String)
    suspend fun addTask(task: Task)

    // updateTask / deleteTask: los usa el calendario (mover un posit a otro
    // día = cambiar dueDate; borrar desde la tarjeta). CON DEFAULT vacío
    // para que los mocks legacy no se rompan; la implementación Api sí los
    // resuelve con PUT/DELETE /Tasks/{id}.
    suspend fun updateTask(task: Task) {}
    suspend fun deleteTask(taskId: String) {}
}
