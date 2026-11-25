package upeu.edu.pe.Data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import upeu.edu.pe.Core.theme.GeneratedPalette
import upeu.edu.pe.Core.theme.hexToComposeColor
import upeu.edu.pe.Core.theme.toHexFormat
import upeu.edu.pe.Domain.model.AppFontFamily
import upeu.edu.pe.Domain.model.AppPalette
import upeu.edu.pe.Domain.model.UserThemePreference
import upeu.edu.pe.Domain.repository.UserPreferencesRepository

// Extensión a nivel de archivo: DataStore debe ser UNO por proceso por nombre.
private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

/** DTO serializable para persistir paletas generadas (los Color se guardan como HEX). */
@Serializable
private data class SavedPaletteDto(
    val name: String,
    val primaryHex: String,
    val secondaryHex: String,
    val tertiaryHex: String,
    val backgroundHex: String,
    val surfaceHex: String
)

private fun GeneratedPalette.toDto() = SavedPaletteDto(
    name = name,
    primaryHex = primary.toHexFormat(),
    secondaryHex = secondary.toHexFormat(),
    tertiaryHex = tertiary.toHexFormat(),
    backgroundHex = background.toHexFormat(),
    surfaceHex = surface.toHexFormat()
)

private fun SavedPaletteDto.toDomain() = GeneratedPalette(
    name = name,
    primary = hexToComposeColor(primaryHex),
    secondary = hexToComposeColor(secondaryHex),
    tertiary = hexToComposeColor(tertiaryHex),
    background = hexToComposeColor(backgroundHex),
    surface = hexToComposeColor(surfaceHex)
)

/**
 * Implementación persistente (solo Android) del repositorio de preferencias visuales.
 * Guarda el tema en disco con DataStore: sobrevive al cierre de la app.
 *
 * Vive en androidMain porque usa android.content.Context. Se integrará en la capa
 * de Data cuando se implemente el backend con Clean Architecture.
 */
class DataStoreUserPreferencesRepositoryImpl(
    private val context: Context
) : UserPreferencesRepository {

    private object Keys {
        val PALETTE           = stringPreferencesKey("palette")
        val FONT              = stringPreferencesKey("font")
        val DARK_MODE         = booleanPreferencesKey("dark_mode")
        val GEN_NAME          = stringPreferencesKey("generated_name")
        val GEN_PRIMARY       = stringPreferencesKey("generated_primary")
        val GEN_SECONDARY     = stringPreferencesKey("generated_secondary")
        val GEN_TERTIARY      = stringPreferencesKey("generated_tertiary")
        val GEN_BACKGROUND    = stringPreferencesKey("generated_background")
        val GEN_SURFACE       = stringPreferencesKey("generated_surface")
        val SAVED_PALETTES    = stringPreferencesKey("saved_palettes")
    }

    override fun getThemePreference(): Flow<UserThemePreference> =
        context.themeDataStore.data.map { p ->
            UserThemePreference(
                palette = p[Keys.PALETTE]
                    ?.let { name -> AppPalette.entries.firstOrNull { it.name == name } }
                    ?: AppPalette.SAKURA,
                font = p[Keys.FONT]
                    ?.let { name -> AppFontFamily.entries.firstOrNull { it.name == name } }
                    ?: AppFontFamily.QUICKSAND,
                isDarkMode = p[Keys.DARK_MODE] ?: false,
                generatedPaletteName      = p[Keys.GEN_NAME],
                generatedPrimaryHex       = p[Keys.GEN_PRIMARY],
                generatedSecondaryHex     = p[Keys.GEN_SECONDARY],
                generatedTertiaryHex      = p[Keys.GEN_TERTIARY],
                generatedBackgroundHex    = p[Keys.GEN_BACKGROUND],
                generatedSurfaceHex       = p[Keys.GEN_SURFACE],
                savedPalettes = p[Keys.SAVED_PALETTES]
                    ?.let { json ->
                        runCatching {
                            Json.decodeFromString<List<SavedPaletteDto>>(json)
                                .map { it.toDomain() }
                        }.getOrDefault(emptyList())
                    } ?: emptyList()
            )
        }

    override suspend fun updatePalette(palette: AppPalette) {
        context.themeDataStore.edit { p ->
            p[Keys.PALETTE] = palette.name
            // Limpia la generada → la fija "gana" al observar
            p.remove(Keys.GEN_NAME)
            p.remove(Keys.GEN_PRIMARY)
            p.remove(Keys.GEN_SECONDARY)
            p.remove(Keys.GEN_TERTIARY)
            p.remove(Keys.GEN_BACKGROUND)
            p.remove(Keys.GEN_SURFACE)
        }
    }

    override suspend fun updateFont(font: AppFontFamily) {
        context.themeDataStore.edit { p -> p[Keys.FONT] = font.name }
    }

    override suspend fun updateDarkMode(isDark: Boolean) {
        context.themeDataStore.edit { p -> p[Keys.DARK_MODE] = isDark }
    }

    override suspend fun updateGeneratedPalette(
        name: String,
        primaryHex: String,
        secondaryHex: String,
        tertiaryHex: String,
        backgroundHex: String,
        surfaceHex: String
    ) {
        context.themeDataStore.edit { p ->
            p[Keys.GEN_NAME]       = name
            p[Keys.GEN_PRIMARY]    = primaryHex
            p[Keys.GEN_SECONDARY]  = secondaryHex
            p[Keys.GEN_TERTIARY]   = tertiaryHex
            p[Keys.GEN_BACKGROUND] = backgroundHex
            p[Keys.GEN_SURFACE]    = surfaceHex
        }
    }

    override suspend fun updateSavedPalettes(palettes: List<GeneratedPalette>) {
        context.themeDataStore.edit { p ->
            p[Keys.SAVED_PALETTES] = Json.encodeToString(palettes.map { it.toDto() })
        }
    }
}
