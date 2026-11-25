package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow

// Categorías del tablero de Tareas, editables por el usuario.
interface TaskCategoryRepository {
    fun getCategories(): Flow<List<String>>
    suspend fun add(name: String)
    suspend fun delete(name: String)
}
