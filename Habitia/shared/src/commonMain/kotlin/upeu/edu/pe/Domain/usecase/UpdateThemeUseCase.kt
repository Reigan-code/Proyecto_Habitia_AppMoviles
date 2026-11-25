package upeu.edu.pe.Domain.usecase

import upeu.edu.pe.Core.theme.GeneratedPalette
import upeu.edu.pe.Domain.model.AppFontFamily
import upeu.edu.pe.Domain.model.AppPalette
import upeu.edu.pe.Domain.repository.UserPreferencesRepository

class UpdateThemeUseCase(
    private val repository: UserPreferencesRepository
) {
    /**
     * Aplica una paleta fija del enum.
     * IMPORTANTE: el repositorio debe LIMPIAR la generatedPaletteName,
     * si no, la generada "gana" en el observe del ViewModel y la fija nunca se ve.
     */
    suspend fun updatePalette(palette: AppPalette) {
        repository.updatePalette(palette)
    }

    suspend fun updateFont(font: AppFontFamily) {
        repository.updateFont(font)
    }

    suspend fun updateGeneratedPalette(
        name: String,
        primaryHex: String,
        secondaryHex: String,
        tertiaryHex: String,
        backgroundHex: String,
        surfaceHex: String
    ) {
        repository.updateGeneratedPalette(
            name, primaryHex, secondaryHex, tertiaryHex, backgroundHex, surfaceHex
        )
    }

    //  NUEVOS
    suspend fun updateSavedPalettes(palettes: List<GeneratedPalette>) {
        repository.updateSavedPalettes(palettes)
    }

    suspend fun updateDarkMode(isDark: Boolean) {
        repository.updateDarkMode(isDark)
    }
}