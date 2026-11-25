package upeu.edu.pe.Data.mocks

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import upeu.edu.pe.Domain.model.Habit


object MockHabits {

    val habits = listOf(

        Habit(
            id = "habit-001",
            name = "Tomar agua",
            frequency = "Diario",
            completed = true,
            currentStreak = 5,
            icon = "water"
        ),

        Habit(
            id = "habit-002",
            name = "Leer",
            frequency = "Diario",
            completed = false,
            currentStreak = 3,
            icon = "book"
        ),

        Habit(
            id = "habit-003",
            name = "Ejercicio",
            frequency = "3 veces por semana",
            completed = false,
            currentStreak = 2,
            icon = "fitness"
        )
    )

}
