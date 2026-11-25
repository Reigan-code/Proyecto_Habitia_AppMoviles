package upeu.edu.pe.Presentation.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.HabitiaButton
import upeu.edu.pe.Core.components.HabitiaChip
import upeu.edu.pe.Core.components.HabitiaSectionTitle
import upeu.edu.pe.Core.theme.habitiaAmbient
import upeu.edu.pe.Core.theme.habitiaGlass

@Composable
fun FocusScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    // Leídos directamente de los StateFlow de tu FocusViewModel actual
    val timeLeftSeconds by viewModel.timeLeftSeconds.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val activePreset by viewModel.selectedPresetMinutes.collectAsState()

    // LIQUID GLASS: fuente de blur local. La esfera del temporizador es el
    // protagonista visual de esta pantalla: difumina el fondo pastel y flota
    // como una burbuja de calma sobre la paleta activa.
    val hazeState = remember { HazeState() }

    // Cálculo dinámico del tiempo sin depender de un estado externo
    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val formattedTime = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    Box(modifier = modifier.fillMaxSize()) {
        // ═══ CAPA FUENTE (hermana) — Haze 1.2.x excluye effects descendientes
        // del source (zIndex); como hermana el blur funciona de verdad.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        // ═══ CAPA CONTENIDO (la esfera de vidrio difumina el ambiental)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Spacer(modifier = Modifier.height(24.dp))
        HabitiaSectionTitle(
            title = "Momentos de Enfoque",
            subtitle = "Cultiva la atención plena y elimina distracciones",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Selector de minutos
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(15, 25, 45).forEach { mins ->
                HabitiaChip(
                    selected = activePreset == mins,
                    onClick = { viewModel.setPreset(mins) },
                    label = "$mins min",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ESFERA DE VIDRIO (Liquid Glass): el corazón de la pantalla. Un
        // círculo glass que difumina el fondo (55% de tinte) con borde de luz
        // y brillo especular — parece una burbuja flotando. El temporizador
        // va en la capa de contenido, sobre el vidrio.
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(0.6f)
                .sizeIn(maxWidth = 260.dp)
                // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
                .habitiaGlass(
                    hazeState = hazeState,
                    shape = CircleShape,
                    // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
                    containerAlpha = 0.55f
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.SelfImprovement,
                    contentDescription = null,
                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                    tint = MaterialTheme.colorScheme.primary,
                    // Tamano -> cambia 32.dp aqui
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        // Tipografia -> cambia fontSize = 46.sp o usa MaterialTheme.typography
                        fontSize = 46.sp,
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        fontWeight = FontWeight.Bold
                    ),
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isRunning) "Fluyendo en calma..." else "Listo para iniciar",
                    style = MaterialTheme.typography.labelMedium,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                // FIX VISIBILIDAD: bottom 32→104dp — la Row de botones
                // Iniciar/Pausar + Reiniciar quedaba CUBIERTA por el nav
                // bar glass (80+12dp); el usuario no veía NINGÚN botón.
                // nav(80) + safeArea(~32) + 24 extra → botones no se cortan
                .padding(bottom = 136.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HabitiaButton(
                onClick = {
                    if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (primary)
                containerColor = MaterialTheme.colorScheme.primary,
                // Color del texto -> Core/theme/Color.kt (onPrimary) o cambia color= aqui
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        // Tamano -> cambia 20.dp aqui
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "Pausar" else "Iniciar",
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            HabitiaButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .weight(0.7f)
                    .height(52.dp),
                // Color del texto -> Core/theme/Color.kt (surfaceVariant) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (surfaceVariant)
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        // Tamano -> cambia 18.dp aqui
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reiniciar",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        } // fin capa contenido (Column)
    } // fin raíz 2 capas
}
