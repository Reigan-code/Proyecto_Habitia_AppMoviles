package upeu.edu.pe.Core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import kotlinx.serialization.Serializable
import upeu.edu.pe.Domain.model.AppPalette
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

// ============================================================
// 1. TONOS BASE COZY (#F4F1F8 como fondo blanco lácteo)
// ============================================================
val CozyBackground = Color(0xFFF4F1F8)       // <-- #F4F1F8 Solicitado
val CozySurface = Color(0xFFFFFFFF)          // Tarjetas en blanco puro
val CozySurfaceVariant = Color(0xFFEBE6F2)   // Elementos secundarios

// ============================================================
// 2. PALETAS PASTEL TOKENS
// ============================================================

// --- 🌸 01. SAKURA ---
val SakuraPrimary = Color(0xFFE99CA8)
val SakuraSecondary = Color(0xFFFBAFBB)
val SakuraTertiary = Color(0xFFFFC5CF)
val SakuraTextPrimary = Color(0xFF4A3437)
val SakuraTextSecondary = Color(0xFF8C6B71)

// --- 🌿 02. AUTUMN ---
val AutumnPrimary = Color(0xFFC6654D)
val AutumnSecondary = Color(0xFFD4E09B)
val AutumnTertiary = Color(0xFFCBDFBD)
val AutumnTextPrimary = Color(0xFF3E2723)
val AutumnTextSecondary = Color(0xFF6D4C41)

// --- 💜 03. LAVENDER ---
val LavenderPrimary = Color(0xFFC8B6FF)
val LavenderSecondary = Color(0xFFB8C0FF)
val LavenderTertiary = Color(0xFFBBD0FF)
val LavenderTextPrimary = Color(0xFF2B2140)
val LavenderTextSecondary = Color(0xFF5C527F)

// --- 🍃 04. SAGE ---
val SagePrimary = Color(0xFF8F9E4A)
val SageSecondary = Color(0xFFA9B95F)
val SageTertiary = Color(0xFFCAD988)
val SageTextPrimary = Color(0xFF2E351A)
val SageTextSecondary = Color(0xFF5A633B)

// --- 🌈 05. RAINBOW ---
val RainbowPrimary = Color(0xFFFF9AA2)
val RainbowSecondary = Color(0xFFB5EAD7)
val RainbowTertiary = Color(0xFFC7CEEA)
val RainbowTextPrimary = Color(0xFF333333)
val RainbowTextSecondary = Color(0xFF666666)

// --- 🍫 06. MINIMAL BEIGE ---
val BeigePrimary = Color(0xFFA48D78)
val BeigeSecondary = Color(0xFFCBB9A4)
val BeigeTertiary = Color(0xFFE6DAC8)
val BeigeTextPrimary = Color(0xFF3D352E)
val BeigeTextSecondary = Color(0xFF706256)

// --- 🧊 07. SKY ICE ---
val SkyIcePrimary = Color(0xFFAFC0D4)
val SkyIceSecondary = Color(0xFFBBD0DC)
val SkyIceTertiary = Color(0xFFC6D4DD)
val SkyIceTextPrimary = Color(0xFF212E36)
val SkyIceTextSecondary = Color(0xFF52636D)

// --- 💛 08. GOLDEN BUTTER ---
val ButterPrimary = Color(0xFFFBC66D)
val ButterSecondary = Color(0xFFF5DB95)
val ButterTertiary = Color(0xFFFBDC6D)
val ButterTextPrimary = Color(0xFF423219)
val ButterTextSecondary = Color(0xFF7A623B)

// ============================================================
// 3. UTILIDAD PARA CONVERTIR HEX A COLOR
// ============================================================
fun hexToComposeColor(hexString: String): Color {
    return try {
        val cleanHex = hexString.removePrefix("#")
        val longVal = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color(0xFF000000 or longVal)
        } else {
            Color(longVal)
        }
    } catch (e: Exception) {
        SakuraPrimary
    }
}

