package upeu.edu.pe.Presentation.more

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import upeu.edu.pe.Core.theme.habitiaGlass
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ═══════════════════════════════════════════════════════════════════════════
// DiaryScreen — "Mi Diario": un libro personal estético.
//
// Diseño: portada → al "Abrir Diario" se revela la vista de lectura/escritura
// con navegación horizontal página a página (slide + fade + sombra lateral).
// Cada página: fecha + TextField multilínea con tipografía serif (Playfair
// Display del sistema → FontFamily.Serif) y colores suaves de la paleta.
// Dark mode → cuaderno oscuro. Estado local (lista de entradas), ligero.
// ═══════════════════════════════════════════════════════════════════════════

private data class DiaryEntry(val id: String = "", val date: String = "", val text: String = "")

@Composable
fun DiaryScreen(onBack: () -> Unit, viewModel: DiaryViewModel? = null) {
    val entries = remember { mutableStateListOf<DiaryEntry>() }
    var opened by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }

    // Inicializa con una página para hoy si no hay ninguna.
    LaunchedEffect(viewModel) {
        viewModel?.refresh()
        viewModel?.entries?.collect { list ->
            entries.clear()
            if (list.isEmpty()) entries.add(DiaryEntry(date = todayLabel(), text = ""))
            else entries.addAll(list.map { DiaryEntry(id = it.id, date = it.date, text = it.text) })
        }
    }
    if (viewModel == null && entries.isEmpty()) {
        entries.add(DiaryEntry(todayLabel(), ""))
    }

    val safeIndex = currentPage.coerceIn(0, entries.lastIndex)

    Box(modifier = Modifier.fillMaxSize()) {
        // ══ CAPA FUENTE (hermana) para glass real ══
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Color de fondo -> Core/theme/Color.kt (background) o cambia aqui
                .background(MaterialTheme.colorScheme.background)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = "Mi Diario",
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            if (!opened) {
                // ── PORTADA ──
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        // Forma -> cambia radio 24.dp (mas alto=mas redondo)
                        .clip(RoundedCornerShape(24.dp)),
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📖",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Mi Diario",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = FontFamily.Serif,
                                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                fontWeight = FontWeight.Bold
                            ),
                            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Un espacio para tus pensamientos, día a día.",
                            style = MaterialTheme.typography.bodyMedium,
                            // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(32.dp))
                        Button(onClick = { opened = true }) {
                            Text("Abrir Diario")
                        }
                    }
                }
            } else {
                // ── VISTA DE LECTURA/ESCRITURA ──
                // Indicador de página
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (safeIndex > 0) currentPage = safeIndex - 1 },
                        enabled = safeIndex > 0
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Página anterior")
                    }
                    Text(
                        text = "Página ${safeIndex + 1} de ${entries.size}",
                        style = MaterialTheme.typography.labelMedium,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = { if (safeIndex < entries.lastIndex) currentPage = safeIndex + 1 },
                        enabled = safeIndex < entries.lastIndex
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Página siguiente")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Página con animación de paso (slide + fade)
                AnimatedContent(
                    targetState = safeIndex,
                    transitionSpec = {
                        (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                                (slideOutHorizontally { -it / 3 } + fadeOut())
                    },
                    label = "diaryPage"
                ) { idx ->
                    val entry = entries[idx]
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                            .clip(RoundedCornerShape(20.dp)),
                        // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = entry.date,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                    fontWeight = FontWeight.Bold
                                ),
                                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(12.dp))
                            // Línea decorativa (lomo de página)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    // Color de fondo -> Core/theme/Color.kt (outlineVariant) o cambia aqui
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                            Spacer(Modifier.height(12.dp))
                            BasicTextField(
                                value = entry.text,
                                onValueChange = { newText ->
                                    entries[idx] = entry.copy(text = newText)
                                    viewModel?.update(entry.id, newText)
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    // Tipografia -> cambia fontSize = 16.sp o usa MaterialTheme.typography
                                    fontSize = 16.sp,
                                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 24.sp
                                ),
                                decorationBox = { inner ->
                                    if (entry.text.isEmpty()) {
                                        Text(
                                            text = "Escribe aquí tus pensamientos...",
                                            style = TextStyle(
                                                fontFamily = FontFamily.Serif,
                                                // Tipografia -> cambia fontSize = 16.sp o usa MaterialTheme.typography
                                                fontSize = 16.sp,
                                                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                    inner()
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = { opened = false },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cerrar libro") }
                    Button(
                        onClick = {
                            viewModel?.addToday()
                            currentPage = entries.lastIndex
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        // Tamano -> cambia 16.dp aqui
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Nueva página")
                    }
                }
            }
        }
    }
}

private fun todayLabel(): String {
    val sdf = SimpleDateFormat("d 'de' MMMM, yyyy", Locale("es", "ES"))
    return sdf.format(Date())
}
