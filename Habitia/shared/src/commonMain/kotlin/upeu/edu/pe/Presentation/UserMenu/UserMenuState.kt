package upeu.edu.pe.Presentation.UserMenu

import upeu.edu.pe.Core.theme.GeneratedPalette
import upeu.edu.pe.Domain.model.AppFontFamily
import upeu.edu.pe.Domain.model.AppPalette

/**
 * Estado que representa los datos del perfil de usuario y la configuración visual.
 */
data class UserMenuState(
    val userName: String = "Usuario",
    val userEmail: String = "",
    // Fecha de nacimiento REAL de la sesión (GET /Users/email) — editable
    // en el perfil y persistida con PUT /Users/{id}.
    val birthDate: String? = null,
    val profileImageBytes: ByteArray? = null,
    val selectedPalette: AppPalette = AppPalette.SAKURA,
    val selectedFont: AppFontFamily = AppFontFamily.QUICKSAND,
    val isDarkMode: Boolean = false,
    val softBackground: Boolean = true,

    val activePaletteName: String = AppPalette.SAKURA.displayName,


    val activePalette: GeneratedPalette? = null,


    val savedPalettes: List<GeneratedPalette> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserMenuState) return false

        if (userName != other.userName) return false
        if (userEmail != other.userEmail) return false
        if (birthDate != other.birthDate) return false
        if (profileImageBytes != null) {
            if (other.profileImageBytes == null) return false
            if (!profileImageBytes.contentEquals(other.profileImageBytes)) return false
        } else if (other.profileImageBytes != null) return false
        if (selectedPalette != other.selectedPalette) return false
        if (selectedFont != other.selectedFont) return false
        if (isDarkMode != other.isDarkMode) return false
        if (softBackground != other.softBackground) return false
        if (activePaletteName != other.activePaletteName) return false
        if (activePalette != other.activePalette) return false
        if (savedPalettes != other.savedPalettes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + userEmail.hashCode()
        result = 31 * result + (birthDate?.hashCode() ?: 0)
        result = 31 * result + (profileImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + selectedPalette.hashCode()
        result = 31 * result + selectedFont.hashCode()
        result = 31 * result + isDarkMode.hashCode()
        result = 31 * result + softBackground.hashCode()
        result = 31 * result + activePaletteName.hashCode()
        result = 31 * result + (activePalette?.hashCode() ?: 0)
        result = 31 * result + savedPalettes.hashCode()
        return result
    }
}
