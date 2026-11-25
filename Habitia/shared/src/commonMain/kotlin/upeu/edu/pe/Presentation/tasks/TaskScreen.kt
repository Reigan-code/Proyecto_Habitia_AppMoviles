package upeu.edu.pe.Presentation.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import upeu.edu.pe.Domain.model.Task
import upeu.edu.pe.Domain.model.TaskPriority

@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showAddCategory by remember { mutableStateOf(false) }
    var newCategory by remember { mutableStateOf("") }


    // dibuja en el Box raíz se difumina a través de las filas de vidrio
    // cuando el usuario hace scroll (efecto de profundidad).
    val hazeState = remember { HazeState() }

    // Manejo de diálogo local, exactamente compatible con tu ViewModel
    var showAddDialog by remember { mutableStateOf(false) }

    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }

    // viewMode: vista actual de la pantalla —
    //   "Lista"  → listado por categorías (comportamiento original)
    //   "Matriz" → Matriz Eisenhower 2x2 (fusionada con pipeline Scrum)
    //   "Sprint" → tablero del backlog del equipo (EPICs/Features/PBIs)
    var viewMode by remember { mutableStateOf("Lista") }


    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── SELECTOR DE VISTA: Lista | Matriz Eisenhower | Sprint ──
                // (US-04 del plan: la matriz fusiona Eisenhower con el
                // pipeline Scrum usando los datos REALES de /Tasks).
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Lista", "Matriz", "Sprint").forEach { vista ->
                        HabitiaChip(
                            selected = viewMode == vista,
                            onClick = { viewMode = vista },
                            label = vista,
                            leadingIcon = {
                                Icon(
                                    imageVector = when (vista) {
                                        "Lista" -> Icons.Default.List
                                        "Matriz" -> Icons.Default.GridView
                                        else -> Icons.Default.EmojiEvents
                                    },
                                    contentDescription = null,
                                    // Tamano -> cambia 16.dp aqui
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
                when (viewMode) {
                    "Lista" -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 176.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HabitiaSectionTitle(
                        title = "Mis Tareas",
                        subtitle = "Agrupa, prioriza y mantén el orden"
                    )
                }

                // Categorías con HabitiaChip y vector icons
                item {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = uiState.currentCategory == cat
                            val chipIcon = when (cat) {
                                "Hogar" -> Icons.Default.Home
                                "Compras" -> Icons.Default.ShoppingCart
                                "Alimentación" -> Icons.Default.Restaurant
                                else -> Icons.Default.Label
                            }

                            HabitiaChip(
                                selected = isSelected,
                                onClick = { viewModel.changeCategory(cat) },
                                label = cat,
                                leadingIcon = {
                                    Icon(
                                        imageVector = chipIcon,
                                        contentDescription = null,
                                        // Tamano -> cambia 16.dp aqui
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                        // Agregar / eliminar categorías del tablero
                        HabitiaChip(
                            selected = false,
                            onClick = { showAddCategory = true },
                            label = "+ Categoría",
                            // Tamano -> cambia 16.dp aqui
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        if (categories.size > 1) {
                            HabitiaChip(
                                selected = false,
                                onClick = { viewModel.deleteCategory(uiState.currentCategory) },
                                label = "− ${uiState.currentCategory}",
                                // Tamano -> cambia 16.dp aqui
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }

                if (uiState.tasks.isEmpty()) {
                    item {
                        HabitiaEmptyState(
                            icon = Icons.Default.AssignmentTurnedIn,
                            title = "No hay tareas pendientes",
                            subtitle = "Disfruta de tu tiempo o crea un nuevo objetivo"
                        )
                    }
                } else {
                    items(uiState.tasks) { task ->
                        TaskRowItem(
                            task = task,
                            onCheckedChange = { viewModel.completeTask(task.id) },
                            hazeState = hazeState
                        )
                    }
                }
            } // fin LazyColumn (vista Lista)
            "Matriz" -> EisenhowerMatrix(
                tasks = viewModel.allTasks.collectAsState().value,
                hazeState = hazeState,
                onToggle = viewModel::completeTask,
                onMove = viewModel::moveTaskInMatrix
            )
            else -> SprintBoard(
                tasks = viewModel.allTasks.collectAsState().value,
                hazeState = hazeState,
                onTaskStatusChanged = { task, col ->
                    viewModel.updateTask(task.copy(completed = (col == "Hecho")))
                }
            )
            } // fin when (viewMode)
        } // fin Column de contenido
        } // fin else (isLoading)

        // FAB de creación
        //
        // FIX VISIBILIDAD (auditado): estaba en bottom=24dp y el nav bar glass
        // (NavigationBar M3 = 80dp + 12dp de margen en App.kt) lo tapaba por
        // COMPLETO — al ser su vidrio 95% opaco no se intuía nada. Subido a
        // 104dp = 80 (nav) + 12 (margen) + 12 (aire) para flotar sobre él.
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 104.dp)
        ) {
            // FAB de vidrio: color primario casi sólido + borde de luz.
            // Es la acción principal de la pantalla → máximo contraste.
            HabitiaGlassFab(
                onClick = { showAddDialog = true },
                icon = Icons.Default.Add,
                contentDescription = "Nueva Tarea"
            )
        }

        // Diálogo con AlertDialog nativo de Material 3 y colores dinámicos
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(20.dp),
                // Diálogo translúcido (entorno glass): deja intuir el fondo
                // tras el scrim sin sacrificar legibilidad del formulario
                // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (surface)
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                title = {
                    Text(
                        text = "Nueva Tarea",
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
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = "Nombre de la tarea",
                            leadingIcon = Icons.Default.Edit
                        )
                        HabitiaTextField(
                            value = taskDesc,
                            onValueChange = { taskDesc = it },
                            label = "Detalle o notas",
                            leadingIcon = Icons.Default.Notes
                        )

                        Text(
                            text = "Nivel de Prioridad",
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
                            TaskPriority.entries.forEach { pri ->
                                val active = taskPriority == pri
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        // Forma -> cambia radio 10.dp (mas alto=mas redondo)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { taskPriority = pri }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pri.name,
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
                            if (taskTitle.isNotBlank()) {
                                viewModel.addNewTask(taskTitle, taskDesc, taskPriority)
                                taskTitle = ""
                                taskDesc = ""
                                taskPriority = TaskPriority.MEDIUM
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
        if (showAddCategory) {
            AlertDialog(
                onDismissRequest = { showAddCategory = false },
                // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(20.dp),
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                title = { Text("Nueva categoría", fontWeight = FontWeight.Bold) },
                text = {
                    HabitiaTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = "Nombre de la categoría"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newCategory.isNotBlank()) {
                            viewModel.addCategory(newCategory.trim())
                            viewModel.changeCategory(newCategory.trim())
                            newCategory = ""
                        }
                        showAddCategory = false
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    }) { Text("Agregar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    // Color del texto -> Core/theme/Color.kt (outline) o cambia color= aqui
                    TextButton(onClick = { showAddCategory = false }) { Text("Cancelar", color = MaterialTheme.colorScheme.outline) }
                }
            )
        }
        } // fin capa contenido
    } // fin raíz 2 capas
}

/**
 * Fila de tarea con superficie de vidrio (Liquid Glass).
 *
 // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
 * Cómo funciona: el Box raíz recibe [hazeState] a través de habitiaGlass();
 * Haze difumina el contenido pintado DETRÁS de la fila (el fondo pastel y
 * las filas vecinas al hacer scroll) y encima se dibujan tinte translúcido,
 * brillo especular y borde de luz. El checkbox y el texto van en una capa
 * superior (regla UX: contenido SIEMPRE separado del material glass).
 *
 * @param hazeState Estado de blur de la pantalla (null = glass estático).
 */
@Composable
fun TaskRowItem(
    task: Task,
    onCheckedChange: () -> Unit,
    hazeState: HazeState? = null
) {
    // Punto de color que indica la prioridad: error=alta, secundaria=media,
    // primaria=baja. Se mantiene como color pleno porque es información de
    // estado (no decoración): debe leerse al instante sobre el vidrio.
    val priColor = when (task.priority) {
        TaskPriority.HIGH -> MaterialTheme.colorScheme.error
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.secondary
        TaskPriority.LOW -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Forma -> cambia radio 14.dp (mas alto=mas redondo)
            // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
            .habitiaGlass(hazeState = hazeState, shape = RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.completed,
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
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.completed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(priColor)
            )
        }
    }
}
