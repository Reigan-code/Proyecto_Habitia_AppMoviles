package upeu.edu.pe.Presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import upeu.edu.pe.Core.components.AnimatedMascot
import upeu.edu.pe.Core.components.HabitiaEmptyState
import upeu.edu.pe.Core.components.HabitiaGlassCard
import upeu.edu.pe.Core.config.HaabiConfig
import upeu.edu.pe.Core.config.HaabiPlacement
import upeu.edu.pe.Core.config.IslandConfig
import upeu.edu.pe.Core.theme.habitiaAmbient
import upeu.edu.pe.Core.theme.habitiaGlass
import upeu.edu.pe.Domain.model.Habit
import upeu.edu.pe.Domain.model.ReminderType
import upeu.edu.pe.Domain.model.Task
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenProfileSettings: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onLogout: () -> Unit = {},

    userName: String = "Usuario",

    userEmail: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val hazeState = remember { HazeState() }

    // ── RITUALES DIARIOS: estado del asistente paso a paso ──
    // showCreateRitual: dialog para crear un ritual nuevo (local, documentado).
    // showRitualWizard: guía paso a paso por los rituales existentes — se
    //   abre cuando el usuario presiona "No" en el dialog inicial o el botón
    //   "Guía paso a paso". Cada paso permite marcar Hecho (toggleHabit real)
    //   o Saltar; al final muestra resumen.
    var showCreateRitual by remember { mutableStateOf(false) }
    var newRitualName by remember { mutableStateOf("") }
    var extraRituals by remember { mutableStateOf(listOf<String>()) }
    var showRitualWizard by remember { mutableStateOf(false) }
    var ritualWizardIndex by remember { mutableStateOf(0) }

    val timeZone = remember { TimeZone.currentSystemDefault() }
    val today = remember { Clock.System.todayIn(timeZone) }
    val yesterday = remember { today.minus(1, DateTimeUnit.DAY) }
    val tomorrow = remember { today.plus(1, DateTimeUnit.DAY) }

    val now = remember { Clock.System.now().toLocalDateTime(timeZone) }
    val greeting = when (now.hour) {
        in 5..11 -> "Buenos días"
        in 12..18 -> "Buenas tardes"
        else -> "Buenas noches"
    }
    val firstName = userName.trim().split(" ").firstOrNull() ?: userName

    val doneToday = uiState.completedTasks + uiState.completedHabits
    val totalToday = uiState.totalTasks + uiState.totalHabits
    val maxStreak = uiState.habits.maxOfOrNull { it.currentStreak } ?: 0
    val dayPercent = (uiState.progress * 100).toInt()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (surface)
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Surface(
                            // Tamano -> cambia 72.dp aqui
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            // Color del texto -> Core/theme/Color.kt (primaryContainer) o cambia color= aqui
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    // Color del icono -> Core/theme/Color.kt (onPrimaryContainer) o cambia tint= aqui
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    // Tamano -> cambia 40.dp aqui
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userName,
                                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    // correo REAL del usuario registrado (antes
                                    // era 'mauricio.lazarte@upeu.edu.pe' fijo)
                                    text = userEmail.ifBlank { "Sin correo" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        scope.launch { drawerState.close() }
                                        onOpenProfileSettings()
                                    },
                                shape = CircleShape,
                                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar Perfil",
                                        // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                        tint = MaterialTheme.colorScheme.primary,
                                        // Tamano -> cambia 18.dp aqui
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                        // Color del texto -> Core/theme/Color.kt (outline) o cambia color= aqui
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(20.dp))
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Palette, contentDescription = null) },
                            label = { Text("Personalizar Temas & Estilo") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onOpenProfileSettings()
                            },
                            // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                            shape = RoundedCornerShape(12.dp)
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                            label = { Text("Mis Tareas & Prioridades") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToTasks()
                            },
                            // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    NavigationDrawerItem(
                        // Color del icono -> Core/theme/Color.kt (error) o cambia tint= aqui
                        icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                        label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onLogout()
                        },
                        // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            //  Haze 1.2.x
            // EXCLUYE los hazeEffect descendientes de su propio source (el
            // filtro area.zIndex < hazeZIndex nunca se cumple dentro del
            // subárbol). Como hermana, el área se incluye en el blur
            //  por eso funciona y difumina este fondo ambiental
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
                    .habitiaAmbient()
            )
            // Contenido
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 20.dp, bottom = IslandConfig.contentBottomPadding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. HEADER
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$greeting,",
                                    style = MaterialTheme.typography.bodyMedium,
                                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = firstName,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                        fontWeight = FontWeight.Bold
                                    ),
                                    // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .clickable { scope.launch { drawerState.open() } },
                                shape = CircleShape,
                                // Color del texto -> Core/theme/Color.kt (primaryContainer) o cambia color= aqui
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Abrir Menú de Cuenta",
                                        // Color del icono -> Core/theme/Color.kt (onPrimaryContainer) o cambia tint= aqui
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        // Tamano -> cambia 26.dp aqui
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 2. HERO CARD
                    item {
                        DayHeroCard(
                            hazeState = hazeState,
                            yesterday = yesterday,
                            today = today,
                            tomorrow = tomorrow,
                            doneToday = doneToday,
                            totalToday = totalToday,
                            maxStreak = maxStreak,
                            dayPercent = dayPercent,
                            progress = uiState.progress,
                            onDetailsClick = onNavigateToTasks
                        )
                    }

                    // 3. QUICK TOGGLE
                    item {
                        QuickToggleCard(hazeState = hazeState)
                    }

                    // 4. SIGUIENTE EN TU AGENDA
                    uiState.upcomingReminder?.let { reminder ->
                        item {
                            SectionHeader(title = "Siguiente en tu agenda")
                            Spacer(modifier = Modifier.height(8.dp))
                            HabitiaGlassCard(
                                hazeState = hazeState,
                                // Forma -> cambia radio 18.dp (mas alto=mas redondo)
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    val contextualIcon = when (reminder.type) {
                                        ReminderType.MEDICATION -> Icons.Default.Medication
                                        ReminderType.RECURRING -> Icons.Default.Repeat
                                        ReminderType.NORMAL -> Icons.Default.Notifications
                                    }
                                    Surface(
                                        // Tamano -> cambia 42.dp aqui
                                        modifier = Modifier.size(42.dp),
                                        shape = CircleShape,
                                        // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = contextualIcon,
                                                contentDescription = null,
                                                // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                                tint = MaterialTheme.colorScheme.primary,
                                                // Tamano -> cambia 24.dp aqui
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reminder.title,
                                            // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${reminder.time} · ${reminder.description}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { viewModel.completeReminder(reminder.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Completar recordatorio",
                                            // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. TAREAS PRIORITARIAS
                    item {
                        SectionHeader(title = "Tareas prioritarias")
                    }
                    if (uiState.tasks.isEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            HabitiaEmptyState(
                                icon = Icons.Default.CheckCircleOutline,
                                title = "Todo despejado por aquí",
                                subtitle = "No tienes tareas pendientes para el día de hoy"
                            )
                        }
                    } else {
                        items(uiState.tasks.take(4), key = { it.id }) { task ->
                            PriorityTaskRow(
                                task = task,
                                onToggle = { viewModel.completeTask(task.id, !task.completed) },
                                hazeState = hazeState
                            )
                        }
                    }

                    // 6. CONSEJOS PARA HOY
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(
                            title = "Consejos para hoy",
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // tipsForToday(): 3 consejos ROTATIVOS por día del
                            // año (cada día un conjunto nuevo, sin hardcode).
                            tipsForToday().forEachIndexed { index, tip ->
                                TipCard(tip = tip, tonalIndex = index)
                            }
                        }
                    }

                    // 7. RITUALES DIARIOS
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SectionHeader(title = "Tus rituales diarios")
                        Spacer(modifier = Modifier.height(8.dp))
                        // Tarjeta "Ritual matutino" con Haabi dentro (BottomEnd)
                        HabitiaGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                            shape = RoundedCornerShape(20.dp),
                            hazeState = hazeState
                        ) {
                            Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
                                // Ritales en fila horizontal
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    uiState.habits.forEach { habit ->
                                        RitualCard(
                                            habit = habit,
                                            onToggle = { viewModel.toggleHabit(habit.id) }
                                        )
                                    }
                                    extraRituals.forEach { name ->
                                        RitualCard(
                                            habit = upeu.edu.pe.Domain.model.Habit(
                                                id = "local-$name",
                                                name = name,
                                                frequency = "DAILY",
                                                completed = false,
                                                currentStreak = 0
                                            ),
                                            onToggle = { }
                                        )
                                    }
                                }
                                // Haabi dentro de la tarjeta SOLO si la
                                // configuración lo pide (HaabiConfig.placement
                                // en Core/config/UiConfig.kt). Por defecto es
                                // GLOBAL (mascota flotante en App.kt) y la
                                // tarjeta queda limpia.
                                if (HaabiConfig.placement == HaabiPlacement.INSIDE_RITUAL_CARD) {
                                    AnimatedMascot(
                                        visible = true,
                                        mascotSize = HaabiConfig.mascotSize * 0.65f,
                                        showHearts = false,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(bottom = 8.dp, end = 8.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Acciones: crear ritual o guía paso a paso.
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            androidx.compose.material3.OutlinedButton(
                                onClick = { showCreateRitual = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                // Tamano -> cambia 16.dp aqui
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Crear ritual", style = MaterialTheme.typography.labelMedium)
                            }
                            androidx.compose.material3.Button(
                                onClick = { showRitualWizard = true; ritualWizardIndex = 0 },
                                modifier = Modifier.weight(1f)
                            ) {
                                // Tamano -> cambia 16.dp aqui
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Guía paso a paso", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
            } // fin capa contenido

            // ── RITUALES: diálogos (crear + guía paso a paso) ──
            val allRituals = uiState.habits.map { it.name } + extraRituals

            if (showCreateRitual) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showCreateRitual = false; newRitualName = "" },
                    // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                    shape = RoundedCornerShape(20.dp),
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (surface)
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    title = { Text("Nuevo ritual diario", fontWeight = FontWeight.Bold) },
                    text = {
                        androidx.compose.material3.OutlinedTextField(
                            value = newRitualName,
                            onValueChange = { newRitualName = it },
                            label = { Text("Nombre del ritual") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        androidx.compose.material3.Button(onClick = {
                            if (newRitualName.isNotBlank()) {
                                viewModel.createRitual(newRitualName.trim())
                            }
                            newRitualName = ""
                            showCreateRitual = false
                        }) { Text("Crear") }
                    },
                    dismissButton = {
                        androidx.compose.material3.TextButton(onClick = {
                            showCreateRitual = false
                            newRitualName = ""
                        }) { Text("Cancelar") }
                    }
                )
            }

            if (showRitualWizard) {
                if (allRituals.isEmpty()) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showRitualWizard = false },
                        // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                        shape = RoundedCornerShape(20.dp),
                        // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                        // Color del contenedor -> Core/theme/Color.kt (surface)
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        title = { Text("Sin rituales", fontWeight = FontWeight.Bold) },
                        text = { Text("Crea tu primer ritual con el botón 'Crear ritual' para empezar la guía paso a paso.") },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = { showRitualWizard = false }) { Text("Entendido") }
                        }
                    )
                } else {
                    val idx = ritualWizardIndex.coerceIn(0, allRituals.lastIndex)
                    val ritualName = allRituals[idx]
                    val habitId = uiState.habits.firstOrNull { it.name == ritualName }?.id
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showRitualWizard = false; ritualWizardIndex = 0 },
                        // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                        shape = RoundedCornerShape(20.dp),
                        // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                        // Color del contenedor -> Core/theme/Color.kt (surface)
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        title = { Text("Ritual ${idx + 1} de ${allRituals.size}: $ritualName", fontWeight = FontWeight.Bold) },
                        text = { Text("¿Completaste este ritual hoy?") },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = {
                                if (habitId != null) viewModel.toggleHabit(habitId)
                                if (idx + 1 < allRituals.size) ritualWizardIndex = idx + 1
                                else { showRitualWizard = false; ritualWizardIndex = 0 }
                            }) { Text("Hecho ✓") }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(onClick = {
                                if (idx + 1 < allRituals.size) ritualWizardIndex = idx + 1
                                else { showRitualWizard = false; ritualWizardIndex = 0 }
                            }) { Text("Saltar") }
                        }
                    )
                }
            }

        } // fin raíz 2 capas (fuente + contenido)
    }
}

