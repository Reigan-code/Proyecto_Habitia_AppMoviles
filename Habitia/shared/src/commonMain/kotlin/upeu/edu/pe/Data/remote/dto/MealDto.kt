package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════════
// DTOs de MEALS — espejo de MealsCreateDto / MealsResponseDto del backend.
// A DIFERENCIA de Habits, aquí el CreateDto SÍ pide userId explícito
// (campo Long): el repositorio lo llena desde SessionManager.
// date: ISO "yyyy-MM-dd"; time: ISO "HH:mm:ss" (el dominio lo guarda como
// String "HH:mm" — el mapper agrega ":00").
// type: "BREAKFAST" | "LUNCH" | "DINNER" | "SNACK" (MealType backend).
// ═══════════════════════════════════════════════════════════════════════════

// → POST /Meals
@Serializable
data class MealCreateDto(
    val userId: Long,
    val name: String,
    val type: String,
    val date: String,
    val time: String?,           // "HH:mm:ss" opcional
    val description: String,
    val completed: Boolean = false
)

// ← GET /Meals/user/{userId}, /user/{userId}/date/{date}, POST, PUT /Meals/{id}
@Serializable
data class MealResponseDto(
    val id: Long,
    val userId: Long,
    val name: String,
    val type: String,
    val date: String,
    val time: String?,           // "HH:mm:ss" (o null)
    val description: String,
    val completed: Boolean
)

// → PUT /Meals/{id}
@Serializable
data class MealUpdateDto(
    val userId: Long,
    val name: String,
    val type: String,
    val date: String,
    val time: String?,
    val description: String,
    val completed: Boolean
)
// ingredientes del backend (MealIngredients)
@Serializable
data class MealIngredientCreateDto(
    val description: String,
    val mealId: Long
)

@Serializable
data class MealIngredientResponseDto(
    val id: Long,
    val description: String,
    val mealId: Long,
    val quantity: Double? = null,
    val unit: String? = null
)