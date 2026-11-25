package upeu.edu.pe.Domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MealIngredient(
    val name: String,
    val quantity: String = "",
    val category: FoodCategory = FoodCategory.OTROS
)
