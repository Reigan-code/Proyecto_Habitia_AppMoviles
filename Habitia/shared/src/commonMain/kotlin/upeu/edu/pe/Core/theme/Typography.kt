package upeu.edu.pe.Core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
// Recursos generados por Compose Multiplatform (paquete derivado de
// rootProject.name="Habitia" + módulo "shared"). El wildcard importa el objeto
// Res y las propiedades de extensión Res.font.xxx (montserrat_regular, etc.),
// que Kotlin exige importar de forma explícita. Además, así las futuras fuentes
// nuevas no requerirán tocar estos imports.
import habitia.shared.generated.resources.*
import upeu.edu.pe.Domain.model.AppFontFamily

/**
 * Mapea la enumeración de tipografía a su familia de fuentes real.
 *
 * - Las 5 originales usan familias genéricas del sistema (sin archivos extra).
 * - Las nuevas cargan sus archivos .ttf desde composeResources/font/
 *   (los accesores Res.font.xxx se generan automáticamente al compilar).
 */
@Composable
fun AppFontFamily.toComposeFontFamily(): FontFamily {
    return when (this) {
        // --- Originales (familias genéricas del sistema) ---
        AppFontFamily.QUICKSAND -> FontFamily.SansSerif
        AppFontFamily.NUNITO -> FontFamily.SansSerif
        AppFontFamily.POPPINS -> FontFamily.Default
        AppFontFamily.CAVEAT -> FontFamily.Cursive
        AppFontFamily.COMFORTAA -> FontFamily.Monospace
        // --- Nuevas (archivos .ttf en composeResources/font/) ---
        AppFontFamily.MONTSERRAT -> FontFamily(
            Font(Res.font.montserrat_regular),
            Font(Res.font.montserrat_bold, FontWeight.Bold)
        )
        AppFontFamily.RALEWAY -> FontFamily(
            Font(Res.font.raleway_regular),
            Font(Res.font.raleway_bold, FontWeight.Bold)
        )
        AppFontFamily.OPEN_SANS -> FontFamily(
            Font(Res.font.opensans_regular),
            Font(Res.font.opensans_bold, FontWeight.Bold)
        )
        AppFontFamily.LATO -> FontFamily(
            Font(Res.font.lato_regular),
            Font(Res.font.lato_bold, FontWeight.Bold)
        )
        AppFontFamily.PLAYFAIR_DISPLAY -> FontFamily(
            Font(Res.font.playfairdisplay_regular),
            Font(Res.font.playfairdisplay_bold, FontWeight.Bold)
        )
        AppFontFamily.PACIFICO -> FontFamily(
            Font(Res.font.pacifico_regular) // Pacifico solo tiene peso Regular
        )
        AppFontFamily.SPACE_GROTESK -> FontFamily(
            Font(Res.font.spacegrotesk_regular),
            Font(Res.font.spacegrotesk_bold, FontWeight.Bold)
        )
        AppFontFamily.DANCING_SCRIPT -> FontFamily(
            Font(Res.font.dancingscript_regular),
            Font(Res.font.dancingscript_bold, FontWeight.Bold)
        )
        AppFontFamily.JOSEFIN_SANS -> FontFamily(
            Font(Res.font.josefinsans_regular),
            Font(Res.font.josefinsans_bold, FontWeight.Bold)
        )
    }
}

/**
 * Jerarquía tipográfica responsiva: tamaños moderados con lineHeight en TODOS
 * los estilos para que ningún texto se desborde en pantallas pequeñas ni
 * quede diminuto en tablets. Todo escala en sp (respeta el tamaño de fuente
 * configurado por el usuario en el sistema operativo).
 */
@Composable
fun AppFontFamily.toTypography(): Typography {
    val family = this.toComposeFontFamily()

    return Typography(
        displaySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 36.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.4).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 26.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 22.sp
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 15.sp
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.3.sp
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            letterSpacing = 0.2.sp
        )
    )
}
