package upeu.edu.pe.dev

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import upeu.edu.pe.Core.theme.HabitiaTheme
import upeu.edu.pe.Presentation.dev.GlassDiagnosticScreen

/**
 * Preview de Android Studio para el laboratorio de diagnóstico del sistema glass.
 *
 * Notas (verificadas contra este proyecto):
 * - @Preview vive SOLO en androidMain (uiToolingPreview está ahí, NO en
 *   commonMain). Por eso este archivo es JVM-Android: aquí sí compila.
 * - En Preview el blur de Haze no se ejecuta: verás A ≡ B (glass estático) —
 *   esa equivalencia es parte del test. La diferencia real con emulador físico
 *   (blur activo en API 31+) es lo que confirma si Haze captura bien el fondo.
 */
@Preview(showBackground = true, showSystemUi = true, name = "Glass Lab")
@Composable
private fun GlassDiagnosticPreview() {
    HabitiaTheme { GlassDiagnosticScreen() }
}
