package upeu.edu.pe.Presentation.UserMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.HabitiaGlassCard
import upeu.edu.pe.Core.theme.CoolorsGenerator
import upeu.edu.pe.Core.theme.GeneratedPalette
import upeu.edu.pe.Core.theme.habitiaAmbient
import upeu.edu.pe.Core.theme.toComposeFontFamily
import upeu.edu.pe.Core.theme.toHexFormat
import upeu.edu.pe.Domain.model.AppFontFamily
import upeu.edu.pe.Domain.model.AppPalette

// ═══════════════════════════════════════════════════════════════════════════
// AppearanceScreen — PERSONALIZACIÓN (apartado separado del perfil, US-03):
// paletas (generador Coolors + curadas + guardadas + creador HEX), fondo,
// tipografía y NUEVO interruptor de MODO OSCURO.
// Usa el MISMO UserMenuViewModel (los handlers ya existían:
// onDarkModeToggled persiste vía UpdateThemeUseCase).
// ═══════════════════════════════════════════════════════════════════════════

private fun contentColorFor(background: Color): Color =
    if (background.red + background.green + background.blue > 2.0) {
        Color.Black.copy(alpha = 0.7f)
    } else {
        Color.White
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    viewModel: UserMenuViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    // Patrón del fix Haze 1.2.x: fuente HERMANA detrás del contenido para
    // que las cards con hazeEffect difuminen el ambiental real.
    val hazeState = remember { HazeState() }
    var currentAiPalette by remember { mutableStateOf(CoolorsGenerator.curatedPalettes.first()) }

    Box(modifier = Modifier.fillMaxSize()) {
        // ══ CAPA FUENTE (hermana) ══
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        // Scaffold TRANSPARENTE: si pintara su propio background taparía
        // la capa fuente y el blur no tendría nada que difuminar.
        androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                title = { Text("Personalizar Apariencia", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (surface)
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = Color.Transparent // deja ver la capa fuente (hermana)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── 0. MODO OSCURO (NUEVO — onDarkModeToggled ya persistía) ──
            HabitiaGlassCard(
                modifier = Modifier.fillMaxWidth(),
                // Forma -> cambia radio 18.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(18.dp),
                hazeState = hazeState
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DarkMode,
                        contentDescription = null,
                        // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo oscuro",
                            style = MaterialTheme.typography.titleMedium,
                            // Peso de letra -> FontWeight.Bold (cambialo aqui)
                            fontWeight = FontWeight.Bold,
                            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Los tokens del vidrio (Liquid Glass) cambian a su variante oscura.",
                            style = MaterialTheme.typography.bodySmall,
                            // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isDarkMode,
                        onCheckedChange = { viewModel.onDarkModeToggled(it) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            // ── 1. GENERADOR DE PALETAS INTELIGENTE (ESTILO COOLORS.CO) ──
            HabitiaGlassCard(
                modifier = Modifier.fillMaxWidth(),
                // Forma -> cambia radio 18.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(18.dp),
                hazeState = hazeState
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Generador Inteligente (Coolors)",
                                style = MaterialTheme.typography.titleMedium,
                                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                fontWeight = FontWeight.Bold,
                                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = "Genera combinaciones armónicas automáticas (incluye fondo y tarjetas) basadas en la teoría del color:",
                        style = MaterialTheme.typography.bodySmall,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Franja de 5 colores estilo Coolors con sus códigos HEX
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        val colorStrip = listOf(
                            currentAiPalette.primary,
                            currentAiPalette.secondary,
                            currentAiPalette.tertiary,
                            currentAiPalette.background,
                            currentAiPalette.surface
                        )
                        colorStrip.forEach { col ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(col),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    text = col.toHexFormat().takeLast(4),
                                    // Tipografia -> cambia fontSize = 10.sp o usa MaterialTheme.typography
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                    fontWeight = FontWeight.Bold,
                                    color = if (col.red + col.green + col.blue > 2.0) {
                                        Color.Black.copy(alpha = 0.6f)
                                    } else {
                                        Color.White
                                    },
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { currentAiPalette = CoolorsGenerator.generateHarmoniousPalette() },
                        // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Tamano -> cambia 18.dp aqui
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        Text("Generar Otra Paleta ✨", fontWeight = FontWeight.Bold)
                    }

                    val isCurrentApplied = state.activePaletteName == currentAiPalette.name
                    val isCurrentSaved = state.savedPalettes.any { it.name == currentAiPalette.name }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.onApplyGeneratedPalette(currentAiPalette) },
                            // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isCurrentApplied,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                if (isCurrentApplied) Icons.Default.Check else Icons.Default.Palette,
                                contentDescription = null,
                                // Tamano -> cambia 18.dp aqui
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (isCurrentApplied) "Aplicada" else "Aplicar")
                        }
                        OutlinedButton(
                            onClick = { viewModel.onSaveGeneratedPalette(currentAiPalette) },
                            // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isCurrentSaved,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                if (isCurrentSaved) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                // Tamano -> cambia 18.dp aqui
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (isCurrentSaved) "Guardada" else "Guardar")
                        }
                    }
                }
            }

            // ── 2. PALETAS DE DISEÑADOR CURADAS ──
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Paletas de Diseñador",
                    style = MaterialTheme.typography.titleMedium,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold,
                    // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onBackground
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(CoolorsGenerator.curatedPalettes) { curated ->
                        val isSelected = state.activePaletteName == curated.name

                        Card(
                            modifier = Modifier
                                .size(width = 120.dp, height = 90.dp)
                                .clickable {
                                    currentAiPalette = curated
                                    viewModel.onApplyGeneratedPalette(curated)
                                }
                                .then(
                                    if (isSelected) Modifier.border(
                                        2.5.dp,
                                        MaterialTheme.colorScheme.primary,
                                        // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                                        RoundedCornerShape(14.dp)
                                    ) else Modifier
                                ),
                            // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = curated.background)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Tamano -> cambia 16.dp aqui
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(curated.primary))
                                    // Tamano -> cambia 16.dp aqui
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(curated.secondary))
                                    // Tamano -> cambia 16.dp aqui
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(curated.tertiary))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = curated.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = contentColorFor(curated.background)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            // Tamano -> cambia 14.dp aqui
                                            modifier = Modifier.size(14.dp),
                                            tint = contentColorFor(curated.background)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── 3. MIS PALETAS GUARDADAS ──
            if (state.savedPalettes.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mis Paletas Guardadas",
                            style = MaterialTheme.typography.titleMedium,
                            // Peso de letra -> FontWeight.Bold (cambialo aqui)
                            fontWeight = FontWeight.Bold,
                            // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${state.savedPalettes.size}",
                            style = MaterialTheme.typography.labelMedium,
                            // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.savedPalettes, key = { it.name }) { saved ->
                            val isSelected = state.activePaletteName == saved.name

                            Box(
                                modifier = Modifier.size(width = 120.dp, height = 90.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { viewModel.onApplyGeneratedPalette(saved) }
                                        .then(
                                            if (isSelected) Modifier.border(
                                                2.5.dp,
                                                MaterialTheme.colorScheme.primary,
                                                // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                                                RoundedCornerShape(14.dp)
                                            ) else Modifier
                                        ),
                                    // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = saved.background)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            // Tamano -> cambia 16.dp aqui
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(saved.primary))
                                            // Tamano -> cambia 16.dp aqui
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(saved.secondary))
                                            // Tamano -> cambia 16.dp aqui
                                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(saved.tertiary))
                                        }
                                        Text(
                                            text = saved.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = contentColorFor(saved.background)
                                        )
                                    }
                                }

                                Surface(
                                    onClick = { viewModel.onDeleteSavedPalette(saved) },
                                    shape = CircleShape,
                                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Eliminar paleta guardada",
                                            // Tamano -> cambia 14.dp aqui
                                            modifier = Modifier.size(14.dp),
                                            // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── 4. CREA TU PALETA (códigos HEX manuales) ──
            CustomPaletteCreator(
                onApply = { viewModel.onApplyGeneratedPalette(it) },
                onSave = { viewModel.onSaveGeneratedPalette(it) },
                hazeState = hazeState
            )

            // ── 5. FONDO DE LA APLICACIÓN ──
            BackgroundSelector(
                onBackgroundSelected = { viewModel.onBackgroundSelected(it) }
            )

            // ── 6. SELECTOR DE TIPOGRAFÍA ──
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Tipografía del Sistema",
                    style = MaterialTheme.typography.titleMedium,
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold,
                    // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onBackground
                )

                AppFontFamily.entries.chunked(2).forEach { rowFonts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowFonts.forEach { font ->
                            val isSelected = font == state.selectedFont
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onFontSelected(font) },
                                label = {
                                    Text(
                                        text = font.displayName,
                                        fontFamily = font.toComposeFontFamily(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                leadingIcon = if (isSelected) {
                                    // Tamano -> cambia 16.dp aqui
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                        if (rowFonts.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
    } // fin Box raíz (2 capas: fuente hermana + contenido)
}
