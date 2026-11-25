package upeu.edu.pe.Core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════
// HabitiaScreenContainer — wrapper GLOBAL de safe-areas.
//
// Centraliza las reglas de espaciado para TODAS las pantallas:
//   • Top:    inset de la status bar (no renderiza bajo reloj/batería).
//   • Bottom: BottomNav (80dp) + FAB (56dp) + safe-area inferior + 16dp.
//   • Horizontal: 16dp uniforme en los bordes.
//
// Uso:
//   HabitiaScreenContainer(hasFab = true) { padding ->
//       LazyColumn(contentPadding = padding) { ... }
//   }
// ═══════════════════════════════════════════════════════════════════════════
object HabitiaInsets {
    val bottomNavHeight: Dp = 80.dp
    val fabSize: Dp = 56.dp
    val horizontalMargin: Dp = 16.dp
    val extraMargin: Dp = 16.dp
}

@Composable
fun HabitiaScreenContainer(
    modifier: Modifier = Modifier,
    hasFab: Boolean = true,
    horizontalPadding: Dp = HabitiaInsets.horizontalMargin,
    content: @Composable (PaddingValues) -> Unit
) {
    val statusBarTop = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }
    val systemBottom = with(LocalDensity.current) { WindowInsets.navigationBars.getBottom(this).toDp() }

    val fabSpace = if (hasFab) HabitiaInsets.fabSize + HabitiaInsets.extraMargin else 0.dp
    val bottomPadding = HabitiaInsets.bottomNavHeight +
            fabSpace +
            systemBottom +
            HabitiaInsets.extraMargin

    val padding = PaddingValues(
        top = statusBarTop,
        bottom = bottomPadding,
        start = horizontalPadding,
        end = horizontalPadding
    )

    Box(modifier = modifier.fillMaxSize()) {
        content(padding)
    }
}