@Composable
private fun DayHeroCard(
    hazeState: HazeState?,
    yesterday: LocalDate,
    today: LocalDate,
    tomorrow: LocalDate,
    doneToday: Int,
    totalToday: Int,
    maxStreak: Int,
    dayPercent: Int,
    progress: Float,
    onDetailsClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val badgeTextColor = if (primary.luminance() > 0.5f) Color(0xFF2D2633) else Color.White

    HabitiaGlassCard(
        hazeState = hazeState,
        // Forma -> cambia radio 26.dp (mas alto=mas redondo)
        shape = RoundedCornerShape(26.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Fila de fechas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateChip(date = yesterday, label = "Ayer", selected = false)
                DateChip(date = today, label = "Hoy", selected = true)
                DateChip(date = tomorrow, label = "Mañana", selected = false)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Progreso del día
            Text(
                text = "$doneToday de $totalToday",
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "tareas y rituales completados hoy",
                style = MaterialTheme.typography.bodyMedium,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (maxStreak > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                        tint = MaterialTheme.colorScheme.primary,
                        // Tamano -> cambia 16.dp aqui
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Día $maxStreak de racha",
                        // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de progreso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    // Forma -> cambia radio 8.dp (mas alto=mas redondo)
                    .clip(RoundedCornerShape(8.dp))
                    // Color de fondo -> Core/theme/Color.kt (surfaceVariant) o cambia aqui
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(listOf(primary, tertiary))
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila inferior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Botón "Detalles": fondo suave con clip sin aplicar un segundo habitiaGlass denso
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        // Color de fondo -> Core/theme/Color.kt (primary) o cambia aqui
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .clickable(onClick = onDetailsClick)
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Detalles",
                            // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                            tint = MaterialTheme.colorScheme.primary,
                            // Tamano -> cambia 18.dp aqui
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Badge circular del porcentaje
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(primary, tertiary)))
                        .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$dayPercent%",
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = badgeTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun DateChip(date: LocalDate, label: String, selected: Boolean) {
    val container = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    }
    val content = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            // Forma -> cambia radio 16.dp (mas alto=mas redondo)
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = content
            )
            Text(
                text = "${date.dayOfMonth}",
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = content
            )
        }
    }
}

