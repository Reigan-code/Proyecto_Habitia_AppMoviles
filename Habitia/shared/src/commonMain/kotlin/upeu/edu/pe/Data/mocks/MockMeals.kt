package upeu.edu.pe.Data.mocks

import upeu.edu.pe.Domain.model.Meal
import upeu.edu.pe.Domain.model.MealType


object MockMeals {

    val meals = listOf(

        Meal(
            id = "meal-001",
            name = "Avena con plátano",
            type = MealType.BREAKFAST,
            date = "2026-09-07",
            description = "Avena cremosa con plátano y canela",
            ingredients = listOf(
                "Avena",
                "Plátano",
                "Leche",
                "Canela"
            )
        ),

        Meal(
            id = "meal-002",
            name = "Pollo con arroz",
            type = MealType.LUNCH,
            date = "2026-09-07",
            description = "Pollo a la plancha con arroz y ensalada",
            ingredients = listOf(
                "Pollo",
                "Arroz",
                "Tomate",
                "Lechuga"
            )
        ),

        Meal(
            id = "meal-003",
            name = "Yogur con frutas",
            type = MealType.SNACK,
            date = "2026-09-07",
            description = "Yogur natural con fruta fresca"
        ),

        Meal(
            id = "meal-004",
            name = "Tortilla de verduras",
            type = MealType.DINNER,
            date = "2026-09-07",
            description = "Tortilla ligera de verduras"
        )
    )
}