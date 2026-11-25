package upeu.edu.pe.Data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.CatalogIngredientDto
import upeu.edu.pe.Data.remote.dto.CatalogWriteDto
import upeu.edu.pe.Domain.model.CatalogIngredient
import upeu.edu.pe.Domain.model.FoodCategory
import upeu.edu.pe.Domain.repository.IngredientCatalogRepository

// Implementación REAL contra /Catalogs/ingredients (persistida en MySQL).
class ApiIngredientCatalogRepository(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : IngredientCatalogRepository {

    private val cache = MutableStateFlow<List<CatalogIngredient>>(emptyList())

    override fun getAll(): Flow<List<CatalogIngredient>> = cache.asStateFlow()

    override fun getGroupedByCategory(): Flow<Map<FoodCategory, List<CatalogIngredient>>> =
        cache.map { list -> list.groupBy { it.category } }

    suspend fun refresh() {
        val token = session.state.value.token ?: return
        try {
            val r = httpClient.get("${ApiClient.getBaseUrl()}/Catalogs/ingredients") { bearerAuth(token) }
            if (!r.status.isSuccess()) return
            cache.value = r.body<List<CatalogIngredientDto>>().map {
                CatalogIngredient(id = it.id.toString(), name = it.name,
                    category = FoodCategory.fromLabel(it.category), unit = it.unit)
            }
        } catch (e: Exception) {
            println("[Habitia-API] catalog ingredients refresh FALLÓ: ${e.message}")
        }
    }

    override suspend fun add(name: String, category: FoodCategory, unit: String) {
        val token = session.state.value.token ?: return
        try {
            val r = httpClient.post("${ApiClient.getBaseUrl()}/Catalogs/ingredients") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(CatalogWriteDto(name, category.label, unit))
            }
            if (r.status.isSuccess()) refresh()
        } catch (e: Exception) {
            println("[Habitia-API] catalog add FALLÓ: ${e.message}")
        }
    }

    override suspend fun delete(id: String) {
        val token = session.state.value.token ?: return
        try {
            val r = httpClient.delete("${ApiClient.getBaseUrl()}/Catalogs/ingredients/$id") { bearerAuth(token) }
            if (r.status.isSuccess()) refresh()
        } catch (e: Exception) {
            println("[Habitia-API] catalog delete FALLÓ: ${e.message}")
        }
    }
}
