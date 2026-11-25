package upeu.edu.pe.Data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.concurrent.Volatile

object ApiClient {

    // BASE_URL: se resuelve en tiempo de ejecución (resolveBaseUrl) para que la
    // app funcione en cualquier red (10.10.10.x, 192.168.100.x) y en el
    // emulador (10.0.2.2) sin recompilar.
    @Volatile
    private var BASE_URL: String = "http://10.10.10.100:8081/habitia-backend"

    private val CANDIDATE_URLS = listOf(
        "http://10.10.10.100:8081/habitia-backend",
        "http://192.168.100.210:8081/habitia-backend",
        "http://10.0.2.2:8081/habitia-backend",
        "http://localhost:8081/habitia-backend"
    )

    suspend fun resolveBaseUrl() {
        var found: String? = null
        for (url in CANDIDATE_URLS) {
            try {
                withTimeout(3000) {
                    httpClient.get(url)
                }
                found = url
                break
            } catch (e: Exception) {
                // no alcanzable, probar siguiente
            }
        }
        if (found != null) BASE_URL = found
        println("[Habitia-API] BASE_URL resuelta: $BASE_URL")
    }

    fun getBaseUrl() = BASE_URL

    // La key NO se versiona (GitHub Push Protection la bloquea).
    // El desarrollador la inyecta localmente antes de compilar.
    const val GOOGLE_CALENDAR_API_KEY = "TU_API_KEY_GOOGLE_CALENDAR"

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    // GEMINI (Haabi)
    const val GEMINI_API_KEY = "TU_API_KEY_GEMINI"

    @Serializable
    private data class GeminiPart(
        val text: String? = null,
        val functionCall: GeminiFunctionCall? = null
    )

    @Serializable
    private data class GeminiContent(
        val parts: List<GeminiPart> = emptyList(),
        val role: String? = null
    )

    @Serializable
    private data class GeminiRequest(
        val contents: List<GeminiContent>,
        val tools: List<GeminiTool>? = null
    )

    @Serializable
    private data class GeminiResponse(
        val candidates: List<GeminiCandidate>? = null
    )

    @Serializable
    private data class GeminiCandidate(
        val content: GeminiContent? = null
    )

    @Serializable
    private data class GeminiFunctionCall(
        val name: String,
        val args: JsonObject? = null
    )

    @Serializable
    private data class GeminiFunctionParameter(
        val type: String,
        val description: String,
        val items: GeminiFunctionParameter? = null,
        val properties: Map<String, GeminiFunctionParameter>? = null,
        val required: List<String>? = null,
        val enum: List<String>? = null
    )

    @Serializable
    private data class GeminiFunctionParameters(
        val type: String = "object",
        val properties: Map<String, GeminiFunctionParameter> = emptyMap(),
        val required: List<String> = emptyList()
    )

    @Serializable
    private data class GeminiFunctionDeclaration(
        val name: String,
        val description: String,
        val parameters: GeminiFunctionParameters
    )

    @Serializable
    private data class GeminiTool(
        val functionDeclarations: List<GeminiFunctionDeclaration>
    )

