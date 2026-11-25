package upeu.edu.pe.Core.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import upeu.edu.pe.Core.config.IslandConfig
import upeu.edu.pe.Core.theme.habitiaGlass

/**
 * =============================================================================
 * ISLA FLOTANTE (Floating Pill Tab Bar)
 * =============================================================================
 * Se implementa como un Row propio en lugar de NavigationBar.
 *
 * POR QUÉ: NavigationBar de Material3 mide sus items con 80dp FIJOS por
 * dentro y no expone ninguna propiedad para cambiar la altura; al forzar
 * `Modifier.height(64.dp)` el contenido se recortaba y la isla se veía
 * descuadrada. Con un Row propio controlamos altura, centrado y paddings
 * exactos, y TODOS los valores salen de IslandConfig
 * (Core/config/UiConfig.kt) para que puedas ajustarlos ahí.
 *
 * Vidrio: la superficie la dibuja habitiaGlass con
 * IslandConfig.containerAlpha (0.72 = translúcida, se aprecia el efecto
 * vidrio). El tinte de los tabs sigue derivado de la paleta activa del
 * usuario (cambia de color al cambiar de tema en Personalizar).
 *
 * Animaciones:
 *  - Color del ícono/texto del tab: tween(300ms) al cambiar de pestaña.
 *  - Entrada/salida de la isla completa: la maneja App.kt con
 *    AnimatedVisibility (slide vertical + fade) y zIndex alto.
 */
@Composable
fun HabitiaBottonNavigation(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = IslandConfig.sideMargin)
            .habitiaGlass(
                shape = RoundedCornerShape(IslandConfig.cornerRadius),
                containerAlpha = IslandConfig.containerAlpha,
                elevation = IslandConfig.elevation
            )
            .height(IslandConfig.height),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomTab.allTabs.forEach { tab ->
            val isSelected = currentTab == tab

            val animatedTintColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(durationMillis = 300),
                label = "bottomTabColor"
            )

            // Tab = Column centrada (pastilla del ícono + etiqueta).
            // weight(1f) reparte el ancho en partes iguales y fillMaxHeight
            // centra el contenido dentro de la altura exacta de la isla.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // sin ripple: la pastilla activa ya indica selección
                    ) { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Pastilla del ícono activo (tinte primary); inactivos solo ícono.
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(IslandConfig.iconPillCorner))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = IslandConfig.iconPillAlpha)
                            else Color.Transparent
                        )
                        .padding(
                            horizontal = IslandConfig.iconPillPaddingH,
                            vertical = IslandConfig.iconPillPaddingV
                        )
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = animatedTintColor,
                        modifier = Modifier.size(IslandConfig.iconSize)
                    )
                }
                Spacer(Modifier.height(IslandConfig.labelSpacing))
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = animatedTintColor
                )
            }
        }
    }
}
