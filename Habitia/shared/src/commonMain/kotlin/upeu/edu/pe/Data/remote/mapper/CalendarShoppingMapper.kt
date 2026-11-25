package upeu.edu.pe.Data.remote.mapper

import upeu.edu.pe.Data.remote.dto.CalendarEventDto
import upeu.edu.pe.Data.remote.dto.ShoppingItemResponseDto
import upeu.edu.pe.Domain.model.CalendarEvent
import upeu.edu.pe.Domain.model.ShoppingItem

// ═══════════════════════════════════════════════════════════════════════════
// CalendarMapper + ShoppingMapper — dominio ↔ DTOs del backend.
//
// CALENDAR: el backend guarda startDate/endDate LocalDateTime completos;
// el dominio parte en date + startTime + endTime ("HH:mm"). location local.
//
// SHOPPING: mismatches documentados — quantity BigDecimal vs String del
// dominio (se parsea el primer número; sin número → 1.0), unit → "UNIT"
// genérica, category → SOLO LOCAL (el backend no la modela).
// ═══════════════════════════════════════════════════════════════════════════

fun CalendarEventDto.toDomain(): CalendarEvent {
    val (date, startTime) = splitDateTime(startDate)
    val (_, endTime) = splitDateTime(endDate)
    return CalendarEvent(
        id = "event-$id",
        title = title,
        description = description,
        date = date,
        startTime = startTime,
        endTime = endTime
    )
}

// date + start/end ("HH:mm" o "HH:mm:ss") → ISO LocalDateTime completos.
fun calendarStartISO(date: String, startTime: String): String =
    joinDateTime(date, startTime)

fun calendarEndISO(date: String, endTime: String): String =
    joinDateTime(date, endTime)

// backendEventId(): "event-42" → 42.
fun backendEventId(domainId: String): Long =
    domainId.removePrefix("event-").toLongOrNull() ?: -1L

// ── SHOPPING ──

fun ShoppingItemResponseDto.toDomain(categoryBackup: String = "Otros"): ShoppingItem =
    ShoppingItem(
        id = "shop-$id",
        name = description,
        quantity = formatQuantity(quantity, unit),
        category = categoryBackup,
        completed = completed,
        date = itemDate,
        sourceDay = itemDate,
        sourceMealType = sourceMealType,
        sourceMealName = sourceMealName
    )

// formatQuantity(): BigDecimal+unit del backend → texto humano del dominio
// ("2.0 KG" → "2 KG"; UNIT se omite — la UI nunca lo mostraba así).
fun formatQuantity(quantity: Double, unit: String): String {
    val number = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else quantity.toString()
    return if (unit.equals("UNIT", true)) number else "$number ${unit.lowercase()}"
}

// parseQuantity(): texto del dominio ("500 gramos", "2") → Double para el
// backend. Toma el PRIMER número que encuentre; sin número → 1.0.
fun parseQuantity(quantityText: String): Double =
    Regex("""\d+([.,]\d+)?""").find(quantityText)?.value
        ?.replace(',', '.')?.toDoubleOrNull() ?: 1.0

// inferUnit(): deduce el unit del texto; sin unidad → "UNIT" (genérica).
fun inferUnit(quantityText: String): String {
    val t = quantityText.lowercase()
    return when {
        "kg" in t || "kilo" in t -> "KG"
        "g" in t && !("kg" in t) -> "G"
        "l" in t && !("ml" in t) -> "L"
        "ml" in t -> "ML"
        else -> "UNIT"
    }
}

// backendShoppingId(): "shop-42" → 42.
fun backendShoppingId(domainId: String): Long =
    domainId.removePrefix("shop-").toLongOrNull() ?: -1L
