package upeu.edu.pe.Data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import upeu.edu.pe.Core.theme.GeneratedPalette
import upeu.edu.pe.Domain.model.AppFontFamily
import upeu.edu.pe.Domain.model.AppPalette
import upeu.edu.pe.Domain.model.UserThemePreference
import upeu.edu.pe.Domain.repository.UserPreferencesRepository

/**
 * Implementación en memoria de [UserPreferencesRepository].
 *
 * Multiplataforma pura (sin APIs de Android), por eso vive en commonMain.
 * No persiste en disco: los valores viven solo mientras la app está abierta,
 * igual que el resto de repositorios Mock del proyecto.
 *
 * Cuando se conecte la capa de Data real (Clean Architecture + backend),
 * la versión con persistencia está en androidMain:
 * [DataStoreUserPreferencesRepositoryImpl].
 */
class MockUserPreferencesRepositoryImpl : UserPreferencesRepository {

    private val preferences = MutableStateFlow(UserThemePreference())

    override fun getThemePreference(): Flow<UserThemePreference> = preferences.asStateFlow()

    override suspend fun updatePalette(palette: AppPalette) {
        // Al elegir una paleta fija se limpia la generada: la generada "gana"
        // al observar y la fija nunca se vería si quedara guardada.
        preferences.update {
            it.copy(
                palette = palette,
                generatedPaletteName = null,
                generatedPrimaryHex = null,
                generatedSecondaryHex = null,
                generatedTertiaryHex = null,
                generatedBackgroundHex = null,
                generatedSurfaceHex = null
            )
        }
    }

    override suspend fun updateFont(font: AppFontFamily) {
        preferences.update { it.copy(font = font) }
    }

    override suspend fun updateDarkMode(isDark: Boolean) {
        preferences.update { it.copy(isDarkMode = isDark) }
    }

    override suspend fun updateGeneratedPalette(
        name: String,
        primaryHex: String,
        secondaryHex: String,
        tertiaryHex: String,
        backgroundHex: String,
        surfaceHex: String
    ) {
        preferences.update {
            it.copy(
                generatedPaletteName = name,
                generatedPrimaryHex = primaryHex,
                generatedSecondaryHex = secondaryHex,
                generatedTertiaryHex = tertiaryHex,
                generatedBackgroundHex = backgroundHex,
                generatedSurfaceHex = surfaceHex
            )
        }
    }

    override suspend fun updateSavedPalettes(palettes: List<GeneratedPalette>) {
        preferences.update { it.copy(savedPalettes = palettes) }
    }
}
