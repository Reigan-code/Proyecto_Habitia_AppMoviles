package upeu.edu.pe.Domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CatalogIngredient(
    val id: String = "",
    val name: String,
    val category: FoodCategory,
    val unit: String = "unidades"
)
