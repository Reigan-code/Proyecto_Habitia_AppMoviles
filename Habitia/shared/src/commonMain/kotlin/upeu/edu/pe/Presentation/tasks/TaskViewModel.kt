package upeu.edu.pe.Presentation.tasks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import upeu.edu.pe.Domain.model.Task
import upeu.edu.pe.Domain.model.TaskPriority
import upeu.edu.pe.Domain.repository.TaskCategoryRepository
import upeu.edu.pe.Domain.repository.TaskRepository

class TasksViewModel(
    private val repository: TaskRepository,
    private val categoryRepository: TaskCategoryRepository
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    private val _currentCategory = MutableStateFlow("Hogar")

    // Categorías editables por el usuario.
    val categories: StateFlow<List<String>> = categoryRepository.getCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf("Hogar", "Compras", "Alimentación")
        )

    fun addCategory(name: String) {
        viewModelScope.launch { categoryRepository.add(name) }
    }

    fun deleteCategory(name: String) {
        viewModelScope.launch {
            categoryRepository.delete(name)
            if (_currentCategory.value == name) {
                _currentCategory.value = categories.value.firstOrNull { it != name } ?: "Hogar"
            }
        }
    }

    val uiState: StateFlow<TasksUiState> = _currentCategory
        .flatMapLatest { category ->
            repository.getTasks()
                .map { tasks ->
                    TasksUiState(
                        isLoading = false,
                        tasks = tasks.filter { it.category == category },
                        currentCategory = category
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TasksUiState()
        )

    // allTasks: TODAS las tareas (sin filtro de categoría) para la Matriz
    // Eisenhower — la matriz clasifica por urgencia/importancia global,
    // no por categoría del tablero.
    val allTasks: StateFlow<List<Task>> = repository.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun changeCategory(category: String) {
        _currentCategory.value = category
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch { repository.completeTask(taskId) }
    }

    // updateTask(): expone PUT /Tasks/{id} del repositorio para que la
    // matriz Eisenhower y el tablero Kanban (drag&drop) puedan persistir
    // cambios de prioridad/dueDate/completed.
    fun updateTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun moveTaskInMatrix(task: Task, quadrant: String) {
        val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val manana = hoy.plus(1, DateTimeUnit.DAY)
        val (newPriority, newDue) = when (quadrant) {
            "Q1" -> (if (task.priority == TaskPriority.LOW) TaskPriority.MEDIUM else task.priority) to hoy.toString()
            "Q2" -> (if (task.priority == TaskPriority.LOW) TaskPriority.MEDIUM else task.priority) to manana.plus(6, DateTimeUnit.DAY).toString()
            "Q3" -> TaskPriority.LOW to hoy.toString()
            "Q4" -> TaskPriority.LOW to manana.plus(6, DateTimeUnit.DAY).toString()
            "DONE" -> task.priority to task.dueDate
            else -> task.priority to task.dueDate
        }
        val nowCompleted = (quadrant == "DONE")
        val updated = task.copy(priority = newPriority, dueDate = newDue, completed = nowCompleted)
        if (updated != task) updateTask(updated)
    }

    fun addNewTask(title: String, description: String, priority: TaskPriority) {
        viewModelScope.launch {
            repository.addTask(
                Task(
                    id = "", // el id REAL lo asigna el backend en el POST
                    title = title,
                    description = description,
                    category = _currentCategory.value,
                    priority = priority,
                    // HOY sin hardcode (antes "2026-09-07" fijo del mock).
                    dueDate = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString(),
                    completed = false
                )
            )
        }
    }
}
