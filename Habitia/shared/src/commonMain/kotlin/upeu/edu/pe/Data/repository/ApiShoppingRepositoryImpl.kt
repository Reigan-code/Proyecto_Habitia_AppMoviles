package upeu.edu.pe.Data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.GenerateShoppingListRequestDto
import upeu.edu.pe.Data.remote.dto.ShoppingItemCreateDto
import upeu.edu.pe.Data.remote.dto.ShoppingItemResponseDto
import upeu.edu.pe.Data.remote.dto.ShoppingItemUpdateDto
import upeu.edu.pe.Data.remote.dto.ShoppingListResponseDto
import upeu.edu.pe.Data.remote.mapper.backendShoppingId
import upeu.edu.pe.Data.remote.mapper.toDomain
import upeu.edu.pe.Domain.model.ShoppingItem
import upeu.edu.pe.Domain.repository.ShoppingRepository

// ═══════════════════════════════════════════════════════════════════════════
// ApiShoppingRepositoryImpl — lista de compras REAL (/ShoppingLists).
//
// El backend organiza las compras en UNA lista POR USUARIO:
//   1) ensureShoppingList(): GET /ShoppingLists/user/{userId} → toma la
//      primera lista; si NO existe → POST /ShoppingLists/generate la crea.
//   2) Los items viven en /ShoppingLists/{listId}/items.
//
// MISMAJS DOMAIN↔API (documentado): quantity del dominio es TEXTO ("2 kg")
// y el backend usa BigDecimal+enum unit → mapper parsea/infiere;
// category del dominio NO existe en el backend → solo local (por sesión).
// ═══════════════════════════════════════════════════════════════════════════
class ApiShoppingRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : ShoppingRepository {

    private val cache = MutableStateFlow<List<ShoppingItem>>(emptyList())
    private val categoriesByItem = mutableMapOf<String, String>()

    // listId resuelto en memoria (create-on-demand con ensureShoppingList).
    private var shoppingListId: Long? = null

    override fun getItems(): Flow<List<ShoppingItem>> = cache.asStateFlow()

    override suspend fun refresh() {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        val listId = ensureShoppingList(token, userId) ?: return
        try {
            val response = httpClient.get("${ApiClient.getBaseUrl()}/ShoppingLists/$listId") {
                bearerAuth(token)
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] shopping refresh: HTTP ${response.status.value}")
                return
            }
            val list = response.body<ShoppingListResponseDto>()
            cache.update { lista ->
                list.items.map { dto ->
                    val domainId = "shop-${dto.id}"
                    dto.toDomain(categoriesByItem[domainId] ?: "Otros")
                }
            }
        } catch (e: Exception) {
            println("[Habitia-API] shopping refresh FALLÓ: ${e.message}")
        }
    }

    override suspend fun toggleItem(itemId: String) {
        val s = session.state.value
        val token = s.token ?: return
        val listId = shoppingListId ?: return
        val item = cache.value.firstOrNull { it.id == itemId } ?: return
        try {
            val response = httpClient.put(
                "${ApiClient.getBaseUrl()}/ShoppingLists/$listId/items/${backendShoppingId(itemId)}"
            ) {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                // El UpdateDto del backend SOLO acepta description + completed
                // (la cantidad/unidad no son editables por este endpoint).
                setBody(
                    ShoppingItemUpdateDto(
                        description = item.name,
                        completed = !item.completed
                    )
                )
            }
            if (response.status.isSuccess()) {
                cache.update { lista ->
                    lista.map {
                        if (it.id == itemId) it.copy(completed = !it.completed) else it
                    }
                }
            }
        } catch (e: Exception) {
            println("[Habitia-API] toggleItem FALLÓ: ${e.message}")
        }
    }

    override suspend fun addItem(name: String, quantity: String, category: String, date: String?, sourceMealType: String?, sourceMealName: String?) {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        val listId = ensureShoppingList(token, userId) ?: return
        try {
            val response = httpClient.post("${ApiClient.getBaseUrl()}/ShoppingLists/$listId/items") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(ShoppingItemCreateDto(description = name, itemDate = date, sourceMealType = sourceMealType, sourceMealName = sourceMealName))
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] addItem: HTTP ${response.status.value}")
                return
            }
            val dto = response.body<ShoppingItemResponseDto>()
            val domainId = "shop-${dto.id}"
            categoriesByItem[domainId] = category
            cache.update { it + dto.toDomain(category) }
        } catch (e: Exception) {
            println("[Habitia-API] addItem FALLÓ: ${e.message}")
        }
    }

    // ensureShoppingList(): la lista del usuario (create-on-demand).
    private suspend fun ensureShoppingList(token: String, userId: Long): Long? {
        shoppingListId?.let { return it }
        try {
            val lists = httpClient.get("${ApiClient.getBaseUrl()}/ShoppingLists/user/$userId") {
                bearerAuth(token)
            }
            if (lists.status.isSuccess()) {
                val body = lists.body<List<ShoppingListResponseDto>>()
                if (body.isNotEmpty()) {
                    shoppingListId = body.first().id
                    return shoppingListId
                }
            }
            // Sin lista → generate (el endpoint del backend la crea a partir
            // de las meals de la semana: HOY → HOY+7 días, sin hardcode).
            val hoy = kotlinx.datetime.Clock.System.todayIn(
                kotlinx.datetime.TimeZone.currentSystemDefault()
            )
            val finSemana = hoy.plus(7, kotlinx.datetime.DateTimeUnit.DAY)
            val created = httpClient.post("${ApiClient.getBaseUrl()}/ShoppingLists/generate") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    GenerateShoppingListRequestDto(
                        userId = userId,
                        startDate = hoy.toString(),
                        endDate = finSemana.toString()
                    )
                )
            }
            if (created.status.isSuccess()) {
                val createdList = created.body<ShoppingListResponseDto>()
                shoppingListId = createdList.id
                return shoppingListId
            }
        } catch (e: Exception) {
            println("[Habitia-API] ensureShoppingList FALLÓ: ${e.message}")
        }
        return null
    }
}
