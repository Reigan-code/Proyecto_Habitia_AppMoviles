package upeu.edu.pe.Domain.model

import upeu.edu.pe.Core.theme.GeneratedPalette

/**
 * Paletas de colores disponibles en Habitia.
 * CUSTOM representa únicamente el tono único elegido en la rueda de color.
 */
enum class AppPalette(val displayName: String) {
    SAKURA("Sakura"),
    AUTUMN("Autumn"),
    LAVENDER("Lavender"),
    SAGE("Sage"),
    RAINBOW("Rainbow"),
    MINIMAL_BEIGE("Minimal Beige"),
    SKY_ICE("Sky Ice"),
    GOLDEN_BUTTER("Golden Butter"),
    CUSTOM("Personalizado")
}

/**
 * Tipografías soportadas en la aplicación.
 * Las 5 primeras usan familias genéricas del sistema (no requieren archivos);
 * las nuevas cargan sus archivos .ttf desde composeResources/font/
 * (descargados de Google Fonts).
 */
enum class AppFontFamily(val displayName: String) {
    QUICKSAND("Quicksand"),
    NUNITO("Nunito"),
    POPPINS("Poppins"),
    CAVEAT("Caveat"),
    COMFORTAA("Comfortaa"),
    MONTSERRAT("Montserrat"),
    RALEWAY("Raleway"),
    OPEN_SANS("Open Sans"),
    LATO("Lato"),
    PLAYFAIR_DISPLAY("Playfair Display"),
    PACIFICO("Pacifico"),
    SPACE_GROTESK("Space Grotesk"),
    DANCING_SCRIPT("Dancing Script"),
    JOSEFIN_SANS("Josefin Sans")
}

// Modelo que agrupa las preferencias actuales de la apariencia visual.
data class UserThemePreference(
    val palette: AppPalette = AppPalette.SAKURA,
    val font: AppFontFamily = AppFontFamily.QUICKSAND,
    val isDarkMode: Boolean = false,
    val generatedPaletteName: String? = null,
    val generatedPrimaryHex: String? = null,
    val generatedSecondaryHex: String? = null,
    val generatedTertiaryHex: String? = null,
    val generatedBackgroundHex: String? = null,
    val generatedSurfaceHex: String? = null,
    val savedPalettes: List<GeneratedPalette> = emptyList()
)