// ============================================================
// 4. MAPEO A MATERIAL 3 COLOR SCHEME
// ============================================================
fun AppPalette.toColorScheme(customColorHex: String = "E99CA8"): ColorScheme {
    return when (this) {
        AppPalette.SAKURA -> lightColorScheme(
            primary = SakuraPrimary,
            onPrimary = Color.White,
            primaryContainer = SakuraSecondary.copy(alpha = 0.35f),
            secondary = SakuraSecondary,
            tertiary = SakuraTertiary,
            background = CozyBackground,
            surface = CozySurface,
            surfaceVariant = CozySurfaceVariant,
            onBackground = SakuraTextPrimary,
            onSurface = SakuraTextPrimary,
            onSurfaceVariant = SakuraTextSecondary
        )
        AppPalette.AUTUMN -> lightColorScheme(
            primary = AutumnPrimary,
            onPrimary = Color.White,
            primaryContainer = AutumnSecondary.copy(alpha = 0.35f),
            secondary = AutumnSecondary,
            tertiary = AutumnTertiary,
            background = CozyBackground,
            surface = CozySurface,
            surfaceVariant = CozySurfaceVariant,
            onBackground = AutumnTextPrimary,
            onSurface = AutumnTextPrimary,
            onSurfaceVariant = AutumnTextSecondary
        )
        AppPalette.LAVENDER -> lightColorScheme(
            primary = LavenderPrimary,
            onPrimary = Color.White,
            primaryContainer = LavenderSecondary.copy(alpha = 0.35f),
            secondary = LavenderSecondary,
            tertiary = LavenderTertiary,
            background = CozyBackground,
            surface = CozySurface,
            surfaceVariant = CozySurfaceVariant,
            onBackground = LavenderTextPrimary,
            onSurface = LavenderTextPrimary,
            onSurfaceVariant = LavenderTextSecondary
        )
        AppPalette.SAGE -> lightColorScheme(
            primary = SagePrimary,
            onPrimary = Color.White,
            primaryContainer = SageSecondary.copy(alpha = 0.35f),
            secondary = SageSecondary,
            tertiary = SageTertiary,
            background = CozyBackground,
            surface = CozySurface,
            surfaceVariant = CozySurfaceVariant,
            onBackground = SageTextPrimary,
            onSurface = SageTextPrimary,
            onSurfaceVariant = SageTextSecondary
        )
        AppPalette.RAINBOW -> lightColorScheme(
            primary = RainbowPrimary,
            onPrimary = Color.White,
            primaryContainer = RainbowSecondary.copy(alpha = 0.35f),
            secondary = RainbowSecondary,
            tertiary = RainbowTertiary,
            background = CozyBackground,
            surface = CozySurface,
            surfaceVariant = CozySurfaceVariant,
            onBackground = RainbowTextPrimary,
            onSurface = RainbowTextPrimary,
            onSurfaceVariant = RainbowTextSecondary
        )
        AppPalette.MINIMAL_BEIGE -> lightColorScheme(
            primary = BeigePrimary,
            onPrimary = Color.White,
            primaryContainer = BeigeSecondary.copy(alpha = 0.35f),
            secondary = BeigeSecondary,
            tertiary = BeigeTertiary,
            background = CozyBackground,
            surface = CozySurface,
            surfaceVariant = CozySurfaceVariant,
            onBackground = BeigeTextPrimary,
            onSurface = BeigeTextPrimary,
            onSurfaceVariant = BeigeTextSecondary
        )
        AppPalette.SKY_ICE -> lightColorScheme(
            primary = SkyIcePrimary,
            onPrimary = Color.White,
            primaryContainer = SkyIceSecondary.copy(alpha = 0.35f),
            secondary = SkyIceSecondary,
            tertiary = SkyIceTertiary,
            background = CozyBackground,
            surface = CozySurface,
            surfaceVariant = CozySurfaceVariant,
            onBackground = SkyIceTextPrimary,
            onSurface = SkyIceTextPrimary,
            onSurfaceVariant = SkyIceTextSecondary
        )
        AppPalette.GOLDEN_BUTTER -> lightColorScheme(
            primary = ButterPrimary,
            onPrimary = Color.White,
            primaryContainer = ButterSecondary.copy(alpha = 0.35f),
            secondary = ButterSecondary,
            tertiary = ButterTertiary,
            background = CozyBackground,
            surface = CozySurface,
            surfaceVariant = CozySurfaceVariant,
            onBackground = ButterTextPrimary,
            onSurface = ButterTextPrimary,
            onSurfaceVariant = ButterTextSecondary
        )
        // --- 🎨 RAMA CUSTOM: Rueda Cromática Personalizada ---
        AppPalette.CUSTOM -> {
            val customColor = hexToComposeColor(customColorHex)
            lightColorScheme(
                primary = customColor,
                onPrimary = Color.White,
                primaryContainer = customColor.copy(alpha = 0.25f),
                secondary = customColor.copy(alpha = 0.70f),
                tertiary = customColor.copy(alpha = 0.45f),
                background = CozyBackground,
                surface = CozySurface,
                surfaceVariant = CozySurfaceVariant,
                onBackground = Color(0xFF2D2633),
                onSurface = Color(0xFF2D2633),
                onSurfaceVariant = Color(0xFF766D80)
            )
        }
    }
}

