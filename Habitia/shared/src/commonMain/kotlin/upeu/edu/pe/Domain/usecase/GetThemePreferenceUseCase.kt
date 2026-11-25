package upeu.edu.pe.Domain.usecase
import kotlinx.coroutines.flow.Flow
import upeu.edu.pe.Domain.model.UserThemePreference
import upeu.edu.pe.Domain.repository.UserPreferencesRepository


/**
 * Caso de Uso para observar de forma reactiva la preferencia de tema actual del usuario.
 */
class GetThemePreferenceUseCase(
    private val repository: UserPreferencesRepository
) {
    /**
     * Retorna un Flow con los cambios en tiempo real del tema.
     */
    operator fun invoke(): Flow<UserThemePreference> {
        return repository.getThemePreference()
    }
}