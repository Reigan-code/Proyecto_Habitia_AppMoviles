package upeu.edu.pe.Domain.repository

import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Core.theme.GeneratedPalette
import upeu.edu.pe.Domain.model.AppFontFamily
import upeu.edu.pe.Domain.model.AppPalette
import upeu.edu.pe.Domain.model.UserThemePreference

interface UserPreferencesRepository {
    fun getThemePreference(): Flow<UserThemePreference>

    suspend fun updatePalette(palette: AppPalette)
    suspend fun updateFont(font: AppFontFamily)
    suspend fun updateDarkMode(isDark: Boolean)

    suspend fun updateGeneratedPalette(
        name: String,
        primaryHex: String,
        secondaryHex: String,
        tertiaryHex: String,
        backgroundHex: String,
        surfaceHex: String
    )

    //
    suspend fun updateSavedPalettes(palettes: List<GeneratedPalette>)
}