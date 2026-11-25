package upeu.edu.pe.Presentation.UserMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import androidx.compose.ui.unit.sp
import upeu.edu.pe.Core.components.HabitiaGlassCard
import upeu.edu.pe.Core.theme.GeneratedPalette
import upeu.edu.pe.Core.theme.createCustomPalette
import upeu.edu.pe.Core.theme.hexToComposeColor
import upeu.edu.pe.Core.theme.isValidHexColor
import upeu.edu.pe.Core.theme.presetBackgrounds
import upeu.edu.pe.Core.theme.toHexFormat

private fun contrastFor(background: Color): Color =
    if (background.red + background.green + background.blue > 2.0f) {
        Color.Black.copy(alpha = 0.7f)
    } else {
        Color.White
    }

/**
 * Sección "Crea tu Paleta": el usuario ingresa sus propios códigos HEX para
 * primario, secundario, terciario, fondo y superficie, con vista previa en vivo.
 * Puede Aplicarla al instante o Guardarla en "Mis Paletas".
 */
@Composable
fun CustomPaletteCreator(
    onApply: (GeneratedPalette) -> Unit,
    onSave: (GeneratedPalette) -> Unit,
    hazeState: HazeState? = null
) {
    var paletteName by remember { mutableStateOf("Mi Paleta") }
    var primaryHex by remember { mutableStateOf("#E99CA8") }
    var secondaryHex by remember { mutableStateOf("#FBAFBB") }
    var tertiaryHex by remember { mutableStateOf("#FFC5CF") }
    var backgroundHex by remember { mutableStateOf("#F4F1F8") }
    var surfaceHex by remember { mutableStateOf("#FFFFFF") }

    val allValid = isValidHexColor(primaryHex) &&
            isValidHexColor(secondaryHex) &&
            isValidHexColor(tertiaryHex) &&
            isValidHexColor(backgroundHex) &&
            isValidHexColor(surfaceHex)

    val preview = if (allValid) {
        createCustomPalette(paletteName, primaryHex, secondaryHex, tertiaryHex, backgroundHex, surfaceHex)
    } else null

    // Tarjeta GLASS ESTÁTICA (sin blur): sobre el fondo plano del perfil basta
    // el tinte translúcido + borde de luz + especular. La vista previa de los
    // 5 colores HEX queda flotando sobre el vidrio.
    HabitiaGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        hazeState = hazeState
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Crea tu Paleta (HEX)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            OutlinedTextField(
                value = paletteName,
                onValueChange = { paletteName = it.take(20) },
                label = { Text("Nombre de tu paleta") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // ---- Vista previa en vivo (franja de 5 colores) ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                val strip = listOfNotNull(
                    preview?.primary,
                    preview?.secondary,
                    preview?.tertiary,
                    preview?.background,
                    preview?.surface
                ).ifEmpty { List(5) { MaterialTheme.colorScheme.surfaceVariant } }

                strip.forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(color),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = color.toHexFormat().takeLast(4),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = contrastFor(color),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            HexColorField(label = "Primario", value = primaryHex, onValueChange = { primaryHex = it })
            HexColorField(label = "Secundario", value = secondaryHex, onValueChange = { secondaryHex = it })
            HexColorField(label = "Terciario", value = tertiaryHex, onValueChange = { tertiaryHex = it })
            HexColorField(label = "Fondo de la app (background)", value = backgroundHex, onValueChange = { backgroundHex = it })
            HexColorField(label = "Tarjetas (surface)", value = surfaceHex, onValueChange = { surfaceHex = it })

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { preview?.let(onApply) },
                    enabled = allValid,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Aplicar")
                }
                OutlinedButton(
                    onClick = { preview?.let(onSave) },
                    enabled = allValid,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar")
                }
            }
        }
    }
}

/** Campo de texto para códigos HEX con punto de color de vista previa y validación. */
@Composable
private fun HexColorField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val valid = isValidHexColor(value)
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(7)) },
        label = { Text(label) },
        singleLine = true,
        isError = !valid,
        supportingText = {
            if (!valid) Text("Formato válido: #RRGGBB (ej. #E99CA8)")
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (valid) hexToComposeColor(value) else MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Sección "Fondo de la Aplicación": fondos suaves predefinidos que se aplican
 * sobre la paleta activa (conserva sus colores, cambia el fondo de toda la app).
 */
@Composable
fun BackgroundSelector(
    onBackgroundSelected: (hex: String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Wallpaper, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                text = "Fondo de la Aplicación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(presetBackgrounds) { (hex, label) ->
                Card(
                    modifier = Modifier
                        .size(width = 110.dp, height = 72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onBackgroundSelected(hex) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = hexToComposeColor(hex))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = contrastFor(hexToComposeColor(hex))
                        )
                    }
                }
            }
        }
    }
}
