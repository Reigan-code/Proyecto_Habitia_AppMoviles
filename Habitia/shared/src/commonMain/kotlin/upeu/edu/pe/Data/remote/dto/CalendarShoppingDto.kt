package upeu.edu.pe.Data.remote.dto

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════════
// DTOs de CALENDAR EVENTS + SHOPPING.
//
// IMPORTANTE CalendarEvents: el backend devuelve la ENTIDAD directamente
// (sin DTO): {id, title, description, startDate, endDate, location, user:{...}}.
// Con ignoreUnknownKeys=true ignoramos el objeto user anidado sin problema.
// startDate/endDate son LocalDateTime → el dominio los parte en
// date + startTime + endTime.
//
// SHOPPING: el backend modela quantity como BigDecimal y unit como enum
// IngredientUnit (G,KG,L,ML,UNIT) — MUY distinto al quantity String del
// dominio ("1 paquete"). El mapper parsea el número y usa UNIT genérica;
// category es LOCAL (el backend no la tiene).
// ═══════════════════════════════════════════════════════════════════════════

// ← GET /CalendarEvents/user/{userId}, POST, PUT /CalendarEvents/{id}
// (la entidad serializada; 'user' anidado lo ignora el Json configurado)
@Serializable
data class CalendarEventDto(
    val id: Long,
    val title: String,
    val description: String,
    val startDate: String,       // ISO "yyyy-MM-ddTHH:mm:ss"
    val endDate: String,         // ISO "yyyy-MM-ddTHH:mm:ss"
    val location: String? = null
)

// → POST /CalendarEvents y PUT /CalendarEvents/{id} (shape del controlador:
//   recibe la ENTIDAD vía body y NO resuelve el usuario del JWT — hay que
//   mandar user:{id} explícito o el save explota con user_id NOT NULL).
@Serializable
data class CalendarEventUserDto(val id: Long)

@Serializable
data class CalendarEventRequestDto(
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val location: String? = null,
    val user: CalendarEventUserDto
)

// ← GET /ShoppingLists/user/{userId}, POST /{listId}/items...
// (id + startDate + endDate + status + items; SIN userId — el backend lo
// resuelve del JWT. status: PENDING | CONFIRMED, etc.)
@Serializable
data class ShoppingListResponseDto(
    val id: Long,
    val items: List<ShoppingItemResponseDto> = emptyList()
)

// → POST /ShoppingLists/{listId}/items — el backend SOLO acepta
//   description (≤255): la cantidad/unidad los deduce del ingrediente
//   generado (o quedan default).
@Serializable
data class ShoppingItemCreateDto(
    val description: String,      // (el "name" del dominio)
    val itemDate: String? = null,  // ISO yyyy-MM-dd (opcional)
    val sourceMealType: String? = null,
    val sourceMealName: String? = null
)

// dentro de ShoppingListResponseDto.items
@Serializable
data class ShoppingItemResponseDto(
    val id: Long,
    val description: String,
    val quantity: Double = 1.0,
    val unit: String = "UNIT",
    val source: String? = null,
    val completed: Boolean = false,
    val itemDate: String? = null,
    val sourceMealType: String? = null,
    val sourceMealName: String? = null
)

// → PUT /ShoppingLists/{listId}/items/{itemId} — SOLO description +
//   completed (el backend no permite cambiar cantidad por este endpoint).
@Serializable
data class ShoppingItemUpdateDto(
    val description: String,
    val completed: Boolean
)

// → POST /ShoppingLists/generate — el backend genera los items desde las
//   meals ENTRE dos fechas (@NotNull userId + startDate + endDate); mandamos
//   la ventana que decida el repositorio (HOY → HOY+7).
@Serializable
data class GenerateShoppingListRequestDto(
    val userId: Long,
    val startDate: String,
    val endDate: String
)
