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
import upeu.edu.pe.Data.remote.dto.TaskResponseDto
import upeu.edu.pe.Data.remote.mapper.toCreateDto
import upeu.edu.pe.Data.remote.mapper.toDomain
import upeu.edu.pe.Data.remote.mapper.toUpdateDto
import upeu.edu.pe.Domain.model.Task
import upeu.edu.pe.Domain.repository.TaskRepository

// ═══════════════════════════════════════════════════════════════════════════
// ApiTaskRepositoryImpl — implementación REAL de TaskRepository contra el
// backend HABITIA-Back.
//
// ¿Por qué SÍ cambia la implementación y NO la interfaz?
//  - TaskRepository (Domain) ya define getTasks/completeTask/addTask como
//    Flow/suspend: los ViewModel y use cases (GetTodayTasksUseCase,
//    CompleteTaskUseCase...) siguen intactos. Solo cambia la "columna" Data,
//    patrón Clean identico al del booking-app.
//
// ¿Cómo funciona si el API no es un "stream" de datos?
//  - El mock devolvía una MutableStateFlow que cambiaba en memoria. Aquí la
//    REPLICA: cache es una MutableStateFlow<List<Task>>; refresh() pide al
//    backend, mapea y actualiza cache; addTask/completeTask actualizan el
//    servidor y al éxito reflejan/conservan la caché. getTasks() expone esa
//    caché como Flow → la UI reacciona igual que con los mocks.
//
// Campos SOLO LOCALES (category, dueTime, recurring): TaskResponseDto del
// backend no los tiene. Se guardan en el mapa localFields por id de tarea y
// se vuelven a aplicar al mapear el refresh, para que las pestañas de
// categorías y las horas de la agenda sigan funcionando en la sesión.
// ═══════════════════════════════════════════════════════════════════════════
class ApiTaskRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : TaskRepository {

    // Datos auxiliares locales de una tarea (no los conoce el backend).
    private data class LocalFields(
        val category: String,
        val dueTime: String? = null,
        val recurring: Boolean = false
    )

    // Caché observable: LO que la UI realmente lee.
    private val cache = MutableStateFlow<List<Task>>(emptyList())

    // Campos locales por id "task-<n>" (solo sobreviven a la sesión actual).
    private val localFields = mutableMapOf<String, LocalFields>()

    // -----------------------------------------------------------------------
    // getTasks(): interfaz de dominio — expone la caché como Flow igual que
    // TasksViewModel no necesita cambios: consume la misma interfaz.
    override fun getTasks(): Flow<List<Task>> = cache.asStateFlow()
    //        Nota: getTodayTasks() se dejaba igual en el mock (la UI filtra);
    //        aquí se replica la MISMA conducta para no cambiar ViewModels.
    override fun getTodayTasks(): Flow<List<Task>> = cache.asStateFlow()

