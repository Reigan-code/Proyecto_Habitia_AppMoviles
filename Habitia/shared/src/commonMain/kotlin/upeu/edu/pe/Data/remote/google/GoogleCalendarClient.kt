package upeu.edu.pe.Data.remote.google

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import upeu.edu.pe.Data.remote.api.ApiClient

// ═══════════════════════════════════════════════════════════════════════════
// GoogleCalendarClient — lectura de calendarios PÚBLICOS de Google.
//
// Endpoint: GET https://www.googleapis.com/calendar/v3/calendars/{id}/events
//   ?key=API_KEY & timeMin & timeMax & singleEvents=true & orderBy=startTime
//
// Con API KEY (sin OAuth) solo se pueden leer calendarios COMPARTIDOS
// PÚBLICAMENTE. Para el calendario privado del usuario se necesitaría
// OAuth2 (mejora futura — así lo pide el plan: GoogleCalendarLinkScreen).
// No enviamos el JWT: esta API NO es la del backend HABITIA.
// ═══════════════════════════════════════════════════════════════════════════

// start/end del evento: dateTime para eventos con hora ("2026-11-16T19:00:00-05:00"),
// date para eventos de TODO el día ("2026-11-16").
@Serializable
data class GoogleTimeDto(
    val dateTime: String? = null,
    val date: String? = null
)

@Serializable
data class GoogleEventDto(
    val id: String,
    val summary: String? = null,        // título
    val description: String? = null,
    val location: String? = null,
    val start: GoogleTimeDto? = null,
    val end: GoogleTimeDto? = null
)

@Serializable
data class GoogleEventsResponseDto(
    val items: List<GoogleEventDto> = emptyList()
)

class GoogleCalendarClient(private val httpClient: HttpClient) {

    // fetchEvents(): baja los eventos del rango [timeMin, timeMax] (ISO).
    // Devuelve lista vacía ante cualquier fallo (calendario no público,
    // sin internet, calendarId inválido) — la UI solo muestra un aviso.
    suspend fun fetchEvents(
        calendarId: String,
        timeMinISO: String,
        timeMaxISO: String
    ): List<GoogleEventDto> {
        return try {
            val url = "https://www.googleapis.com/calendar/v3/calendars/" +
                    calendarId.encodeURLParameter() + "/events" +
                    "?key=${ApiClient.GOOGLE_CALENDAR_API_KEY}" +
                    "&timeMin=${timeMinISO.encodeURLParameter()}" +
                    "&timeMax=${timeMaxISO.encodeURLParameter()}" +
                    "&singleEvents=true&orderBy=startTime"
            val response = httpClient.get(url)
            if (!response.status.value.let { it in 200..299 }) {
                println("[Habitia-Google] fetchEvents: HTTP ${response.status.value}")
                // Log del cuerpo para diagnosticar (401 key inválida, 404 no público)
                val body = response.bodyAsText()
                println("[Habitia-Google] body: ${body.take(300)}")
                return emptyList()
            }
            response.body<GoogleEventsResponseDto>().items
        } catch (e: Exception) {
            println("[Habitia-Google] fetchEvents FALLÓ: ${e.message}")
            emptyList()
        }
    }
}

// parseGoogleDate(): "2026-11-16T19:00:00-05:00" → (fechaISO, hora "HH:mm").
// Para eventos all-day (solo date) la hora es "00:00". TOP-LEVEL para que
// la UI lo use sin instanciar el cliente.
fun parseGoogleDate(time: GoogleTimeDto?): Pair<String, String> {
    if (time == null) return "" to "00:00"
    val raw = time.dateTime ?: (time.date ?: "")
    val date = raw.take(10)
    val hora = if (raw.length >= 16 && time.dateTime != null) raw.substring(11, 16) else "00:00"
    return date to hora
}
