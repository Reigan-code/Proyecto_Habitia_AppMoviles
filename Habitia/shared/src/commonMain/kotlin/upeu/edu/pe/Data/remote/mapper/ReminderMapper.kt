package upeu.edu.pe.Data.remote.mapper

import upeu.edu.pe.Data.remote.dto.ReminderResponseDto
import upeu.edu.pe.Domain.model.Reminder

// ═══════════════════════════════════════════════════════════════════════════
// ReminderMapper — dominio ↔ DTOs del backend.
//
// El backend guarda UNA fecha LocalDateTime (reminderDate); el dominio la
// parte en date ("yyyy-MM-dd") + time ("HH:mm"). repeat del backend
// (NONE/DAILY/WEEKLY) ↔ recurring Boolean del dominio. type (NORMAL/
// MEDICATION/RECURRING del dominio) es SOLO LOCAL: el backend no lo modela.
// ═══════════════════════════════════════════════════════════════════════════

// toDomain(): DTO → dominio, parte la fecha-hora y mapea repeat → recurring.
fun ReminderResponseDto.toDomain(typeBackup: ReminderTypeBackup = ReminderTypeBackup()): Reminder {
    val (date, time) = splitDateTime(reminderDate)
    return Reminder(
        id = "reminder-$id",
        title = title,
        description = description,
        time = time,
        date = date,
        recurring = repeat.equals("DAILY", true) || repeat.equals("WEEKLY", true),
        type = typeBackup.type,
        completed = completed
    )
}

// ReminderTypeBackup: conserva el tipo local (NORMAL/MEDICATION/RECURRING)
// que el backend no guarda; los recordatorios nuevos mantienen el suyo.
data class ReminderTypeBackup(val type: upeu.edu.pe.Domain.model.ReminderType =
    upeu.edu.pe.Domain.model.ReminderType.NORMAL)

// splitDateTime(): "2026-11-16T08:00:00" → ("2026-11-16", "08:00").
// Si el backend manda solo fecha (sin T), time queda vacío.
fun splitDateTime(isoDateTime: String): Pair<String, String> {
    val parts = isoDateTime.split('T')
    val date = parts.firstOrNull()?.take(10) ?: ""
    val time = parts.getOrNull(1)?.take(5) ?: "00:00"
    return date to time
}

// joinDateTime(): une date + time del dominio a ISO LocalDateTime.
// Acepta time "HH:mm" → agrega ":00" (el backend espera segundos).
fun joinDateTime(date: String, time: String?): String {
    val safeTime = time?.take(5) ?: "00:00"
    val withSeconds = if (safeTime.length == 5) "$safeTime:00" else safeTime
    return "${date.take(10)}T$withSeconds"
}

// backendReminderId(): "reminder-42" → 42.
fun backendReminderId(domainId: String): Long =
    domainId.removePrefix("reminder-").toLongOrNull() ?: -1L
