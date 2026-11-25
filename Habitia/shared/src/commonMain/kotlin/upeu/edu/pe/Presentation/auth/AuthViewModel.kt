package upeu.edu.pe.Presentation.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import upeu.edu.pe.Domain.repository.AuthRepository

// ═══════════════════════════════════════════════════════════════════════════
// AuthViewModel — MVVM para Login/Register REAL contra el backend.
//
// ¿Por qué existe?
//  - Antes LoginScreen tenía el botón pegado directo a onLoginSuccess (nada
//    verificaba nada). Ahora la pantalla sigue siendo tonta: solo muestra
//    estado; este ViewModel decide QUÉ hacer (llamar al AuthRepository real).
//
// ¿Qué expone?
//  - uiState: isLoading (para deshabilitar botón/indicador), errorMessage
//    (texto legible de por qué falló) e isSuccess (disparo de navegación).
//
// Estilo de code: MISMA conducta del proyecto (CoroutineScope(Dispatchers.Main)
// como TaskViewModel) — sin androidx ViewModel para mantener coherencia.
// ═══════════════════════════════════════════════════════════════════════════
class AuthViewModel(
    private val authRepository: AuthRepository
) {
    // viewModelScope
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    // AuthUiState
    data class AuthUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(AuthUiState())


    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    fun login(email: String, password: String) {
        // Validación local: correo con forma de correo + contraseña presente.
        if (!email.contains("@") || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Escribe un correo y una contraseña válidos.")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            authRepository.login(email, password)
                .onSuccess {
                    // Token+userId en SessionManager → App.kt cargará

                    _uiState.value = AuthUiState(isSuccess = true)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState(errorMessage = e.message)
                }
        }
    }


    fun register(name: String, email: String, password: String, birthDate: String) {
        if (name.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "El nombre es obligatorio.")
            return
        }
        if (!email.contains("@")) {
            _uiState.value = AuthUiState(errorMessage = "Escribe un correo válido.")
            return
        }
        if (password.length < 8) {
            _uiState.value = AuthUiState(errorMessage = "La contraseña debe tener al menos 8 caracteres.")
            return
        }
        if (birthDate.length != 10 || birthDate[4] != '-' || birthDate[7] != '-') {
            _uiState.value = AuthUiState(errorMessage = "La fecha de nacimiento debe tener formato AAAA-MM-DD.")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            authRepository.register(name, email, password, birthDate)
                .onSuccess {
                    _uiState.value = AuthUiState(isSuccess = true)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState(errorMessage = e.message)
                }
        }
    }


    fun clearTransient() {
        _uiState.value = AuthUiState()
    }
}
