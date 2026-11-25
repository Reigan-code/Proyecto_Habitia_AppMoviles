package upeu.edu.pe.Data.mocks

import upeu.edu.pe.Domain.model.Task
import upeu.edu.pe.Domain.model.TaskPriority

object MockTasks {

    val tasks = listOf(

        Task(
            id = "task-001",
            title = "Lavar ropa",
            description = "Separar ropa blanca y de color",
            category = "Hogar",
            priority = TaskPriority.MEDIUM,
            dueDate = "2026-09-07",
            dueTime = "18:00"
        ),

        Task(
            id = "task-002",
            title = "Ordenar cocina",
            category = "Hogar",
            priority = TaskPriority.HIGH,
            dueDate = "2026-09-07",
            dueTime = "19:00"
        ),

        Task(
            id = "task-003",
            title = "Comprar frutas",
            category = "Compras",
            priority = TaskPriority.MEDIUM,
            dueDate = "2026-09-07",
            dueTime = "17:30"
        ),

        Task(
            id = "task-004",
            title = "Regar plantas",
            category = "Hogar",
            priority = TaskPriority.LOW,
            dueDate = "2026-09-07",
            completed = true
        ),

        Task(
            id = "task-005",
            title = "Preparar cena",
            category = "Alimentación",
            priority = TaskPriority.HIGH,
            dueDate = "2026-09-07",
            dueTime = "19:30"
        )
    )
}