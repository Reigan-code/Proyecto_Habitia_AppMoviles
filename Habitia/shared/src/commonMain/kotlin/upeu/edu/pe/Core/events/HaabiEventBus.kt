package upeu.edu.pe.Core.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// Evento global que emite Haabi cada vez que crea algo (tarea, compra, hábito...).
// La App lo observa para: (1) navegar a la pantalla correcta y (2) refrescar
// los repositorios, de modo que TODAS las pantallas vean el dato nuevo al
// instante, sin esperar a un reinicio.
object HaabiEventBus {
    private val _events = MutableSharedFlow<HaabiCreatedEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<HaabiCreatedEvent> = _events.asSharedFlow()

    fun emit(event: HaabiCreatedEvent) {
        _events.tryEmit(event)
    }
}

data class HaabiCreatedEvent(
    val route: String,       // "tasks" | "food" | "home" | "diary" | ...
    val category: String? = null,  // categoría de tarea, si aplica
    val description: String
)
