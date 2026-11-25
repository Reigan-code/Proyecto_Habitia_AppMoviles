package upeu.edu.pe.Presentation.more

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import upeu.edu.pe.Core.components.HabitiaCard
import upeu.edu.pe.Core.theme.toComposeFontFamily
import upeu.edu.pe.Domain.model.AppFontFamily
import upeu.edu.pe.Domain.model.AppPalette
import upeu.edu.pe.Domain.model.UserThemePreference

@Composable
fun AppearanceSettingsSection(
    userPreference: UserThemePreference,
    onPreferenceChanged: (UserThemePreference) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Alternador Modo Oscuro
        HabitiaCard(
            modifier = Modifier.fillMaxWidth(),
            // Color del texto -> Core/theme/Color.kt (surfaceVariant) o cambia color= aqui
            // Color del contenedor -> Core/theme/Color.kt (surfaceVariant)
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (userPreference.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Modo Oscuro",
                            style = MaterialTheme.typography.titleMedium,
                            // Peso de letra -> FontWeight.Bold (cambialo aqui)
                            fontWeight = FontWeight.Bold,
                            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Adapta el contraste para entornos con poca luz",
                            style = MaterialTheme.typography.bodySmall,
                            // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = userPreference.isDarkMode,
                    onCheckedChange = { isDark ->
                        onPreferenceChanged(userPreference.copy(isDarkMode = isDark))
                    },
                    colors = SwitchDefaults.colors(
                        // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        // Color del texto -> Core/theme/Color.kt (primaryContainer) o cambia color= aqui
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        // 2. Selector de Tipografía
        Text(
            text = "Estilo de Tipografía",
            style = MaterialTheme.typography.titleMedium,
            // Peso de letra -> FontWeight.Bold (cambialo aqui)
            fontWeight = FontWeight.Bold,
            // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AppFontFamily.entries.toTypedArray()) { font ->
                val isSelected = userPreference.font == font
                FilterChip(
                    selected = isSelected,
                    onClick = { onPreferenceChanged(userPreference.copy(font = font)) },
                    label = {
                        Text(
                            text = font.displayName,
                            fontFamily = font.toComposeFontFamily(),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (isSelected) {
                        // Tamano -> cambia 16.dp aqui
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        // Color del texto -> Core/theme/Color.kt (primaryContainer) o cambia color= aqui
                        // Color del contenedor -> Core/theme/Color.kt (primaryContainer)
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        // Color del texto -> Core/theme/Color.kt (onPrimaryContainer) o cambia color= aqui
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // 3. Selector de Paleta de Colores
        Text(
            text = "Paleta de Colores Pastel",
            style = MaterialTheme.typography.titleMedium,
            // Peso de letra -> FontWeight.Bold (cambialo aqui)
            fontWeight = FontWeight.Bold,
            // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(340.dp)
        ) {
            items(AppPalette.entries.toTypedArray()) { palette ->
                val isSelected = userPreference.palette == palette

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Forma -> cambia radio 16.dp (mas alto=mas redondo)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onPreferenceChanged(userPreference.copy(palette = palette)) }
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            // Forma -> cambia radio 16.dp (mas alto=mas redondo)
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = palette.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                                tint = MaterialTheme.colorScheme.primary,
                                // Tamano -> cambia 18.dp aqui
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
