package upeu.edu.pe.Presentation.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import upeu.edu.pe.Core.theme.habitiaAmbient
import upeu.edu.pe.Core.theme.habitiaGlass
import upeu.edu.pe.Data.remote.google.GoogleEventDto
import upeu.edu.pe.Data.remote.google.parseGoogleDate
import upeu.edu.pe.Domain.model.CalendarEvent
import upeu.edu.pe.Domain.model.Task

// ═══════════════════════════════════════════════════════════════════════════
// PlannerScreen — CALENDARIO COMPLETO estilo Google Calendar + POSITS.
//
// Estructura (misma estética glass del sistema con habitiaGlass):
//   1. Cabecera: < MES AÑO > con navegación de meses.
//   2. GRID 7x6 de días: cada celda muestra el número y hasta 3 PUNTOS de
//      color (evento backend / tarea / google) — como los dots de Google.
//   3. Panel del día seleccionado: los posits (sticky notes) de ese día.
//      • Posit EVENTO (azul glass): tap → Eliminar / Mover a otro día.
//      • Posit TAREA (ámbar glass): tap → Completar / Mover / Eliminar.
//      • Posit GOOGLE (violeta): solo lectura.
//   4. Botón "+ Google" para conectar un calendario público (API key).
//   5. FAB para crear un evento en el día seleccionado.
// "Mover" abre un diálogo con los próximos 7 días (arrastre real queda
// documentado como mejora — en Compose multiplataforma el drag&drop
// cross-cell requiere long-press detector global).
// ═══════════════════════════════════════════════════════════════════════════

private val dayNames = listOf("L", "M", "X", "J", "V", "S", "D")
private val monthNames = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
)