data class GeneratedPalette(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color = Color(0xFFF4F1F8),
    val surface: Color = Color(0xFFFFFFFF),
    val onSurface: Color = Color(0xFF2D2633),
    val onSurfaceVariant: Color = Color(0xFF766D80)
) {
    fun toColorScheme(): ColorScheme {
        return lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.20f),
            onPrimaryContainer = primary,
            secondary = secondary,
            tertiary = tertiary,
            background = background,
            surface = surface,
            surfaceVariant = Color(0xFFEBE6F2),
            onBackground = onSurface,
            onSurface = onSurface,
            onSurfaceVariant = onSurfaceVariant
        )
    }
}

/**
 * Convierte un Color a código HEX (#RRGGBB).
 */
fun Color.toHexFormat(): String {
    val r = (red * 255).roundToInt().coerceIn(0, 255)
    val g = (green * 255).roundToInt().coerceIn(0, 255)
    val b = (blue * 255).roundToInt().coerceIn(0, 255)
    return "#" + ((r shl 16) or (g shl 8) or b).toString(16).padStart(6, '0').uppercase()
}

// ============================================================
// 5. PALETAS PERSONALIZADAS Y FONDOS (HEX MANUAL)
// ============================================================

/** Valida un código HEX (#RRGGBB o RRGGBB). */
fun isValidHexColor(hex: String): Boolean {
    val clean = hex.trim().removePrefix("#")
    if (clean.length != 6) return false
    return clean.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
}

/**
 * Crea una paleta personalizada desde códigos HEX.
 * Devuelve null si alguno de los códigos es inválido.
 */
fun createCustomPalette(
    name: String,
    primaryHex: String,
    secondaryHex: String,
    tertiaryHex: String,
    backgroundHex: String,
    surfaceHex: String
): GeneratedPalette? {
    if (!isValidHexColor(primaryHex) || !isValidHexColor(secondaryHex) ||
        !isValidHexColor(tertiaryHex) || !isValidHexColor(backgroundHex) ||
        !isValidHexColor(surfaceHex)
    ) return null

    return GeneratedPalette(
        name = name.trim().ifBlank { "Mi Paleta" },
        primary = hexToComposeColor(primaryHex),
        secondary = hexToComposeColor(secondaryHex),
        tertiary = hexToComposeColor(tertiaryHex),
        background = hexToComposeColor(backgroundHex),
        surface = hexToComposeColor(surfaceHex)
    )
}

/** Fondos suaves predefinidos para la sección "Fondo de la Aplicación". */
val presetBackgrounds = listOf(
    "#F4F1F8" to "Blanco Lácteo",
    "#FFF1F2" to "Rosa Suave",
    "#EFF6FF" to "Azul Cielo",
    "#ECFDF5" to "Menta",
    "#F5F3FF" to "Lavanda",
    "#FFF7ED" to "Durazno",
    "#FAF5E9" to "Beige",
    "#F1F5F9" to "Gris Perla"
)

