package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.Meal

interface MealRepository {

    fun getMeals(): Flow<List<Meal>>

    suspend fun getWeeklyMeals(): List<Meal>

    suspend fun addMeal(meal: Meal)

    suspend fun updateMeal(meal: Meal)

    suspend fun deleteMeal(mealId: String)
}
