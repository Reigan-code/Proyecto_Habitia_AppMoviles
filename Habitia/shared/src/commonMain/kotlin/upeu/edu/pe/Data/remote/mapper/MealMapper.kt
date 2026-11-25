package upeu.edu.pe.Data.remote.mapper

import upeu.edu.pe.Data.remote.dto.MealResponseDto
import upeu.edu.pe.Domain.model.Meal
import upeu.edu.pe.Domain.model.MealType

// ═══════════════════════════════════════════════════════════════════════════
// MealMapper — dominio ↔ DTOs del backend.
// Campos SOLO LOCALES: ingredients (lista de strings) y image — el backend
// los modela en tablas separadas (/MealIngredients) con IDs propios; para
// este corte se preservan en la sesión del repositorio (patrón ApiTask).
// completed del backend se ignora en el dominio (no lo usa la UI de Meals).
// ═══════════════════════════════════════════════════════════════════════════

fun MealResponseDto.toDomain(
    ingredientsBackup: List<String> = emptyList()
): Meal = Meal(
    id = "meal-$id",
    name = name,
    type = parseMealType(type),
    date = date.take(10),
    description = description,
    ingredients = ingredientsBackup,
    image = null
)

fun parseMealType(value: String): MealType =
    runCatching { MealType.valueOf(value.uppercase()) }.getOrDefault(MealType.LUNCH)

// time del dominio ("HH:mm" o null) → "HH:mm:ss" que espera el backend.
fun domainTimeToBackend(time: String?): String? {
    if (time.isNullOrBlank()) return null
    return if (time.length == 5) "$time:00" else time
}

// backendMealId(): "meal-42" → 42.
fun backendMealId(domainId: String): Long =
    domainId.removePrefix("meal-").toLongOrNull() ?: -1L
