package upeu.edu.pe.Data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
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
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.DiaryEntryCreateDto
import upeu.edu.pe.Data.remote.dto.DiaryEntryResponseDto
import upeu.edu.pe.Domain.model.DiaryEntry
import upeu.edu.pe.Domain.repository.DiaryRepository

class ApiDiaryRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : DiaryRepository {

    private val cache = MutableStateFlow<List<DiaryEntry>>(emptyList())

    override fun getEntries(): Flow<List<DiaryEntry>> = cache.asStateFlow()

    override suspend fun refresh() {
        val token = session.state.value.token ?: return
        try {
            val response = httpClient.get("${ApiClient.getBaseUrl()}/DiaryEntries") {
                bearerAuth(token)
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] diary refresh: HTTP ${response.status.value}")
                return
            }
            val dtos = response.body<List<DiaryEntryResponseDto>>()
            cache.update { dtos.map { DiaryEntry(id = "diary-${it.id}", date = it.entryDate, text = it.text) } }
        } catch (e: Exception) {
            println("[Habitia-API] diary refresh FALLÓ: ${e.message}")
        }
    }

    override suspend fun addEntry(date: String, text: String): DiaryEntry? {
        val token = session.state.value.token ?: return null
        return try {
            val response = httpClient.post("${ApiClient.getBaseUrl()}/DiaryEntries") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(DiaryEntryCreateDto(entryDate = date, text = text))
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] diary add: HTTP ${response.status.value}")
                return null
            }
            val dto = response.body<DiaryEntryResponseDto>()
            val entry = DiaryEntry(id = "diary-${dto.id}", date = dto.entryDate, text = dto.text)
            cache.update { it + entry }
            entry
        } catch (e: Exception) {
            println("[Habitia-API] diary add FALLÓ: ${e.message}")
            null
        }
    }

    override suspend fun updateEntry(id: String, text: String) {
        val token = session.state.value.token ?: return
        val backendId = id.removePrefix("diary-").toLongOrNull() ?: return
        val date = cache.value.find { it.id == id }?.date ?: return
        try {
            val response = httpClient.put("${ApiClient.getBaseUrl()}/DiaryEntries/$backendId") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(DiaryEntryCreateDto(entryDate = date, text = text))
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] diary update: HTTP ${response.status.value}")
                return
            }
            cache.update { list -> list.map { if (it.id == id) it.copy(text = text) else it } }
        } catch (e: Exception) {
            println("[Habitia-API] diary update FALLÓ: ${e.message}")
        }
    }

    override suspend fun deleteEntry(id: String) {
        val token = session.state.value.token ?: return
        val backendId = id.removePrefix("diary-").toLongOrNull() ?: return
        try {
            val response = httpClient.delete("${ApiClient.getBaseUrl()}/DiaryEntries/$backendId") {
                bearerAuth(token)
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] diary delete: HTTP ${response.status.value}")
                return
            }
            cache.update { it.filterNot { e -> e.id == id } }
        } catch (e: Exception) {
            println("[Habitia-API] diary delete FALLÓ: ${e.message}")
        }
    }
}
