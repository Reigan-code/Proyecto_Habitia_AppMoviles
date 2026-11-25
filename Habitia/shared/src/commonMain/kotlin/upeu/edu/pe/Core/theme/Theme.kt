package upeu.edu.pe.Core.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import upeu.edu.pe.Domain.model.UserThemePreference

fun ColorScheme.toDarkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary            = this.primary,
        onPrimary          = Color(0xFF1A1A2E),
        primaryContainer   = this.primary.copy(alpha = 0.30f),
        onPrimaryContainer = this.primary,
        secondary          = this.secondary,
        onSecondary        = Color(0xFF1A1A2E),
        secondaryContainer = this.secondary.copy(alpha = 0.22f),
        tertiary           = this.tertiary,
        onTertiary         = Color(0xFF1A1A2E),
        background         = Color(0xFF141318),
        surface            = Color(0xFF1F1D24),
        surfaceVariant     = Color(0xFF2B2833),
        onBackground       = Color(0xFFEFECE9),
        onSurface          = Color(0xFFEFECE9),
        onSurfaceVariant   = Color(0xFFABA4B5),
        outline            = Color(0xFF6E6B78)
    )
}

@Composable
fun HabitiaTheme(
    userPreference: UserThemePreference = UserThemePreference(),
    activeGeneratedPalette: GeneratedPalette? = null,
    content: @Composable () -> Unit
) {
    val baseColorScheme = if (activeGeneratedPalette != null) {
        activeGeneratedPalette.toColorScheme()
    } else {
        userPreference.palette.toColorScheme()
    }
    val colorScheme = if (userPreference.isDarkMode) {
        baseColorScheme.toDarkColorScheme()
    } else {
        baseColorScheme
    }
    val typography = userPreference.font.toTypography()
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = typography,
        shapes      = Shapes(),
        content     = content
    )
}
