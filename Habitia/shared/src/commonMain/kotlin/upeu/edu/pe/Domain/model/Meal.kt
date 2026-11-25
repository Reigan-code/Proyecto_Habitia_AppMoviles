package upeu.edu.pe.Domain.model


enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}

data class Meal(
    val id: String,
    val name: String,
    val type: MealType,
    val date: String,
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val mealIngredients: List<MealIngredient> = emptyList(),
    val youtubeQuery: String? = null,
    val youtubeUrl: String? = null,
    val image: String? = null
)