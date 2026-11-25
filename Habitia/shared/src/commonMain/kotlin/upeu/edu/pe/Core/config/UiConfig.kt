package upeu.edu.pe.Core.config

import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════════════════
// UiConfig — PANEL DE AJUSTES VISUALES DE HABITIA
// ═══════════════════════════════════════════════════════════════════════════
// TODOS los valores de la Isla (barra inferior) y de Haabi (mascota + chat)
// viven AQUÍ para que puedas cambiarlos sin buscar en el código.
//
// Cómo funciona: cada valor es un `val` de tipo Dp/Float/Boolean. Cambia el
// número, recompila (o Hot Reload) y la app toma el nuevo valor.
//
// ── Guía rápida de ajuste ──────────────────────────────────────────────────
//  ¿Quieres la isla más baja?            → IslandConfig.height
//  ¿Isla más ancha/estrecha?             → IslandConfig.sideMargin (menor = más ancha)
//  ¿Isla más transparente (más vidrio)?  → IslandConfig.containerAlpha (menor = más vidrio)
//  ¿Sombra más fuerte?                   → IslandConfig.elevation
//  ¿Mascota más grande?                  → HaabiConfig.mascotSize
//  ¿Mascota más arriba/abajo?            → HaabiConfig.bottomOffset (mayor = más arriba)
//  ¿Mascota dentro de la tarjeta de rituales? → HaabiConfig.placement
// ═══════════════════════════════════════════════════════════════════════════


object IslandConfig {
    // ALTURA de la isla. Antes NavigationBar imponía 80dp internos y no se
    // podía controlar (se recortaba el contenido); con el Row propio sí.
    // 56dp = compacta · 64dp = recomendada · 72dp = holgada
    val height = 64.dp

    // Margen horizontal (a cada lado). MENOR valor = isla MÁS ANCHA.
    // 32dp = ancha · 48dp = equilibrada · 64dp = estrecha
    val sideMargin = 48.dp

    // Radio de esquina. Para píldora perfecta debe ser height / 2 (32dp
    // cuando height = 64dp). Un valor mayor se recorta al mismo resultado.
    val cornerRadius = 32.dp

    // TRANSPARENCIA del vidrio. MENOR = más efecto vidrio real (se ve el
    // contenido pasar detrás). 0.60 = muy translúcida · 0.72 = recomendada ·
    // 0.90 = casi sólida (máxima legibilidad de los tabs)
    val containerAlpha = 0.72f

    // Sombra/flotación de la isla sobre el contenido. 8dp suave · 12dp
    // recomendado · 16dp marcada
    val elevation = 12.dp

    // Separación entre el borde inferior de la pantalla y la isla (App.kt).
    // 16dp pegada al borde · 32dp recomendado
    val bottomSpacing = 32.dp

    // Padding inferior de los LazyColumn para que el último contenido
    // ("Consejos para hoy", etc.) no quede tapado por la isla.
    // Debe ser >= height + bottomSpacing (64 + 32 = 96) más un margen de aire.
    val contentBottomPadding = 176.dp

    // Tamaño del ícono de cada tab (dp).
    val iconSize = 24.dp

    // Pastilla que resalta el tab activo:
    // alpha del tinte primary detrás del ícono activo.
    val iconPillAlpha = 0.22f
    // Radio de esa pastilla.
    val iconPillCorner = 18.dp
    // Padding interno de la pastilla (horizontal y vertical).
    val iconPillPaddingH = 14.dp
    val iconPillPaddingV = 4.dp
    // Espacio entre la pastilla del ícono y la etiqueta de texto.
    val labelSpacing = 2.dp
}

/** Dónde vive la mascota Haabi. Cambia aquí y toda la app obedece. */
enum class HaabiPlacement {
    /** Flotante global: esquina inferior derecha en TODAS las pantallas,
     *  por encima de la isla. Tocarla abre el chat. (Recomendado) */
    GLOBAL,

    /** Anidada dentro de la tarjeta "Tus rituales diarios" de HomeScreen. */
    INSIDE_RITUAL_CARD
}

/**
 * Configuración de Haabi (mascota + chat).
 * La implementa HaabiCompanion (Core/components) + HomeScreen si placement
 * = INSIDE_RITUAL_CARD.
 */
object HaabiConfig {
    // Ubicación de la mascota (ver enum HaabiPlacement arriba).
    val placement = HaabiPlacement.GLOBAL

    // Tamaño de la mascota. 90dp pequeña · 110dp recomendada · 140dp grande
    val mascotSize = 110.dp

    // Corazones flotantes alrededor de la mascota.
    val showHearts = true

    // Posición de la mascota GLOBAL (solo se usa en GLOBAL):
    // margen desde el borde derecho…
    val marginEnd = 16.dp
    // …y desde el borde INFERIOR. Debe superar a la isla
    // (IslandConfig.height + IslandConfig.bottomSpacing = 96dp) para no
    // quedar encima de los tabs. 112dp = justo encima de la isla.
    val bottomOffset = 112.dp

    // Chat (Dialog centrado, sin offsets):
    // ancho del dialog como fracción de la pantalla (0.92 = 92%).
    val chatDialogWidthFraction = 0.92f
    // Altura máxima total del dialog.
    val chatDialogMaxHeight = 500.dp
    // Altura máxima de la lista de mensajes.
    val chatListMaxHeight = 300.dp
    // Radio de esquina del dialog del chat.
    val chatCorner = 20.dp
}