    private fun haabiTools(): List<GeminiTool> = listOf(
        GeminiTool(
            functionDeclarations = listOf(
                GeminiFunctionDeclaration(
                    name = "crear_tarea",
                    description = "Crea una tarea para el usuario",
                    parameters = GeminiFunctionParameters(
                        properties = mapOf(
                            "titulo" to GeminiFunctionParameter("string", "Titulo corto de la tarea"),
                            "descripcion" to GeminiFunctionParameter("string", "Detalle opcional"),
                            "categoria" to GeminiFunctionParameter("string", "Categoria (Trabajo, Personal, Salud, Estudio, Otro)"),
                            "prioridad" to GeminiFunctionParameter("string", "LOW, MEDIUM o HIGH"),
                            "fecha" to GeminiFunctionParameter("string", "Fecha limite ISO yyyy-MM-dd (opcional)")
                        ),
                        required = listOf("titulo")
                    )
                ),
                GeminiFunctionDeclaration(
                    name = "crear_habito",
                    description = "Crea un habito para el usuario",
                    parameters = GeminiFunctionParameters(
                        properties = mapOf(
                            "nombre" to GeminiFunctionParameter("string", "Nombre del habito"),
                            "frecuencia" to GeminiFunctionParameter("string", "DAILY, WEEKLY o MONTHLY")
                        ),
                        required = listOf("nombre")
                    )
                ),
                GeminiFunctionDeclaration(
                    name = "crear_recordatorio",
                    description = "Crea un recordatorio para el usuario",
                    parameters = GeminiFunctionParameters(
                        properties = mapOf(
                            "titulo" to GeminiFunctionParameter("string", "Titulo del recordatorio"),
                            "fecha" to GeminiFunctionParameter("string", "Fecha ISO yyyy-MM-dd (opcional)"),
                            "hora" to GeminiFunctionParameter("string", "Hora HH:mm (opcional)")
                        ),
                        required = listOf("titulo")
                    )
                ),
                GeminiFunctionDeclaration(
                    name = "crear_comida",
                    description = "Agrega una comida al plan semanal",
                    parameters = GeminiFunctionParameters(
                        properties = mapOf(
                            "nombre" to GeminiFunctionParameter("string", "Nombre del plato"),
                            "tipo" to GeminiFunctionParameter("string", "BREAKFAST, LUNCH, DINNER o SNACK"),
                            "fecha" to GeminiFunctionParameter("string", "Fecha ISO yyyy-MM-dd (opcional)"),
                            "ingredientes" to GeminiFunctionParameter("string", "Lista de ingredientes separados por coma (opcional)")
                        ),
                        required = listOf("nombre")
                    )
                ),
                GeminiFunctionDeclaration(
                    name = "crear_compra",
                    description = "Agrega un item a la lista de compras",
                    parameters = GeminiFunctionParameters(
                        properties = mapOf(
                            "nombre" to GeminiFunctionParameter("string", "Producto"),
                            "cantidad" to GeminiFunctionParameter("string", "Cantidad (ej. 2, 500 g)"),
                            "categoria" to GeminiFunctionParameter("string", "Categoria (Frutas, Limpieza, etc.)"),
                            "fecha" to GeminiFunctionParameter("string", "Fecha ISO yyyy-MM-dd para agrupar por dia (opcional)")
                        ),
                        required = listOf("nombre")
                    )
                ),
                GeminiFunctionDeclaration(
                    name = "crear_diario",
                    description = "Escribe una entrada en el diario",
                    parameters = GeminiFunctionParameters(
                        properties = mapOf(
                            "texto" to GeminiFunctionParameter("string", "Texto de la entrada")
                        ),
                        required = listOf("texto")
                    )
                ),
                GeminiFunctionDeclaration(
                    name = "crear_plan_semanal_comidas",
                    description = "Crea el menu completo con comidas, ingredientes categorizados, lista de compras y tarea. Pasa el plan como JSON con esta forma EXACTA: {\"dias\":[{\"fecha\":\"yyyy-MM-dd\",\"nombre\":\"Lunes\",\"comidas\":[{\"nombre\":\" Plato\",\"tipo\":\"BREAKFAST|LUNCH|DINNER\",\"youtubeQuery\":\"receta en espanol\",\"ingredientes\":[{\"nombre\":\"Avena\",\"cantidad\":\"50g\",\"categoria\":\"Granos\"}]}]}]}",
                    parameters = GeminiFunctionParameters(
                        properties = mapOf(
                            "plan" to GeminiFunctionParameter("string", "JSON del plan con la forma exacta descrita")
                        ),
                        required = listOf("plan")
                    )
                )
            )
        )
    )

