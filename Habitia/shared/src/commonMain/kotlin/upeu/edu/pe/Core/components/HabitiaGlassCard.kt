package upeu.edu.pe.Core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import upeu.edu.pe.Core.theme.habitiaGlass



/**
 * Tarjeta de vidrio estilo Liquid Glass.
 * Provee la superficie translúcida unificada sin capas intermedias que rompan el radio o la tonalidad.
 */
@Composable
fun HabitiaGlassCard(
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    shape: Shape = RoundedCornerShape(20.dp),
    containerAlpha: Float? = null,
    tint: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    elevation: Dp? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.habitiaGlass(
            hazeState = hazeState,
            shape = shape,
            containerAlpha = containerAlpha,
            tint = tint,
            elevation = elevation,
            onClick = onClick
        )
    ) {
        // Capa de contenido sin fondos secundarios agregados
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}