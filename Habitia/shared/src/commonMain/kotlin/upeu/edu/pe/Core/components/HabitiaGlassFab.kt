package upeu.edu.pe.Core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import upeu.edu.pe.Core.theme.glassBorderBrush
import upeu.edu.pe.Core.theme.glassSpecularBrush
import upeu.edu.pe.Core.theme.glassTokens

/**
 * FAB de vidrio: mantiene el color primario de la paleta activa (contraste
 * alto para acción principal) pero con tratamiento glass: borde de luz,
 * brillo especular y sombra suave. Touch target 58dp (>= 48dp, regla Android).
 */

@Composable
fun HabitiaGlassFab(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val tokens = glassTokens()
    val shape = RoundedCornerShape(18.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(58.dp)
            .shadow(elevation = 4.dp, shape = shape, clip = false)
            .clip(shape)
            .clickable(onClick = onClick)
            .background(containerColor.copy(alpha = 0.88f))
            .background(glassSpecularBrush(tokens))
            .border(width = 1.dp, brush = glassBorderBrush(tokens), shape = shape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor
        )
    }
}