@Composable
private fun QuickToggleCard(hazeState: HazeState?) {
    var answer by remember { mutableStateOf<Boolean?>(null) }

    HabitiaGlassCard(
        hazeState = hazeState,
        // Forma -> cambia radio 20.dp (mas alto=mas redondo)
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    // Color de fondo -> Core/theme/Color.kt (primary) o cambia aqui
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                    tint = MaterialTheme.colorScheme.primary,
                    // Tamano -> cambia 22.dp aqui
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "¿Iniciaste tu ritual matutino?",
                // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    // Color de fondo -> Core/theme/Color.kt (surfaceVariant) o cambia aqui
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            ) {
                YesNoSegment(
                    label = "Sí",
                    selected = answer == true,
                    onClick = { answer = true }
                )
                YesNoSegment(
                    label = "No",
                    selected = answer == false,
                    onClick = { answer = false }
                )
            }
        }
    }
}

@Composable
private fun YesNoSegment(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                fontWeight = FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun PriorityTaskRow(
    task: Task,
    onToggle: () -> Unit,
    hazeState: HazeState?
) {
    // Usamos HabitiaGlassCard directamente para garantizar la misma consistencia visual
    HabitiaGlassCard(
        hazeState = hazeState,
        // Forma -> cambia radio 16.dp (mas alto=mas redondo)
        shape = RoundedCornerShape(16.dp),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    checkedColor = MaterialTheme.colorScheme.primary,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (task.completed) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                        maxLines = 2
                    )
                }
                if (!task.completed && task.dueTime != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            // Forma -> cambia radio 8.dp (mas alto=mas redondo)
                            .clip(RoundedCornerShape(8.dp))
                            // Color de fondo -> Core/theme/Color.kt (primary) o cambia aqui
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Hoy · ${task.dueTime}",
                            style = MaterialTheme.typography.labelSmall,
                            // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

private data class DailyTip(val emoji: String, val text: String)

// CONSEJOS ROTATIVOS: la lista completa de consejos. Cada DÍA se muestra
// un subconjunto DISTINTO (rotación por día del año) — ya no son siempre
// los mismos 3: el índice de arranque avanza con la fecha real del
// dispositivo (kotlinx-datetime, sin hardcode).
private val dailyTips = listOf(
    DailyTip(
        emoji = "💧",
        text = "Bebe un vaso de agua al despertar: activa tu metabolismo y tu concentración."
    ),
    DailyTip(
        emoji = "🧘",
        text = "5 minutos de respiración profunda reducen el estrés de toda la mañana."
    ),
    DailyTip(
        emoji = "🌙",
        text = "Apaga las pantallas 30 minutos antes de dormir: tu sueño lo notará."
    ),
    DailyTip(
        emoji = "🚶",
        text = "Una caminata corta después de comer ayuda a tu digestión y a tu ánimo."
    ),
    DailyTip(
        emoji = "📝",
        text = "Escribe tus 3 prioridades del día antes de abrir cualquier red social."
    ),
    DailyTip(
        emoji = "🍎",
        text = "Prepara tu comida la noche anterior: ahorras tiempo y evitas improvisar."
    ),
    DailyTip(
        emoji = "⏰",
        text = "Trabaja en bloques de 25 minutos: el foco sostenido vence al multitasking."
    ),
    DailyTip(
        emoji = "🧹",
        text = "Dedica 10 minutos al orden de tu espacio: un entorno limpio despeja la mente."
    ),
    DailyTip(
        emoji = "📵",
        text = "Guarda el celular fuera del dormitorio: tu mañana empieza la noche anterior."
    ),
    DailyTip(
        emoji = "🌿",
        text = "Toma 5 minutos de sol temprano: regula tu reloj biológico y tu energía."
    ),
    DailyTip(
        emoji = "💪",
        text = "Estira el cuerpo al levantarte: despierta tus músculos y tu circulación."
    ),
    DailyTip(
        emoji = "📖",
        text = "Lee 10 páginas al día: en un año habrás terminado más de 12 libros."
    ),
    DailyTip(
        emoji = "🤝",
        text = "Agradece algo pequeño cada noche: la gratitud entrena tu cerebro al optimismo."
    ),
    DailyTip(
        emoji = "🥤",
        text = "Lleva siempre una botella de agua contigo: la hidratación sostiene tu foco."
    ),
    DailyTip(
        emoji = "🎯",
        text = "Divide las tareas grandes en pasos de 15 minutos: lo pequeño avanza igual."
    )
)

// tipsForToday(): rotación diaria real — cada día del año muestra un
// conjunto distinto de 3 consejos consecutivos (año de 365/366 → el ciclo
// se repite naturalmente al año siguiente).
private fun tipsForToday(): List<DailyTip> {
    val dayOfYear = kotlinx.datetime.Clock.System.todayIn(
        kotlinx.datetime.TimeZone.currentSystemDefault()
    ).dayOfYear
    val start = (dayOfYear - 1) % dailyTips.size
    return List(minOf(3, dailyTips.size)) { i ->
        dailyTips[(start + i) % dailyTips.size]
    }
}

@Composable
private fun TipCard(tip: DailyTip, tonalIndex: Int) {
    val tonal = when (tonalIndex % 3) {
        0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        1 -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
    }

    Box(
        modifier = Modifier
            .width(170.dp)
            .height(132.dp)
            // Forma -> cambia radio 16.dp (mas alto=mas redondo)
            .clip(RoundedCornerShape(16.dp))
            .background(tonal)
            .padding(12.dp)
    ) {
        Text(
            text = tip.text,
            // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = tip.emoji,
            // Tipografia -> cambia fontSize = 26.sp o usa MaterialTheme.typography
            fontSize = 26.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

private val ritualEmojis = mapOf(
    "spa" to "🧘",
    "water" to "💧",
    "book" to "📖",
    "walk" to "🚶",
    "gym" to "💪"
)

@Composable
private fun RitualCard(habit: Habit, onToggle: () -> Unit) {
    val emoji = ritualEmojis[habit.icon] ?: "⭐"
    val done = habit.completed

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(92.dp)
            .alpha(if (done) 1f else 0.6f)
            // Forma -> cambia radio 16.dp (mas alto=mas redondo)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (done) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
                .then(
                    if (done) {
                        Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            // Tipografia -> cambia fontSize = 30.sp o usa MaterialTheme.typography
            Text(text = emoji, fontSize = 30.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = habit.name,
            // Peso de letra -> FontWeight.Medium (cambialo aqui)
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Text(
            text = "Racha ${habit.currentStreak}d",
            style = MaterialTheme.typography.labelSmall,
            // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            // Peso de letra -> FontWeight.Bold (cambialo aqui)
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
            color = MaterialTheme.colorScheme.onBackground
        )
        trailingIcon?.invoke()
    }
}
