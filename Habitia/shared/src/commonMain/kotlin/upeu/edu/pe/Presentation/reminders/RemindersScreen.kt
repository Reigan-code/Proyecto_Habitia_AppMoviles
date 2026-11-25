package upeu.edu.pe.Presentation.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.*
import upeu.edu.pe.Core.theme.habitiaAmbient
import upeu.edu.pe.Core.theme.habitiaGlass
import upeu.edu.pe.Domain.model.Reminder
import upeu.edu.pe.Domain.model.ReminderType

@Composable
fun RemindersScreen(viewModel: RemindersViewModel) {
    // Lee directamente de tu RemindersViewModel de disco
    val remindersList by viewModel.reminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // LIQUID GLASS: fuente de blur local: las filas de avisos difuminan el
    // fondo de la paleta activa al desplazarse por la lista.
    val hazeState = remember { HazeState() }

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ReminderType.NORMAL) }

    Box(modifier = Modifier.fillMaxSize()) {
        // ═══ CAPA FUENTE (hermana) — Haze 1.2.x excluye effects descendientes
        // del source (zIndex); como hermana el blur funciona de verdad.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        // ═══ CAPA CONTENIDO (incluidas las cards glass)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 24.dp, bottom = 176.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HabitiaSectionTitle(
                    title = "Avisos & Rutinas",
                    subtitle = "Sintoniza tus recordatorios importantes"
                )
            }

            if (remindersList.isEmpty()) {
                item {
                    HabitiaEmptyState(
                        icon = Icons.Default.NotificationsNone,
                        title = "No tienes avisos agendados",
                        subtitle = "Agrega alertas para hábitos, salud o momentos clave"
                    )
                }
            } else {
                items(remindersList) { reminder ->
                    ReminderRowItem(
                        reminder = reminder,
                        onCheckedChange = { viewModel.toggleReminder(reminder.id) },
                        hazeState = hazeState
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                // FIX VISIBILIDAD: bottom 24→104dp — el nav bar glass (80dp+12)
                // tapaba el FAB por completo (vidrio 95% opaco).
                .padding(bottom = 104.dp)
        ) {
            // FAB de vidrio: acción principal "Nuevo Recordatorio"
            HabitiaGlassFab(
                onClick = { showAddDialog = true },
                icon = Icons.Default.Add,
                contentDescription = "Nuevo Recordatorio"
            )
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(20.dp),
                // Diálogo translúcido (entorno glass) manteniendo legibilidad
                // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (surface)
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                title = {
                    Text(
                        text = "Nuevo Aviso",
                        style = MaterialTheme.typography.titleLarge,
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        fontWeight = FontWeight.Bold,
                        // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HabitiaTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = "Título del recordatorio",
                            leadingIcon = Icons.Default.Notifications
                        )
                        HabitiaTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = "Notas o detalles",
                            leadingIcon = Icons.Default.Notes
                        )
                        HabitiaTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = "Hora (ej. 14:00)",
                            leadingIcon = Icons.Default.Schedule
                        )

                        Text(
                            text = "Tipo de Aviso",
                            style = MaterialTheme.typography.labelLarge,
                            // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                            fontWeight = FontWeight.SemiBold,
                            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReminderType.entries.forEach { t ->
                                val active = type == t
                                val label = when (t) {
                                    ReminderType.NORMAL -> "Aviso"
                                    ReminderType.MEDICATION -> "Salud"
                                    ReminderType.RECURRING -> "Diario"
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        // Forma -> cambia radio 10.dp (mas alto=mas redondo)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { type = t }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                            color = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (title.isNotBlank() && time.isNotBlank()) {
                                viewModel.addReminder(title, desc, time, type)
                                title = ""
                                desc = ""
                                time = ""
                                type = ReminderType.NORMAL
                                showAddDialog = false
                            }
                        }
                    ) {
                        // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        Text("Crear", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        // Color del texto -> Core/theme/Color.kt (outline) o cambia color= aqui
                        Text("Cancelar", color = MaterialTheme.colorScheme.outline)
                    }
                }
            )
        }
        } // fin capa contenido
    } // fin raíz 2 capas
}

/**
 * Fila de aviso con superficie de vidrio (Liquid Glass).
 *
 */
@Composable
fun ReminderRowItem(
    reminder: Reminder,
    onCheckedChange: () -> Unit,
    hazeState: HazeState? = null
) {
    // Icono semántico según el tipo de aviso (salud / recurrente / normal)
    val icon = when (reminder.type) {
        ReminderType.MEDICATION -> Icons.Default.LocalHospital
        ReminderType.RECURRING -> Icons.Default.Repeat
        ReminderType.NORMAL -> Icons.Default.Notifications
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
            .habitiaGlass(
                hazeState = hazeState,
                // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(14.dp),
                // Completado → vidrio más tenue (jerarquía visual del estado)
                // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
                containerAlpha = if (reminder.completed) 0.30f else null
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = reminder.completed,
                onCheckedChange = { onCheckedChange() },
                colors = CheckboxDefaults.colors(
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    checkedColor = MaterialTheme.colorScheme.primary,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (reminder.completed) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (reminder.completed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                    )
                )
                if (reminder.description.isNotEmpty()) {
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                    tint = MaterialTheme.colorScheme.primary,
                    // Tamano -> cambia 18.dp aqui
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reminder.time,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
