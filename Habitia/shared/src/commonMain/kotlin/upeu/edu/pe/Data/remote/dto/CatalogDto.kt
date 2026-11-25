package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CatalogIngredientDto(
    val id: Long,
    val name: String,
    val category: String,
    val unit: String = "unidades"
)

@Serializable
data class CatalogWriteDto(val name: String, val category: String? = null, val unit: String? = null)
