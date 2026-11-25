package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import upeu.edu.pe.Domain.model.FoodCategory
import upeu.edu.pe.Domain.model.CatalogIngredient

interface IngredientCatalogRepository {
    fun getAll(): Flow<List<CatalogIngredient>>
    fun getGroupedByCategory(): Flow<Map<FoodCategory, List<CatalogIngredient>>>
    suspend fun add(name: String, category: FoodCategory, unit: String = "unidades")
    suspend fun delete(id: String)
}