    private suspend fun executeHaabiAction(
        actions: upeu.edu.pe.Domain.actions.HaabiActions,
        call: GeminiFunctionCall
    ): String {
        val args = call.args ?: emptyMap()
        fun str(key: String): String? =
            (args[key] as? kotlinx.serialization.json.JsonPrimitive)?.content
        return try {
            when (call.name) {
                "crear_tarea" -> actions.createTask(
                    title = str("titulo").orEmpty(),
                    description = str("descripcion"),
                    category = str("categoria"),
                    priority = str("prioridad"),
                    dueDate = str("fecha")
                )
                "crear_habito" -> actions.createHabit(
                    name = str("nombre").orEmpty(),
                    frequency = str("frecuencia")
                )
                "crear_recordatorio" -> actions.createReminder(
                    title = str("titulo").orEmpty(),
                    date = str("fecha"),
                    time = str("hora")
                )
                "crear_comida" -> actions.createMeal(
                    name = str("nombre").orEmpty(),
                    type = str("tipo"),
                    date = str("fecha"),
                    ingredients = str("ingredientes")?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }
                )
                "crear_compra" -> actions.createShoppingItem(
                    name = str("nombre").orEmpty(),
                    quantity = str("cantidad"),
                    category = str("categoria"),
                    date = str("fecha")
                )
                "crear_diario" -> actions.createDiaryEntry(str("texto").orEmpty())
                "crear_plan_semanal_comidas" -> actions.createWeeklyMenuPlan(str("plan").orEmpty())
                else -> "No reconozco la accion '${call.name}'."
            }
        } catch (e: Exception) {
            "No pude completar '${call.name}': ${e.message}"
        }
    }

    suspend fun haabiChat(
        userMessage: String,
        context: String = "",
        actions: upeu.edu.pe.Domain.actions.HaabiActions? = null,
        history: List<Pair<Boolean, String>> = emptyList(),
        persona: String = ""
    ): String {
        if (GEMINI_API_KEY.isBlank() || GEMINI_API_KEY.contains("TU_API_KEY")) {
            return "Hola! Soy Haabi. Para que pueda responder, configura tu API key de Gemini en ApiClient.GEMINI_API_KEY (gratis en aistudio.google.com)."
        }
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$GEMINI_API_KEY"
            val systemText = buildString {
                append("Eres Haabi, la mascota virtual de la app Habitia (habitos, tareas, bienestar). ")
                append("Responde en espanol, calido, breve (max 2 frases), con un emoji. ")
                append("No repitas lo que el usuario dijo ni parafrasees su mensaje; ve directo al punto. ")
                if (persona.isNotBlank()) append("Trata al usuario asi: $persona. ")
                if (actions != null) append("USO DE HERRAMIENTAS: si el usuario pide crear algo (tarea, habito, recordatorio, comida, compra, diario o menu), llama la herramienta correspondiente SIN escribir texto antes. Para fechas usa SIEMPRE yyyy-MM-dd (hoy=fecha de hoy del contexto, manana=+1 dia). ")
                if (context.isNotBlank()) append("Contexto del usuario hoy: $context. ")
            }
            val contents = mutableListOf<GeminiContent>()
            // Historial (memoria de la sesion): turnos previos user/model.
            for ((isUser, text) in history) {
                contents.add(GeminiContent(role = if (isUser) "user" else "model", parts = listOf(GeminiPart(text = text))))
            }
            contents.add(GeminiContent(role = "user", parts = listOf(GeminiPart(text = "$systemText\n\nUsuario: $userMessage"))))
            val request = GeminiRequest(
                contents = contents,
                tools = if (actions != null) haabiTools() else null
            )
            val body = withTimeout(30_000) {
                val response = httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
                response.body<GeminiResponse>()
            }
            val parts = body.candidates?.firstOrNull()?.content?.parts ?: emptyList()
            val calls = parts.mapNotNull { it.functionCall }
            val textPart = parts.firstNotNullOfOrNull { it.text }?.trim()?.takeIf { it.isNotBlank() }
            if (calls.isNotEmpty() && actions != null) {
                val results = mutableListOf<String>()
                for (call in calls) {
                    results.add(executeHaabiAction(actions, call))
                }
                val shortText = textPart?.takeIf { it.length <= 280 }
                return (if (shortText != null) "$shortText\n\n" else "") + results.joinToString("\n")
            }
            textPart ?: "No pude generar respuesta ahora."
        } catch (e: Exception) {
            println("[Habitia-Haabi] chat FALLO: ${e.message}")
            "Ups, tuve un problema al pensar. Intenta de nuevo."
        }
    }
}
