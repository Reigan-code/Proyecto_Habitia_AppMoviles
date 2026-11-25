package upeu.edu.pe.Presentation.dev

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.HabitiaGlassCard
import upeu.edu.pe.Core.theme.HabitiaTheme
import upeu.edu.pe.Core.theme.habitiaAmbient

/**
 * =============================================================================
 * GlassDiagnosticScreen — laboratorio de diagnóstico del sistema Liquid Glass
 * =============================================================================
 * 😷 NO es parte de la navegación de la app. Es una pantalla temporal para
 * verificar por separado qué produce una tonalidad distinta entre el interior
 * y el contorno de una tarjeta glass (cadena de modificadores, Haze o color).
 *
 * CÓMO USARLA:
 * - En Android Studio, abre este archivo y usa el PANEL DE PREVIEW → verás
 *   las 3 tarjetas SIN instalar la app (nota: el blur de Haze no se ejecuta
 *   en Preview; verás la receta SIN blur — exactamente lo que la variante B
 *   debe coincidir en el emulador real).
 * - Para verla en el emulador real, agrega temporalmente una ruta/objeto en
 *   HabitiaScreen y App.kt (ver comentario en `GlassDiagnosticPreview`).
 *
 * DIFERENCIAL QUE SEPARA CAUSAS:
 * A) idéntica a las de Home (receta completa CON blur)
 * B) misma receta pero SIN blur (glass estático)
 * C) SOLO tinte translúcido plano, sin especular ni borde de luz
 *
 * INTERPRETACIÓN (comparar Preview vs emulador en el MISMO dispositivo):
 * - A ≡ B en el emulador  → Haze no está difuminando nada (revisar
 *   hazeSource/ambient, versión, o la GPU del emulador: el blur aporta
 *   textura/suavidad visible cuando funciona).
 * - B ≠ C en emulador     → la receta (especular/borde/sombra) es la que
 *   produce la diferencia contorno vs interior — por DISEÑO; eso es lo que
 *   lee el ojo como "vidrio". Unificar 100% = desaparece el glass.
 * - En Preview A ≡ B siempre (no hay blur): no significa fallo.
 */
@Composable
fun GlassDiagnosticScreen() {
    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize()) {
        //  CAPA FUENTE (hermana) — misma arquitectura que las pantallas
        // reales tras la auditoría #4: el blur solo funciona si el effect
        // NO es descendiente del source.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        // ═══ CAPA CONTENIDO
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "Laboratorio Glass (diagnóstico)",
                style = MaterialTheme.typography.titleMedium
            )

            // A) Receta completa con blur — igual a las tarjetas de Home
            DiagGlassCard(
                label = "A · Receta completa (blur ON)",
                hazeState = hazeState
            )

            // B) Receta completa SIN blur — glass estático
            DiagGlassCard(
                label = "B · Receta completa (blur OFF)",
                hazeState = null
            )

            // C) Solo tinte plano — sin especular ni borde de luz
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                    .clip(RoundedCornerShape(20.dp))
                    // Color de fondo -> Core/theme/Color.kt (surface) o cambia aqui
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.80f))
            ) {
                Text(
                    "C · Solo tinte plano (sin especular ni borde)",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        } // fin capa contenido
    } // fin raíz 2 capas
}

/** Card de testeo: solo texto, tamaño y forma FIJOS para comparar entre A y B. */
@Composable
private fun DiagGlassCard(label: String, hazeState: HazeState?) {
    HabitiaGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        hazeState = hazeState,
        // Forma -> cambia radio 20.dp (mas alto=mas redondo)
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            label,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