/**
 * Motor Algorítmico de Armonía de Color ( Coolors Engine).
 * Emplea saturaciones pastel controladas (40% - 60%) y alta luminosidad (80% - 90%)
 * para que NUNCA genere colores chillones o desagradables.
 * Ahora TAMBIÉN genera fondo y superficie armónicos derivados del tono base.
 */
object CoolorsGenerator {

    private fun hsv(h: Float, s: Float, v: Float): Color {
        val hue = (h % 360f + 360f) % 360f
        val c = v * s
        val x = c * (1f - abs((hue / 60f) % 2f - 1f))
        val m = v - c
        val (r, g, b) = when ((hue / 60f).toInt()) {
            0 -> Triple(c, x, 0f)
            1 -> Triple(x, c, 0f)
            2 -> Triple(0f, c, x)
            3 -> Triple(0f, x, c)
            4 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        return Color(r + m, g + m, b + m)
    }

    /**
     * Genera una nueva paleta estética de 5 tonos basada en reglas de diseño:
     * - Análoga suave o complementaria armónica.
     * - Fondo y superficie tintados muy suavemente del tono base.
     */
    fun generateHarmoniousPalette(): GeneratedPalette {
        val baseHue = Random.nextFloat() * 360f
        val harmonyType = Random.nextInt(3) // 0: Análoga, 1: Triádica Pastel, 2: Monocromática Rica

        val (secHue, tertHue) = when (harmonyType) {
            0 -> Pair((baseHue + 28f) % 360f, (baseHue + 56f) % 360f) // Análoga: máxima calidez y paz
            1 -> Pair((baseHue + 120f) % 360f, (baseHue + 240f) % 360f) // Triádica suave
            else -> Pair((baseHue + 180f) % 360f, (baseHue + 25f) % 360f) // Complementario delicado
        }

        val primary = hsv(baseHue, 0.52f, 0.90f)
        val secondary = hsv(secHue, 0.42f, 0.92f)
        val tertiary = hsv(tertHue, 0.35f, 0.95f)
        // NUEVO: fondo armónico muy claro (para no competir con el contenido)
        // y superficie casi blanca con un toque del mismo tono.
        val background = hsv(baseHue, 0.22f, 0.96f)
        val surface = hsv(baseHue, 0.10f, 0.99f)

        val names = listOf(
            "Serenity Mist", "Velvet Bloom", "Golden Cloud", "Nordic Whisper",
            "Matcha Dream", "Sunset Silk", "Lofi Morning", "Pastel Breeze"
        )

        return GeneratedPalette(
            name = names.random(),
            primary = primary,
            secondary = secondary,
            tertiary = tertiary,
            background = background,
            surface = surface
        )
    }

    /**
     * Colección curada de paletas de nivel profesional (reemplazan las anteriores).
     */
    val curatedPalettes = listOf(
        GeneratedPalette(
            name = "French Sakura",
            primary = Color(0xFFE58C9D),
            secondary = Color(0xFFF8BDC8),
            tertiary = Color(0xFFFFDDE3)
        ),
        GeneratedPalette(
            name = "Matcha Latte",
            primary = Color(0xFF7FA874),
            secondary = Color(0xFFA5C89C),
            tertiary = Color(0xFFD6E8D0)
        ),
        GeneratedPalette(
            name = "Nordic Fjord",
            primary = Color(0xFF7B9EBD),
            secondary = Color(0xFFA6C5DC),
            tertiary = Color(0xFFD3E4EE)
        ),
        GeneratedPalette(
            name = "Cozy Espresso",
            primary = Color(0xFF9E7E6A),
            secondary = Color(0xFFC7B19F),
            tertiary = Color(0xFFEFE6DE)
        ),
        GeneratedPalette(
            name = "Lavender Dew",
            primary = Color(0xFF9A86C8),
            secondary = Color(0xFFBFAFDD),
            tertiary = Color(0xFFE4DCF3)
        ),
        GeneratedPalette(
            name = "Peach Glaze",
            primary = Color(0xFFE38E74),
            secondary = Color(0xFFF5B6A4),
            tertiary = Color(0xFFFCE1DA)
        )
    )
}
