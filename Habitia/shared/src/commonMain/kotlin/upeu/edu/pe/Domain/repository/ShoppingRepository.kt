package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.ShoppingItem

// ═══════════════════════════════════════════════════════════════════════════
// ShoppingRepository — NUEVA interfaz de dominio para la pestaña "Compras"
// de FoodScreen. Antes los ítems vivían en un MutableStateFlow interno del
// FoodViewModel (mock duro en el código). Ahora el patrón es el mismo del
// resto: interfaz aquí, implementación Api (Ktor) en Data.
// ═══════════════════════════════════════════════════════════════════════════
interface ShoppingRepository {

    // getItems(): flow de la lista de compras del usuario (cache local).
    fun getItems(): Flow<List<ShoppingItem>>

    // refresh(): baja la lista desde /ShoppingLists (resolviendo el listId).
    suspend fun refresh()

    // toggleItem(): marca/desmarca comprado (PUT del item en el backend).
    suspend fun toggleItem(itemId: String)

    // addItem(): crea el ítem en la lista del usuario (POST /{listId}/items).
    suspend fun addItem(name: String, quantity: String, category: String, date: String? = null, sourceMealType: String? = null, sourceMealName: String? = null)
}
