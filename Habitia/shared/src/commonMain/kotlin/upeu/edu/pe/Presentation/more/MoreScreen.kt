package upeu.edu.pe.Presentation.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.HabitiaGlassCard
import upeu.edu.pe.Core.components.HabitiaSectionTitle
import upeu.edu.pe.Core.theme.habitiaAmbient

@Composable
fun MoreScreen(
    onOpenFood: () -> Unit,              // <-- PARÁMETRO QUE FALTABA
    onOpenReminders: () -> Unit,
    onOpenFocus: () -> Unit,
    onOpenAppearance: (() -> Unit)? = null,
    onOpenDiary: (() -> Unit)? = null,
    onOpenHaabi: (() -> Unit)? = null,
    onOpenAnalytics: (() -> Unit)? = null
) {
    // LIQUID GLASS: fuente de blur local; las tarjetas de navegación de esta
    // pantalla difuminan el fondo pastel al desplazarse la lista.
    val hazeState = remember { HazeState() }

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
            contentPadding = PaddingValues(top = 24.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HabitiaSectionTitle(
                    title = "Más de Habitia",
                    subtitle = "Herramientas para mantener tu hogar y tu rutina en sintonía"
                )
            }

            // 1. Acceso a Cocina & Alimentación — tarjeta glass clicable (ripple
            // recortado por la forma redondeada, regla UX de feedback táctil)
            item {
                HabitiaGlassCard(onClick = onOpenFood, hazeState = hazeState) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            // Tamano -> cambia 44.dp aqui
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.RestaurantMenu,
                                    contentDescription = null,
                                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Cocina & Alimentación",
                                style = MaterialTheme.typography.titleMedium,
                                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Menú semanal, recetas y lista de compras",
                                style = MaterialTheme.typography.bodyMedium,
                                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Acceso a Avisos & Recordatorios — tarjeta glass
            item {
                HabitiaGlassCard(onClick = onOpenReminders, hazeState = hazeState) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            // Tamano -> cambia 44.dp aqui
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Avisos & Rutinas",
                                style = MaterialTheme.typography.titleMedium,
                                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Consulta y completa tus recordatorios diarios",
                                style = MaterialTheme.typography.bodyMedium,
                                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3. Acceso a Momentos de Enfoque — tarjeta glass
            item {
                HabitiaGlassCard(onClick = onOpenFocus, hazeState = hazeState) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            // Tamano -> cambia 44.dp aqui
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SelfImprovement,
                                    contentDescription = null,
                                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Momentos de Enfoque",
                                style = MaterialTheme.typography.titleMedium,
                                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Sesiones de concentración temporizada",
                                style = MaterialTheme.typography.bodyMedium,
                                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 4. Acceso a Personalizar Apariencia — tarjeta glass
            onOpenAppearance?.let { openSettings ->
                item {
                    HabitiaGlassCard(onClick = openSettings, hazeState = hazeState) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                // Tamano -> cambia 44.dp aqui
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Personalizar Apariencia",
                                    style = MaterialTheme.typography.titleMedium,
                                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Paletas pastel, tipografías y foto de perfil",
                                    style = MaterialTheme.typography.bodyMedium,
                                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 5. Mi Diario — pantalla secundaria (libro estético)
            onOpenDiary?.let { openDiary ->
                item {
                    HabitiaGlassCard(onClick = openDiary, hazeState = hazeState) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                // Tamano -> cambia 44.dp aqui
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Mi Diario",
                                    style = MaterialTheme.typography.titleMedium,
                                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Tu libro personal, página por página",
                                    style = MaterialTheme.typography.bodyMedium,
                                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            onOpenAnalytics?.let { openAnalytics ->
                item {
                    HabitiaGlassCard(onClick = openAnalytics, hazeState = hazeState) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(modifier = Modifier.size(44.dp), shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Insights, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Analíticas y recompensas", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text("Podio de hábitos, balance y logros", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            onOpenHaabi?.let { openHaabi ->
                item {
                    HabitiaGlassCard(onClick = openHaabi, hazeState = hazeState) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                // Tamano -> cambia 44.dp aqui
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🐶", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Haabi",
                                    style = MaterialTheme.typography.titleMedium,
                                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Tu compañero de hábitos (chat con IA)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

        } // fin capa contenido
    } // fin raíz 2 capas
}
}