    // -----------------------------------------------------------------------
    // refresh(): baja del backend las tareas del usuario logueado.
    //  - Si la sesión NO está lista (sin token o sin userId), no hace nada
    //    (p. ej. antes del login): la caché queda vacía y la UI lo muestra.
    //  - Éxito (200): mapea cada DTO a dominio aplicando sus campos locales.
    //    401 (token expirado): aviso por log — el JWT del profe dura 15 min
    //    (expiration-ms=900000), así que un 401 significa re-loguear.
    suspend fun refresh() {
        val snapshot = session.state.value
        val token = snapshot.token
        val userId = snapshot.userId
        if (token == null || userId == null) return // sesión no lista → nada que pedir

        try {
            // GET /Tasks/user/{userId} con Authorization: Bearer <token>
            val response = httpClient.get("${ApiClient.getBaseUrl()}/Tasks/user/$userId") {
                bearerAuth(token)
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] refresh tasks: HTTP ${response.status.value} (si es 401, el token expiró: inicia sesión otra vez)")
                return
            }
            val dtos = response.body<List<TaskResponseDto>>()
            // Mapea DTO→dominio conservando los campos que el backend no guarda.
            cache.update {
                dtos.map { dto ->
                    val key = localTaskId(dto.id)
                    val local = localFields[key]
                    dto.toDomain(
                        categoryBackup = local?.category ?: "Hogar",
                        dueTimeBackup  = local?.dueTime
                    ).copy(recurring = local?.recurring ?: false)
                }
            }
        } catch (e: Exception) {
            // Red caída, backend apagado, JSON inesperado… → no rompemos la UI.
            println("[Habitia-API] refresh tasks FALLÓ: ${e.message}")
        }
    }

    // -----------------------------------------------------------------------
    // completeTask(taskId): alterna el estado completado de una tarea.
    //  1) Lee la tarea de caché (el dominio define TOGGLE, no set).
    //  2) PUT /Tasks/{id} con task.toUpdateDto(status) — el backend exige el
    //     estado COMPLETO en la actualización (TaskUpdateDto), status flips
    //     entre PENDING y COMPLETED.
    //  3) Solo si el PUT responde bien se refleja en caché (evita "mentirle"
    //     a la UI con un cambio que el servidor rechazó).
    override suspend fun completeTask(taskId: String) {
        // TaskRepository completa la tarea señalada; aquí TOGGLE de completed.
        val task = cache.value.firstOrNull { it.id == taskId } ?: return
        val nuevoEstado = if (task.completed) "PENDING" else "COMPLETED"

        // Token de la sesión (si no lo hay, no hay nada que hacer).
        val token = session.state.value.token ?: return

        try {
            // PUT /Tasks/{id} (id numérico sin el prefijo "task-").
            val response = httpClient.put("${ApiClient.getBaseUrl()}/Tasks/${backendId(taskId)}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(task.toUpdateDto(status = nuevoEstado))
            }
            if (response.status.isSuccess()) {
                // Refleja en caché SOLO el cambio confirmado por el servidor.
                val completado = nuevoEstado == "COMPLETED"
                cache.update { lista ->
                    lista.map { if (it.id == taskId) it.copy(completed = completado) else it }
                }
            } else {
                println("[Habitia-API] completeTask: HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            println("[Habitia-API] completeTask FALLÓ: ${e.message}")
        }
    }

    // -----------------------------------------------------------------------
    // addTask(task): crea en el backend y agrega a caché la versión REAL
    // (con el id que el servidor asigna). Campos locales se conservan a mano.
    override suspend fun addTask(task: Task) {
        // Token de la sesión (si no lo hay, no hay nada que hacer).
        val token = session.state.value.token ?: return
        try {
            // POST /Tasks: el backend toma el dueño DEL JWT (no del body).
            val response = httpClient.post("${ApiClient.getBaseUrl()}/Tasks") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(task.toCreateDto())
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] addTask: HTTP ${response.status.value}")
                return
            }
            // El backend devuelve la tarea con su id numérico (201 Created).
            val dto = response.body<TaskResponseDto>()
            val domainId = localTaskId(dto.id)

            // Conserva los datos que solo existen en la app (categorias/ horas).
            localFields[domainId] = LocalFields(
                category = task.category,
                dueTime = task.dueTime,
                recurring = task.recurring
            )
            // Agrega la versión real del servidor a la caché (los mocks hacían
            // lo mismo: tasks.update { it + task }).
            cache.update { it + dto.toDomain(task.category, task.dueTime).copy(recurring = task.recurring) }
        } catch (e: Exception) {
            println("[Habitia-API] addTask FALLÓ: ${e.message}")
        }
    }

    // -----------------------------------------------------------------------
    // backendId(idDominio): "task-42" → 42 (el backend usa Long).
    // updateTask: PUT /Tasks/{id} con el TaskUpdateDto COMPLETO (el backend
    // lo exige). Lo usa el calendario para MOVER un posit a otro día
    // (dueDate nuevo) y para ediciones del dominio.
    override suspend fun updateTask(task: Task) {
        val token = session.state.value.token ?: return
        try {
            val status = if (task.completed) "COMPLETED" else "PENDING"
            val response = httpClient.put("${ApiClient.getBaseUrl()}/Tasks/${backendId(task.id)}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(task.toUpdateDto(status = status))
            }
            if (response.status.isSuccess()) {
                cache.update { lista ->
                    lista.map { if (it.id == task.id) task else it }
                }
            } else {
                println("[Habitia-API] updateTask: HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            println("[Habitia-API] updateTask FALLÓ: ${e.message}")
        }
    }

    // deleteTask: DELETE /Tasks/{id} + limpieza del cache local.
    override suspend fun deleteTask(taskId: String) {
        val token = session.state.value.token ?: return
        try {
            val response = httpClient.delete("${ApiClient.getBaseUrl()}/Tasks/${backendId(taskId)}") {
                bearerAuth(token)
            }
            if (response.status.isSuccess()) {
                cache.update { lista -> lista.filterNot { it.id == taskId } }
                localFields.remove(taskId)
            } else {
                println("[Habitia-API] deleteTask: HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            println("[Habitia-API] deleteTask FALLÓ: ${e.message}")
        }
    }

    private fun backendId(domainId: String): Long =
        domainId.removePrefix("task-").toLongOrNull() ?: -1L

    // localTaskId(idBackend): 42 → "task-42" (clave unica en localFields).
    private fun localTaskId(backendId: Long): String = "task-$backendId"
}
