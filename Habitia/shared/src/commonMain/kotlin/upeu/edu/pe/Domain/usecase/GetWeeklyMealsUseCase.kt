package upeu.edu.pe.Domain.usecase

import upeu.edu.pe.Domain.model.Meal
import upeu.edu.pe.Domain.repository.MealRepository

class GetWeeklyMealsUseCase(
    private val repository: MealRepository
) {
//invoca la función del repositorio de manera asíncrona (suspend) para obtener
    // y devolver una lista única de comidas semanales
    suspend operator fun invoke(): List<Meal> {
        return repository.getWeeklyMeals()
    }

    //Se debe ejecutar dentro de una corrutina (como viewModelScope.launch) para no congelar la pantalla mientras consulta los dato
}