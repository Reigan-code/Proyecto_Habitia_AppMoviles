package upeu.edu.pe.Presentation.food

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import upeu.edu.pe.Domain.model.CatalogIngredient
import upeu.edu.pe.Domain.model.FoodCategory
import upeu.edu.pe.Domain.model.Meal
import upeu.edu.pe.Domain.model.ShoppingItem
import upeu.edu.pe.Domain.repository.IngredientCatalogRepository
import upeu.edu.pe.Domain.repository.MealRepository
import upeu.edu.pe.Domain.repository.ShoppingRepository

// FoodViewModel — comidas reales + compras reales + catálogo de ingredientes.
class FoodViewModel(
    private val repository: MealRepository,
    private val shoppingRepository: ShoppingRepository,
    private val catalogRepository: IngredientCatalogRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    val meals: StateFlow<List<Meal>> = repository.getMeals().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val shoppingItems: StateFlow<List<ShoppingItem>> =
        shoppingRepository.getItems().stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Catálogo agrupado por categoría de alimento (FoodCategory.ordered).
    val catalogByCategory: StateFlow<Map<FoodCategory, List<CatalogIngredient>>> =
        catalogRepository.getGroupedByCategory().stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    fun addCatalogIngredient(name: String, category: FoodCategory, unit: String = "unidades") {
        scope.launch { catalogRepository.add(name = name, category = category, unit = unit) }
    }

    fun deleteCatalogIngredient(id: String) {
        scope.launch { catalogRepository.delete(id) }
    }

    fun refreshShopping() {
        scope.launch { shoppingRepository.refresh() }
    }

    fun toggleShoppingItem(itemId: String) {
        scope.launch { shoppingRepository.toggleItem(itemId) }
    }

    fun addShoppingItem(name: String, quantity: String, category: String, date: String? = null) {
        scope.launch { shoppingRepository.addItem(name, quantity, category, date) }
    }

    fun addMeal(meal: Meal) {
        scope.launch { repository.addMeal(meal) }
    }

    fun updateMeal(meal: Meal) {
        scope.launch { repository.updateMeal(meal) }
    }

    fun deleteMeal(mealId: String) {
        scope.launch { repository.deleteMeal(mealId) }
    }
}
