package upeu.edu.pe.Presentation.tasks

import upeu.edu.pe.Domain.model.Task

data class TasksUiState(
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val currentCategory: String = "Hogar"
)