@Composable
fun PlannerScreen(viewModel: PlannerViewModel) {
    val events by viewModel.events.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val googleEvents by viewModel.googleEvents.collectAsState()
    val googleStatus by viewModel.googleStatus.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val hoyISO = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()).toString() }

    val hazeState = remember { dev.chrisbanes.haze.HazeState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var showGoogleConnect by remember { mutableStateOf(false) }

    // Posit seleccionado para su diálogo de acciones
    var selectedEvent by remember { mutableStateOf<CalendarEvent?>(null) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showMoveDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // ══ CAPA FUENTE (hermana) ══ — patrón del fix Haze 1.2.x: el source
        // NO puede ser ancestro de los effects (zIndex los excluye). Como
        // hermana, el blur real funciona igual que en Home/Tasks/Focus.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = 104.dp), // espacio para la nav glass (80+12)
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── 1. CABECERA DEL MES ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
                }
                Text(
                    text = "${monthNames[selectedMonth - 1]} $selectedYear",
                    style = MaterialTheme.typography.titleLarge,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold,
                    // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = { viewModel.nextMonth() }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
                }
            }

            // ── 2. GRID DEL CALENDARIO (tarjeta glass completa) ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                    // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
                    .habitiaGlass(hazeState = hazeState, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // fila de nombres de días
                    Row(modifier = Modifier.fillMaxWidth()) {
                        dayNames.forEach { d ->
                            Text(
                                text = d,
                                style = MaterialTheme.typography.labelSmall,
                                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                fontWeight = FontWeight.Bold,
                                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))

                    // semanas: 6 filas × 7 columnas
                    val leadingBlanks = leadingBlanks(selectedYear, selectedMonth)
                    val daysInMonth = daysInMonth(selectedYear, selectedMonth)
                    val cells: List<Int?> = List(leadingBlanks) { null } + (1..daysInMonth).toList()
                    cells.chunked(7).forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            week.forEach { cell ->
                                Box(modifier = Modifier.weight(1f)) {
                                    if (cell == null) {
                                        Spacer(modifier = Modifier.aspectRatio(1f))
                                    } else {
                                        val iso = "%04d-%02d-%02d".format(selectedYear, selectedMonth, cell)
                                        val isToday = iso == hoyISO
                                        val isSelected = iso == selectedDay
                                        val dayEvents = events.filter { it.date == iso }.size
                                        val dayTasks = tasks.filter { it.dueDate == iso }.size
                                        val dayGoogle = googleEvents.count {
                                            parseGoogleDate(it.start).first == iso
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .padding(2.dp)
                                                .aspectRatio(0.95f)
                                                // Forma -> cambia radio 10.dp (mas alto=mas redondo)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    when {
                                                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                                        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
                                                    }
                                                )
                                                .clickable { viewModel.selectDay(iso) }
                                                .padding(3.dp)
                                        ) {
                                            Text(
                                                text = "$cell",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) {
                                                    MaterialTheme.colorScheme.onPrimary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurface
                                                }
                                            )
                                            // dots estilo Google Calendar
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                repeat(dayEvents.coerceAtMost(3)) {
                                                    // Color de fondo -> Core/theme/Color.kt (primary) o cambia aqui
                                                    // Tamano -> cambia 4.dp aqui
                                                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                                                }
                                                repeat(dayTasks.coerceAtMost(3)) {
                                                    // Color de fondo -> Core/theme/Color.kt (tertiary) o cambia aqui
                                                    // Tamano -> cambia 4.dp aqui
                                                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
                                                }
                                                if (dayGoogle > 0) {
                                                    // Color de fondo -> Core/theme/Color.kt (secondary) o cambia aqui
                                                    // Tamano -> cambia 4.dp aqui
                                                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // rellena la última semana corta
                            if (week.size < 7) {
                                repeat(7 - week.size) {
                                    Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                }
                            }
                        }
                    }
                }
            }

            // ── 3. CONEXIÓN GOOGLE CALENDAR ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (googleStatus != null) {
                    Text(
                        text = googleStatus!!,
                        style = MaterialTheme.typography.labelSmall,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        maxLines = 2
                    )
                }
                TextButton(onClick = { showGoogleConnect = !showGoogleConnect }) {
                    // Tamano -> cambia 18.dp aqui
                    Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (showGoogleConnect) "Ocultar" else "Conectar Google Calendar")
                }
            }
            if (showGoogleConnect) {
                GoogleConnectCard(
                    hazeState = hazeState,
                    onFetch = { calendarId ->
                        viewModel.fetchGoogle(calendarId)
                        showGoogleConnect = false
                    }
                )
            }

            // ── 4. POSITS DEL DÍA SELECCIONADO ──
            Text(
                text = "Posits del día seleccionado",
                style = MaterialTheme.typography.titleMedium,
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                fontWeight = FontWeight.Bold,
                // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                color = MaterialTheme.colorScheme.onBackground
            )

            if (events.none { it.date == selectedDay } &&
                tasks.none { it.dueDate == selectedDay } &&
                googleEvents.none { parseGoogleDate(it.start).first == selectedDay }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Forma -> cambia radio 16.dp (mas alto=mas redondo)
                        // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
                        .habitiaGlass(hazeState = hazeState, shape = RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "Este día está despejado. Toca el + para agendar algo.",
                        style = MaterialTheme.typography.bodyMedium,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    )
                }
            }

            // Posits de EVENTOS (azul)
            events.filter { it.date == selectedDay }.forEach { ev ->
                Posit(
                    title = ev.title,
                    subtitle = "${ev.startTime} – ${ev.endTime} · Evento",
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.primary,
                    hazeState = hazeState,
                    onClick = { selectedEvent = ev }
                )
            }

            // Posits de TAREAS (ámbar)
            tasks.filter { it.dueDate == selectedDay }.forEach { t ->
                Posit(
                    title = t.title,
                    subtitle = if (t.completed) "Tarea ✓ completada" else "Tarea · pendiente",
                    // Color del texto -> Core/theme/Color.kt (tertiary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.tertiary,
                    hazeState = hazeState,
                    onClick = { selectedTask = t }
                )
            }

            // Posits de GOOGLE (violeta, read-only)
            googleEvents
                .filter { parseGoogleDate(it.start).first == selectedDay }
                .forEach { g ->
                    val (gDate, gStart) = parseGoogleDate(g.start)
                    val (_, gEnd) = parseGoogleDate(g.end)
                    Posit(
                        title = g.summary ?: "(sin título)",
                        subtitle = "$gStart–$gEnd · Google Calendar",
                        // Color del texto -> Core/theme/Color.kt (secondary) o cambia color= aqui
                        color = MaterialTheme.colorScheme.secondary,
                        hazeState = hazeState,
                        onClick = { }
                    )
                }
        }

        // ── FAB: agregar evento en el día seleccionado ──
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 104.dp),
            // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
            // Color del contenedor -> Core/theme/Color.kt (primary)
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar evento")
        }
    }

    // ── DIÁLOGO: NUEVO EVENTO ──
    if (showAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newStart by remember { mutableStateOf("08:00") }
        var newEnd by remember { mutableStateOf("09:00") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            // Forma -> cambia radio 20.dp (mas alto=mas redondo)
            shape = RoundedCornerShape(20.dp),
            // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
            // Color del contenedor -> Core/theme/Color.kt (surface)
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            // Peso de letra -> FontWeight.Bold (cambialo aqui)
            title = { Text("Nuevo evento · $selectedDay", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Título") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newStart,
                            onValueChange = { newStart = it },
                            label = { Text("Inicio HH:mm") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newEnd,
                            onValueChange = { newEnd = it },
                            label = { Text("Fin HH:mm") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addEvent(newTitle.trim(), newStart, newEnd)
                        }
                        showAddDialog = false
                    }
                ) { Text("Agendar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // ── DIÁLOGO: ACCIONES DEL POSIT EVENTO ──
    if (selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { selectedEvent = null },
            // Forma -> cambia radio 20.dp (mas alto=mas redondo)
            shape = RoundedCornerShape(20.dp),
            // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
            // Color del contenedor -> Core/theme/Color.kt (surface)
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            // Peso de letra -> FontWeight.Bold (cambialo aqui)
            title = { Text(selectedEvent!!.title, fontWeight = FontWeight.Bold) },
            text = { Text("${selectedEvent!!.startTime} – ${selectedEvent!!.endTime}\n${selectedEvent!!.date}") },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        showMoveDialog = true
                    }) { Text("Mover") }
                    TextButton(onClick = {
                        viewModel.deleteEvent(selectedEvent!!.id)
                        selectedEvent = null
                    // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                    }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                    TextButton(onClick = { selectedEvent = null }) { Text("Cerrar") }
                }
            }
        )
    }

    // ── DIÁLOGO: ACCIONES DEL POSIT TAREA ──
    if (selectedTask != null) {
        AlertDialog(
            onDismissRequest = { selectedTask = null },
            // Forma -> cambia radio 20.dp (mas alto=mas redondo)
            shape = RoundedCornerShape(20.dp),
            // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
            // Color del contenedor -> Core/theme/Color.kt (surface)
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            // Peso de letra -> FontWeight.Bold (cambialo aqui)
            title = { Text(selectedTask!!.title, fontWeight = FontWeight.Bold) },
            text = { Text(if (selectedTask!!.completed) "Completada ✓" else "Pendiente · ${selectedTask!!.dueDate}") },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        viewModel.toggleTask(selectedTask!!.id)
                        selectedTask = null
                    }) { Text(if (selectedTask!!.completed) "Desmarcar" else "Completar") }
                    TextButton(onClick = { showMoveDialog = true }) { Text("Mover") }
                    TextButton(onClick = {
                        viewModel.deleteTask(selectedTask!!.id)
                        selectedTask = null
                    // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                    }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                }
            }
        )
    }

    // ── DIÁLOGO: MOVER POSIT A OTRO DÍA ──
    if (showMoveDialog) {
        val baseISO = selectedDay
        AlertDialog(
            onDismissRequest = { showMoveDialog = false; selectedEvent = null; selectedTask = null },
            // Forma -> cambia radio 20.dp (mas alto=mas redondo)
            shape = RoundedCornerShape(20.dp),
            // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
            // Color del contenedor -> Core/theme/Color.kt (surface)
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            // Peso de letra -> FontWeight.Bold (cambialo aqui)
            title = { Text("Mover a…", fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (0..7).forEach { offset ->
                        val target = addDays(baseISO, offset)
                        TextButton(onClick = {
                            selectedEvent?.let { viewModel.moveEvent(it.id, target) }
                            selectedTask?.let { viewModel.moveTask(it.id, target) }
                            showMoveDialog = false
                            selectedEvent = null
                            selectedTask = null
                        }) { Text(shortDayLabel(target)) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMoveDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Helpers de fechas (kotlinx-datetime; LocalDate sin java.time)
// ═══════════════════════════════════════════════════════════════════════════

// daysInMonth(): días del mes (2024 es bisiesto para referencia de feb 29).
private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    else -> if (isLeapYear(year)) 29 else 28
}

private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

// leadingBlanks(): cuántas celdas vacías ANTES del día 1. La semana empieza
// en LUNES (columnas L M X J V S D). kotlinx: dayOfWeek.ordinal 0=lunes ✓.
private fun leadingBlanks(year: Int, month: Int): Int {
    val first = kotlinx.datetime.LocalDate(year, month, 1)
    return first.dayOfWeek.ordinal
}

// addDays(iso, n): suma n días a una fecha ISO "yyyy-MM-dd".
private fun addDays(iso: String, n: Int): String {
    val d = kotlinx.datetime.LocalDate.parse(iso)
    val sum = d.plus(n, kotlinx.datetime.DateTimeUnit.DAY)
    return sum.toString()
}

// shortDayLabel(iso): "16 Nov" para los chips del diálogo de mover.
private fun shortDayLabel(iso: String): String {
    val d = kotlinx.datetime.LocalDate.parse(iso)
    val m = monthNames[d.monthNumber - 1].take(3)
    return "${d.dayOfMonth} $m"
}

// ═══════════════════════════════════════════════════════════════════════════
// Posit — sticky note glass con color por tipo (evento/tarea/google)
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun Posit(
    title: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    hazeState: HazeState?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
            .habitiaGlass(
                hazeState = hazeState,
                // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(14.dp),
                tint = color.copy(alpha = 0.16f),
                elevation = 2.dp
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "pin" del sticky note
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// GoogleConnectCard — input del calendarId + botón sincronizar
// ═══════════════════════════════════════════════════════════════════════════
@Composable
private fun GoogleConnectCard(
    hazeState: HazeState?,
    onFetch: (String) -> Unit
) {
    var calendarId by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Forma -> cambia radio 16.dp (mas alto=mas redondo)
            // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
            .habitiaGlass(hazeState = hazeState, shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Google Calendar (solo calendarios PÚBLICOS con API key; el privado exigiría OAuth2)",
                style = MaterialTheme.typography.bodySmall,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = calendarId,
                    onValueChange = { calendarId = it },
                    label = { Text("Calendar ID (ej. primary o un correo público)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { onFetch(calendarId) }) {
                    Text("Sync")
                }
            }
        }
    }
}
