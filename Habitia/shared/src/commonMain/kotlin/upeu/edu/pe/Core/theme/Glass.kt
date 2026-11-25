package upeu.edu.pe.Core.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
/**
 * =============================================================================
 * SISTEMA "LIQUID GLASS"
 * =============================================================================
 * Design tokens del material glass, calibrados para evitar dobles tonalidades,
 * sombras duras filtradas tras el radio del vidrio o parches de gradiente.
 */
data class GlassTokens(
    val containerAlpha: Float,
    val tintColor: Color,
    val strokeColor: Color,
    val strokeHighlightColor: Color,
    val specularAlpha: Float,
    val shadowElevation: Dp
)
/** Tokens activos según el tema actual (claro/oscuro). */
@Composable
fun glassTokens(): GlassTokens {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (isDark) {
        GlassTokens(
            containerAlpha = 0.40f,
            tintColor = MaterialTheme.colorScheme.surface,
            strokeColor = Color.White.copy(alpha = 0.12f),
            strokeHighlightColor = Color.White.copy(alpha = 0.35f),
            specularAlpha = 0.04f,
            shadowElevation = 3.dp
        )
    } else {
        GlassTokens(
            // 0.72f es el recomendado pero yo prefiero 0.30f
            containerAlpha = 0.20f,
            tintColor = MaterialTheme.colorScheme.surface,
            strokeColor = Color.White.copy(alpha = 1.0f),
            //niveles de brillo
            strokeHighlightColor = Color.White.copy(alpha = 1.0f),
            // specularAlpha reducido a 0.04f para evitar el "bloque blanco/diferente" en el centro
            specularAlpha = 0.25f,
            // Sombra suave de 3dp para que la penumbra nativa no oscurezca el contorno translúcido
            shadowElevation = 16.dp
        )
    }
}
/** Borde de luz: stroke con gradiente (más brillante arriba, como una lente). */
fun glassBorderBrush(tokens: GlassTokens): Brush = Brush.linearGradient(
    colors = listOf(
        tokens.strokeHighlightColor,
        tokens.strokeColor,
        tokens.strokeHighlightColor
    )
)
/** Brillo especular: gradiente suave y continuo, sin saltos abruptos de color. */
fun glassSpecularBrush(tokens: GlassTokens): Brush = Brush.linearGradient(
    colorStops = arrayOf(
        0.0f to Color.White.copy(alpha = tokens.specularAlpha),
        0.4f to Color.White.copy(alpha = tokens.specularAlpha * 0.3f),
        1.0f to Color.Transparent
    ),
    start = Offset.Zero,
    end = Offset.Infinite
)
@Composable
fun Modifier.habitiaGlass(
    hazeState: HazeState? = null,
    shape: Shape = RoundedCornerShape(20.dp),
    containerAlpha: Float? = null,
    tint: Color = Color.Unspecified,
    elevation: Dp? = null,
    onClick: (() -> Unit)? = null
): Modifier {
    val tokens = glassTokens()
    val effectiveTint = if (tint != Color.Unspecified) tint else tokens.tintColor
    val tintColor = effectiveTint.copy(alpha = containerAlpha ?: tokens.containerAlpha)
    val shadowElevation = elevation ?: tokens.shadowElevation
    return this
        // 1. Sombra exterior nativa (clip = false para que no recorte abruptamente antes del clip oficial)
        .shadow(elevation = shadowElevation, shape = shape, clip = false)
        // 2. Recorte limpio de la geometría de la tarjeta
        .clip(shape)
        // 3. Manejo de click
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        // 4. CAPA DE VIDRIO UNIFICADA (auditoría #4: la clave de la doble tonalidad).
        //    · Con blur: el tinte viaja DENTRO del HazeStyle → Haze compone
        //      [base + contenido difuminado + tinte + grano] en UN solo render.
        //      El fondo NO se vuelve a pintar encima (adiós doble tonalidad).
        //      backgroundColor es OBLIGATORIO en la ruta RenderEffect de Haze
        //      1.2.x (require del fuente) — sin él: crash en API 31+.
        //    · Sin blur (estático): el tinte lo pintamos nosotros, igual que antes.
        .then(
            if (hazeState != null) {
                Modifier.hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        backgroundColor = effectiveTint,
                        tints = listOf(HazeTint(tintColor)),
                        blurRadius = HazeDefaults.blurRadius,
                        noiseFactor = HazeDefaults.noiseFactor
                    )
                )
            } else {
                Modifier.background(tintColor)
            }
        )
        // 5. Especular sutil y continuo
        .background(glassSpecularBrush(tokens))
        // 6. Borde de luz perimetral
        .border(width = 1.dp, brush = glassBorderBrush(tokens), shape = shape)
}
@Composable
fun Modifier.habitiaAmbient(): Modifier {
    val bg = MaterialTheme.colorScheme.background
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val isDark = bg.luminance() < 0.5f
    val blob1Alpha = if (isDark) 0.14f else 0.22f
    val blob2Alpha = if (isDark) 0.10f else 0.16f
    val blob3Alpha = if (isDark) 0.06f else 0.10f
    return this.drawBehind {
        drawRect(color = bg)
        // Blob 1 — PRIMARIO arriba a la derecha
        val c1 = Offset(size.width * 0.90f, size.height * 0.05f)
        val r1 = size.maxDimension * 0.75f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = blob1Alpha), Color.Transparent),
                center = c1,
                radius = r1
            ),
            radius = r1,
            center = c1
        )
        // Blob 2 — TERCIARIO abajo a la izquierda
        val c2 = Offset(size.width * 0.08f, size.height * 0.95f)
        val r2 = size.maxDimension * 0.80f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tertiary.copy(alpha = blob2Alpha), Color.Transparent),
                center = c2,
                radius = r2
            ),
            radius = r2,
            center = c2
        )
        // Blob 3 — SECUNDARIO en el centro (profundidad general)
        val c3 = Offset(size.width * 0.45f, size.height * 0.50f)
        val r3 = size.maxDimension * 0.90f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondary.copy(alpha = blob3Alpha), Color.Transparent),
                center = c3,
                radius = r3
            ),
            radius = r3,
            center = c3
        )
    }
}