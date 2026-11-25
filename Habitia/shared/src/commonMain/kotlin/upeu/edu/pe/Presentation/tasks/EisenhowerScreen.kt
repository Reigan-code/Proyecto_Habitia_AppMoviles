package upeu.edu.pe.Presentation.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import upeu.edu.pe.Core.theme.habitiaAmbient
import upeu.edu.pe.Core.theme.habitiaGlass
import upeu.edu.pe.Domain.model.Task
import upeu.edu.pe.Domain.model.TaskPriority

// ═══════════════════════════════════════════════════════════════════════════
// Eisenhower + Scrum (US-04): DRAG & DROP real.
//
// MATRIZ EISENHOWER 2x2 — cada tarea se clasifica con sus datos REALES
// (urgente = dueDate <= mañana, importante = priority != LOW). Al ARRASTRAR
// un posit de un cuadrante a otro, se actualiza la tarea en el backend
// (priority + dueDate) vía updateTask — la matriz se re-clasifica al
// instante porque el flow de allTasks se re-emite.
//
// TABLERO KANBAN (Sprint) — columnas LOCALES (Backlog / En progreso / Hecho)
// agregables por el usuario, con drag&drop de tareas entre columnas y
// sincronización de completed con el backend (Hecho = completed true).
// Se ELIMINÓ el backlog local del proyecto (EPICs/Features/PBIs) — el
// tablero ahora es del USUARIO (sus tareas reales).
// ═══════════════════════════════════════════════════════════════════════════

// ── claves de cuadrante ──
private const val Q1 = "Q1" // Haz ya: urgente + importante
private const val Q2 = "Q2" // Planifica: importante, no urgente
private const val Q3 = "Q3" // Delega: urgente, no importante
private const val Q4 = "Q4" // Elimina: ni urgente ni importante

