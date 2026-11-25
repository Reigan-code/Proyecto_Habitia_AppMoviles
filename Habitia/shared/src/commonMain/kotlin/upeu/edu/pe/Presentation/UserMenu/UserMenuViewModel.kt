package upeu.edu.pe.Presentation.UserMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.todayIn
import upeu.edu.pe.Core.theme.GeneratedPalette
import upeu.edu.pe.Core.theme.hexToComposeColor
import upeu.edu.pe.Core.theme.toColorScheme
import upeu.edu.pe.Core.theme.toHexFormat
import upeu.edu.pe.Domain.model.AppFontFamily
import upeu.edu.pe.Domain.model.AppPalette
import upeu.edu.pe.Domain.usecase.GetThemePreferenceUseCase
import upeu.edu.pe.Domain.usecase.UpdateThemeUseCase

class UserMenuViewModel(
    private val getThemePreferenceUseCase: GetThemePreferenceUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,

    realUserName: String? = null,

    realUserEmail: String? = null,

    // Repositorio del perfil REAL (PUT /Users/{id}) y la sesión — null por
    // defecto para no romper construcciones previas; App.kt los pasa.
    private val userApi: upeu.edu.pe.Domain.repository.UserRepository? = null,
    private val session: upeu.edu.pe.Data.remote.auth.SessionManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UserMenuState(
            userName = realUserName ?: "Usuario",
            userEmail = realUserEmail ?: "",
            // birthDate REAL de la sesión (campo nuevo del estado).
            birthDate = session?.state?.value?.birthDate
        )
    )
    val uiState: StateFlow<UserMenuState> = _uiState.asStateFlow()

    init {
        observeThemePreferences()
    }

    // ═══ PERFIL REAL: PUT /Users/{id} + réplica en SessionManager ═══
    // Estados separados del uiState (UserMenuState NO es data class: tocarlo
    // exigiría actualizar equals/hashCode a mano — aquí no hace falta).

    private val _profileSaving = MutableStateFlow(false)
    val profileSaving: StateFlow<Boolean> = _profileSaving.asStateFlow()

    private val _profileMessage = MutableStateFlow<String?>(null)
    val profileMessage: StateFlow<String?> = _profileMessage.asStateFlow()

    // updateUserProfile(): valida en cliente (nombre no vacío, fecha ISO
    // AAAA-MM-DD válida y en el pasado) y envía el PUT. En éxito replica en
    // la sesión → el saludo de Home se actualiza al momento.
    fun updateUserProfile(name: String, birthDate: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            _profileMessage.value = "El nombre no puede estar vacío."
            return
        }
        val fechaValida = runCatching {
            kotlinx.datetime.LocalDate.parse(birthDate)
        }.isSuccess
        if (!fechaValida) {
            _profileMessage.value = "La fecha debe ser AAAA-MM-DD."
            return
        }
        val hoy = kotlinx.datetime.Clock.System.todayIn(
            kotlinx.datetime.TimeZone.currentSystemDefault()
        )
        if (kotlinx.datetime.LocalDate.parse(birthDate) > hoy) {
            _profileMessage.value = "La fecha de nacimiento no puede ser futura."
            return
        }
        val userId = session?.state?.value?.userId
        if (userApi == null || userId == null) {
            _profileMessage.value = "Sin sesión activa."
            return
        }
        viewModelScope.launch {
            _profileSaving.value = true
            _profileMessage.value = null
            val ok = userApi.updateProfile(userId, trimmed, birthDate)
            _profileSaving.value = false
            _profileMessage.value = if (ok) {
                session?.updateProfile(trimmed, birthDate)
                "Perfil actualizado en el servidor ✓"
            } else {
                "No se pudo actualizar (revisa tu conexión)."
            }
        }
    }

    fun clearProfileMessage() {
        _profileMessage.value = null
    }

    private fun observeThemePreferences() {
        viewModelScope.launch {
            getThemePreferenceUseCase().collect { preferences ->
                _uiState.update { current ->
                    current.copy(
                        selectedPalette = preferences.palette,
                        selectedFont = preferences.font,
                        isDarkMode = preferences.isDarkMode,
                        // Una paleta de 5 colores (generatedPalette) siempre manda sobre
                        // el enum fijo: es lo que HabitiaTheme realmente está pintando.
                        activePaletteName = preferences.generatedPaletteName
                            ?: preferences.palette.displayName,
                        // Colores reales de la paleta generada activa (null = paleta del enum).
                        activePalette = preferences.generatedPaletteName?.let { name ->
                            GeneratedPalette(
                                name = name,
                                primary = hexToComposeColor(preferences.generatedPrimaryHex ?: "#E99CA8"),
                                secondary = hexToComposeColor(preferences.generatedSecondaryHex ?: "#F5C6CB"),
                                tertiary = hexToComposeColor(preferences.generatedTertiaryHex ?: "#FFFFFF"),
                                background = hexToComposeColor(preferences.generatedBackgroundHex ?: "#F4F1F8"),
                                surface = hexToComposeColor(preferences.generatedSurfaceHex ?: "#FFFFFF")
                            )
                        },
                        savedPalettes = preferences.savedPalettes
                    )
                }
            }
        }
    }

    /** Aplica una de las 8 paletas fijas del enum AppPalette. */
    fun onPaletteSelected(palette: AppPalette) {
        _uiState.update {
            it.copy(activePaletteName = palette.displayName, activePalette = null)
        }
        viewModelScope.launch {
            updateThemeUseCase.updatePalette(palette)
        }
    }

    /**
     * Aplica una paleta con colore
     */
    fun onApplyGeneratedPalette(palette: GeneratedPalette) {
        _uiState.update {
            it.copy(activePaletteName = palette.name, activePalette = palette)
        }
        viewModelScope.launch {
            updateThemeUseCase.updateGeneratedPalette(
                name = palette.name,
                primaryHex = palette.primary.toHexFormat(),
                secondaryHex = palette.secondary.toHexFormat(),
                tertiaryHex = palette.tertiary.toHexFormat(),
                backgroundHex = palette.background.toHexFormat(),
                surfaceHex = palette.surface.toHexFormat()
            )
        }
    }

    /**
     * Cambia SOLO el fondo de la aplicación conservando los colores de la
     * paleta activa. Si la activa es una paleta del enum, se convierte en una
     * paleta generada con esos mismos tonos pero con el nuevo fondo.
     */
    fun onBackgroundSelected(backgroundHex: String) {
        val base = _uiState.value.activePalette
            ?: _uiState.value.selectedPalette.toColorScheme().let { scheme ->
                GeneratedPalette(
                    name = _uiState.value.selectedPalette.displayName,
                    primary = scheme.primary,
                    secondary = scheme.secondary,
                    tertiary = scheme.tertiary
                )
            }
        // Evita acumular el sufijo si se eligen varios fondos seguidos
        val baseName = base.name.removeSuffix(" · Fondo")
        onApplyGeneratedPalette(
            base.copy(
                name = "$baseName · Fondo",
                background = hexToComposeColor(backgroundHex)
            )
        )
    }

    /** Guarda una paleta para que quede disponible en "Mis Paletas". */
    fun onSaveGeneratedPalette(palette: GeneratedPalette) {
        val currentList = _uiState.value.savedPalettes
        val updated =
            if (currentList.any { it.name == palette.name }) currentList
            else currentList + palette
        _uiState.update { s -> s.copy(savedPalettes = updated) }
        // Sincroniza con el repositorio (DataStore persistirá "Mis Paletas" en Android)
        viewModelScope.launch { updateThemeUseCase.updateSavedPalettes(updated) }
    }

    /** Elimina una paleta previamente guardada por el usuario. */
    fun onDeleteSavedPalette(palette: GeneratedPalette) {
        val updated = _uiState.value.savedPalettes.filterNot { it.name == palette.name }
        _uiState.update { s -> s.copy(savedPalettes = updated) }
        // Sincroniza con el repositorio
        viewModelScope.launch { updateThemeUseCase.updateSavedPalettes(updated) }
    }

    fun onFontSelected(font: AppFontFamily) {
        viewModelScope.launch {
            updateThemeUseCase.updateFont(font)
        }
    }

    fun onDarkModeToggled(isDark: Boolean) {
        _uiState.update { it.copy(isDarkMode = isDark) }
        // Persiste el modo oscuro en el repositorio para que el tema lo aplique
        viewModelScope.launch { updateThemeUseCase.updateDarkMode(isDark) }
    }

    fun onToggleSoftBackground(v: Boolean) = _uiState.update { it.copy(softBackground = v) }

    fun onProfileImagePicked(bytes: ByteArray) {
        _uiState.update { it.copy(profileImageBytes = bytes) }
    }
}
