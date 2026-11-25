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
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.CatalogWriteDto
import upeu.edu.pe.Domain.repository.TaskCategoryRepository

// Implementación REAL contra /Catalogs/categories (persistida en MySQL).
class ApiTaskCategoryRepository(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : TaskCategoryRepository {

    private val cache = MutableStateFlow<List<String>>(emptyList())

    // Guardamos también los ids para poder borrar por id.
    private var ids = mapOf<String, Long>()

    override fun getCategories(): Flow<List<String>> = cache.asStateFlow()

    suspend fun refresh() {
        val token = session.state.value.token ?: return
        try {
            // El endpoint devuelve solo nombres; para ids usamos un mapa local
            // construido al agregar. El seed se identifica por nombre.
            val r = httpClient.get("${ApiClient.getBaseUrl()}/Catalogs/categories") { bearerAuth(token) }
            if (!r.status.isSuccess()) return
            cache.value = r.body<List<String>>()
        } catch (e: Exception) {
            println("[Habitia-API] catalog categories refresh FALLÓ: ${e.message}")
        }
    }

    override suspend fun add(name: String) {
        val token = session.state.value.token ?: return
        try {
            val r = httpClient.post("${ApiClient.getBaseUrl()}/Catalogs/categories") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(CatalogWriteDto(name = name))
            }
            if (r.status.isSuccess()) cache.value = r.body<List<String>>()
        } catch (e: Exception) {
            println("[Habitia-API] category add FALLÓ: ${e.message}")
        }
    }

    override suspend fun delete(name: String) {
        val token = session.state.value.token ?: return
        try {
            val enc = java.net.URLEncoder.encode(name, "UTF-8")
            val r = httpClient.delete("${ApiClient.getBaseUrl()}/Catalogs/categories/by-name/$enc") { bearerAuth(token) }
            if (r.status.isSuccess()) cache.value = r.body<List<String>>()
        } catch (e: Exception) {
            println("[Habitia-API] category delete FALLÓ: ${e.message}")
        }
    }
}