private fun classify(task: Task, hoy: kotlinx.datetime.LocalDate): String {
    val manana = hoy.plus(1, DateTimeUnit.DAY)
    val urgente = kotlinx.datetime.LocalDate.parse(task.dueDate) <= manana
    val importante = task.priority != TaskPriority.LOW
    return when {
        urgente && importante -> Q1
        !urgente && importante -> Q2
        urgente && !importante -> Q3
        else -> Q4
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// MATRIZ EISENHOWER CON DRAG & DROP
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun EisenhowerMatrix(
    tasks: List<Task>,
    hazeState: HazeState,
    onToggle: (String) -> Unit,
    onMove: (Task, String) -> Unit
) {
    val hoy = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val manana = hoy.plus(1, DateTimeUnit.DAY).toString()

    // estado de arrastre (a nivel de matriz para que el overlay flotante no
    // se recorte por los scroll containers internos de los cuadrantes).
    var draggingTask by remember { mutableStateOf<Task?>(null) }
    var dragPos by remember { mutableStateOf(Offset.Zero) }
    var rootWindowOrigin by remember { mutableStateOf(Offset.Zero) }
    val quadrantBounds = remember { mutableStateMapOf<String, androidx.compose.ui.geometry.Rect>() }
    val chipCenters = remember { mutableStateMapOf<String, Offset>() }

    // handlers de arrastre — se propagan a cada cuadrante/posit.
    val onDragStart: (Task) -> Unit = { t ->
        draggingTask = t
        dragPos = chipCenters[t.id] ?: Offset.Zero
    }
    val onDrag: (Offset) -> Unit = { delta -> dragPos += delta }
    val onDragEnd: (Task) -> Unit = { t ->
        val pos = dragPos
        val target = quadrantBounds.entries.firstOrNull { (_, r) -> r.contains(pos) }?.key
        if (target != null && target != classify(t, hoy)) {
            onMove(t, target)
        }
        draggingTask = null
    }

    // buckets por cuadrante (solo pendientes)
    val buckets = remember(tasks) {
        mapOf(
            Q1 to tasks.filter { !it.completed && classify(it, hoy) == Q1 },
            Q2 to tasks.filter { !it.completed && classify(it, hoy) == Q2 },
            Q3 to tasks.filter { !it.completed && classify(it, hoy) == Q3 },
            Q4 to tasks.filter { !it.completed && classify(it, hoy) == Q4 }
        )
    }
    val doneTasks = tasks.filter { it.completed }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootWindowOrigin = it.positionInWindow() }
    ) {
        // ══ CAPA FUENTE (hermana) para glass real ══
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
                .padding(bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Arrastra un posit de cuadrante a cuadrante (mantén presionado).",
                style = MaterialTheme.typography.labelSmall,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuadrantDrop(
                    key = Q1, title = "Q1 · Haz ya", hint = "Urgente + Importante",
                    // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.18f),
                    pin = MaterialTheme.colorScheme.error,
                    tasks = buckets[Q1]!!,
hazeState = hazeState,
                    onToggle = onToggle, draggingTask = draggingTask,
                    quadrantBounds = quadrantBounds, chipCenters = chipCenters,
                    onDragStart = onDragStart, onDrag = onDrag, onDragEnd = onDragEnd,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                QuadrantDrop(
                    key = Q2, title = "Q2 · Planifica", hint = "Importante, no urgente",
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    pin = MaterialTheme.colorScheme.primary,
                    tasks = buckets[Q2]!!,
hazeState = hazeState,
                    onToggle = onToggle, draggingTask = draggingTask,
                    quadrantBounds = quadrantBounds, chipCenters = chipCenters,
                    onDragStart = onDragStart, onDrag = onDrag, onDragEnd = onDragEnd,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuadrantDrop(
                    key = Q3, title = "Q3 · Delega", hint = "Urgente, no importante",
                    // Color del texto -> Core/theme/Color.kt (secondary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                    pin = MaterialTheme.colorScheme.secondary,
                    tasks = buckets[Q3]!!,
hazeState = hazeState,
                    onToggle = onToggle, draggingTask = draggingTask,
                    quadrantBounds = quadrantBounds, chipCenters = chipCenters,
                    onDragStart = onDragStart, onDrag = onDrag, onDragEnd = onDragEnd,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                QuadrantDrop(
                    key = Q4, title = "Q4 · Elimina", hint = "Ni urgente ni importante",
                    // Color del texto -> Core/theme/Color.kt (surfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    pin = MaterialTheme.colorScheme.onSurfaceVariant,
                    tasks = buckets[Q4]!!,
hazeState = hazeState,
                    onToggle = onToggle, draggingTask = draggingTask,
                    quadrantBounds = quadrantBounds, chipCenters = chipCenters,
                    onDragStart = onDragStart, onDrag = onDrag, onDragEnd = onDragEnd,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }

            // Done: tareas completadas (pipeline Scrum Done)
            QuadrantDrop(
                key = "DONE", title = "Done · Completadas", hint = "Pipeline: hechas en este sprint",
                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                pin = MaterialTheme.colorScheme.primary,
                tasks = doneTasks,
                hazeState = hazeState,
                onToggle = onToggle, draggingTask = draggingTask,
                quadrantBounds = quadrantBounds, chipCenters = chipCenters,
                onDragStart = onDragStart, onDrag = onDrag, onDragEnd = onDragEnd,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }

        // ══ OVERLAY FLOTANTE del posit arrastrado (no se recorta) ══
        draggingTask?.let { t ->
            val density = LocalDensity.current
            val localX = with(density) { (dragPos.x - rootWindowOrigin.x).toDp() }
            val localY = with(density) { (dragPos.y - rootWindowOrigin.y).toDp() }
            Box(
                modifier = Modifier
                    .offset(x = localX - 60.dp, y = localY - 20.dp)
                    .width(120.dp)
                    // Forma -> cambia radio 10.dp (mas alto=mas redondo)
                    .shadow(8.dp, RoundedCornerShape(10.dp))
                    // Forma -> cambia radio 10.dp (mas alto=mas redondo)
                    .clip(RoundedCornerShape(10.dp))
                    // Color de fondo -> Core/theme/Color.kt (surface) o cambia aqui
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(8.dp)
            ) {
                Text(
                    text = t.title,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuadrantDrop(
    key: String,
    title: String,
    hint: String,
    color: Color,
    pin: Color,
    tasks: List<Task>,
    hazeState: HazeState,
    onToggle: (String) -> Unit,
    draggingTask: Task?,
    quadrantBounds: MutableMap<String, androidx.compose.ui.geometry.Rect>,
    chipCenters: MutableMap<String, Offset>,
    onDragStart: (Task) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
            .habitiaGlass(
                hazeState = hazeState,
                // Forma -> cambia radio 18.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(18.dp),
                tint = color.copy(alpha = 1f),
                elevation = 2.dp
            )
            .onGloballyPositioned { quadrantBounds[key] = it.boundsInRoot() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Tamano -> cambia 8.dp aqui
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(pin))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold,
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "$hint · ${tasks.size}",
                style = MaterialTheme.typography.labelSmall,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (tasks.isEmpty()) {
                    Text(
                        text = "Vacío",
                        style = MaterialTheme.typography.bodySmall,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                tasks.forEach { task ->
                    val isDragging = draggingTask?.id == task.id
                    TaskChip(
                        task = task,
                        pin = pin,
                        isDragging = isDragging,
                        onToggle = onToggle,
                        chipCenters = chipCenters,
                        onDragStart = { onDragStart(task) },
                        onDrag = onDrag,
                        onDragEnd = { onDragEnd(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskChip(
    task: Task,
    pin: Color,
    isDragging: Boolean,
    onToggle: (String) -> Unit,
    chipCenters: MutableMap<String, Offset>,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    // pointerInput vive aquí para capturar el arrastre (long-press).
    // El estado real de drag lo maneja el padre (EisenhowerMatrix) vía
    // los callbacks; aquí solo reportamos deltas. Para simplificar, el
    // arrastre se gestiona con estado compartido por el padre a través
    // de un CompositionLocal implícito NO — lo pasamos por parámetros.
    // En su lugar usamos el patrón: detectar aquí y llamar callbacks.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (isDragging) 0.35f else 1f }
            // Forma -> cambia radio 10.dp (mas alto=mas redondo)
            .clip(RoundedCornerShape(10.dp))
            // Color de fondo -> Core/theme/Color.kt (surface) o cambia aqui
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
            .onGloballyPositioned { chipCenters[task.id] = it.boundsInRoot().center }
            .pointerInput(task.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount -> onDrag(dragAmount) },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            }
            .clickable { onToggle(task.id) }
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (task.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = pin,
                // Tamano -> cambia 16.dp aqui
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Column {
                Text(
                    text = task.title,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${task.dueDate} · ${task.priority.name}",
                    // Tipografia -> cambia fontSize = 10.sp o usa MaterialTheme.typography
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// TABLERO KANBAN (Sprint) — columnas locales agregables + drag entre ellas
// ═══════════════════════════════════════════════════════════════════════════
@Composable
fun SprintBoard(
    tasks: List<Task>,
    hazeState: HazeState,
    onTaskStatusChanged: (Task, String) -> Unit
) {
    // columnas locales (el backend no modela sprints/columnas)
    val columns = remember { mutableStateOf(listOf("Backlog", "En progreso", "Hecho")) }
    val taskColumn = remember { mutableStateMapOf<String, String>() }
    var showAddColumn by remember { mutableStateOf(false) }
    var newColumnName by remember { mutableStateOf("") }

    // estado de arrastre (a nivel de tablero)
    var draggingTask by remember { mutableStateOf<Task?>(null) }
    var dragPos by remember { mutableStateOf(Offset.Zero) }
    var rootWindowOrigin by remember { mutableStateOf(Offset.Zero) }
    val columnBounds = remember { mutableStateMapOf<String, androidx.compose.ui.geometry.Rect>() }
    val chipCenters = remember { mutableStateMapOf<String, Offset>() }

    // helpers (declarados ANTES de los handlers que los usan)
    fun defaultColumn(t: Task): String = if (t.completed) "Hecho" else "Backlog"
    fun columnOf(t: Task): String = taskColumn[t.id] ?: defaultColumn(t)

    val onDragStart: (Task) -> Unit = { t ->
        draggingTask = t
        dragPos = chipCenters[t.id] ?: Offset.Zero
    }
    val onDrag: (Offset) -> Unit = { delta -> dragPos += delta }
    val onDragEnd: (Task) -> Unit = { t ->
        val pos = dragPos
        val target = columnBounds.entries.firstOrNull { (_, r) -> r.contains(pos) }?.key
        if (target != null && target != columnOf(t)) {
            taskColumn[t.id] = target
            // sincroniza completed con el backend: Hecho = completed true
            val nowCompleted = (target == "Hecho")
            if (t.completed != nowCompleted) {
                onTaskStatusChanged(t, target)
            }
        }
        draggingTask = null
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootWindowOrigin = it.positionInWindow() }
    ) {
        // capa fuente hermana
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
                .padding(bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tablero Scrum: arrastra una tarea entre columnas (mantén).",
                    style = MaterialTheme.typography.labelSmall,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { showAddColumn = true }) {
                    // Tamano -> cambia 16.dp aqui
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Columna")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                columns.value.forEach { colName ->
                    ColumnCard(
                        name = colName,
                        tasks = tasks.filter { columnOf(it) == colName },
                        hazeState = hazeState,
                        draggingTask = draggingTask,
                        columnBounds = columnBounds,
                        chipCenters = chipCenters,
                        onDragStart = onDragStart, onDrag = onDrag, onDragEnd = onDragEnd,
                        onToggle = { /* completar se maneja al soltar en "Hecho" */ },
                        modifier = Modifier.width(240.dp)
                    )
                }
            }
        }

        // overlay flotante del posit arrastrado
        draggingTask?.let { t ->
            val density = LocalDensity.current
            val localX = with(density) { (dragPos.x - rootWindowOrigin.x).toDp() }
            val localY = with(density) { (dragPos.y - rootWindowOrigin.y).toDp() }
            Box(
                modifier = Modifier
                    .offset(x = localX - 60.dp, y = localY - 20.dp)
                    .width(120.dp)
                    // Forma -> cambia radio 10.dp (mas alto=mas redondo)
                    .shadow(8.dp, RoundedCornerShape(10.dp))
                    // Forma -> cambia radio 10.dp (mas alto=mas redondo)
                    .clip(RoundedCornerShape(10.dp))
                    // Color de fondo -> Core/theme/Color.kt (surface) o cambia aqui
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(8.dp)
            ) {
                Text(
                    text = t.title,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    // dialogo: nueva columna
    if (showAddColumn) {
        AlertDialog(
            onDismissRequest = { showAddColumn = false },
            // Forma -> cambia radio 20.dp (mas alto=mas redondo)
            shape = RoundedCornerShape(20.dp),
            // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
            // Color del contenedor -> Core/theme/Color.kt (surface)
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            // Peso de letra -> FontWeight.Bold (cambialo aqui)
            title = { Text("Nueva columna", fontWeight = FontWeight.Bold) },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = newColumnName,
                    onValueChange = { newColumnName = it },
                    label = { Text("Nombre (ej. Review)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val name = newColumnName.trim()
                    if (name.isNotEmpty() && name !in columns.value) {
                        columns.value = columns.value + name
                    }
                    newColumnName = ""
                    showAddColumn = false
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showAddColumn = false; newColumnName = "" }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ColumnCard(
    name: String,
    tasks: List<Task>,
    hazeState: HazeState?,
    draggingTask: Task?,
    columnBounds: MutableMap<String, androidx.compose.ui.geometry.Rect>,
    chipCenters: MutableMap<String, Offset>,
    onDragStart: (Task) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (Task) -> Unit,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
            .habitiaGlass(
                hazeState = hazeState,
                // Forma -> cambia radio 18.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(18.dp),
                elevation = 2.dp
            )
            .onGloballyPositioned { columnBounds[name] = it.boundsInRoot() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold,
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${tasks.size}",
                    style = MaterialTheme.typography.labelMedium,
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.primary,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (tasks.isEmpty()) {
                    Text(
                        text = "Vacío",
                        style = MaterialTheme.typography.bodySmall,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                tasks.forEach { task ->
                    val isDragging = draggingTask?.id == task.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = if (isDragging) 0.35f else 1f }
                            // Forma -> cambia radio 10.dp (mas alto=mas redondo)
                            .clip(RoundedCornerShape(10.dp))
                            // Color de fondo -> Core/theme/Color.kt (surface) o cambia aqui
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
                            .onGloballyPositioned { chipCenters[task.id] = it.boundsInRoot().center }
                            .pointerInput(task.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { onDragStart(task) },
                                    onDrag = { change, dragAmount -> onDrag(dragAmount) },
                                    onDragEnd = { onDragEnd(task) },
                                    onDragCancel = { onDragEnd(task) }
                                )
                            }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (task.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                tint = MaterialTheme.colorScheme.primary,
                                // Tamano -> cambia 16.dp aqui
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = task.title,
                                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
