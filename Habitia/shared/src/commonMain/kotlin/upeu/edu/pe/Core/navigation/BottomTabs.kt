package upeu.edu.pe.Core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Define las secciones de la barra de navegación inferior.
 * Cada tab tiene un título, un icono y una ruta (para futura navegación).
 */
// por seguirdad ya que subclases solo estaran dentro del mismo archivo para poder heredar de ella
sealed class BottomTab(
    val screen: HabitiaScreen,
    val title: String,
    val icon: ImageVector,
    val route: String = screen::class.simpleName ?: ""
) {
    object Home : BottomTab(HabitiaScreen.Home, "Inicio", Icons.Default.Home)
    object Tasks : BottomTab(HabitiaScreen.Tasks, "Tareas", Icons.Default.CheckCircle)
    object Planner : BottomTab(HabitiaScreen.Planner, "Agenda", Icons.Default.DateRange)
    object Reminders : BottomTab(HabitiaScreen.Reminders, "Alertas", Icons.Default.Notifications)
    object More : BottomTab(HabitiaScreen.More, "Más", Icons.Default.MoreHoriz)

    // Secciones secundarias integradas dentro del sellado
    object Food : BottomTab(HabitiaScreen.Food, "Alimentación", Icons.Default.Restaurant)
    object Focus : BottomTab(HabitiaScreen.Focus, "Enfoque", Icons.Default.PlayArrow)

    companion object {
        // Pestañas visibles en la barra inferior (Material 3 - UX 5 items max)
        val allTabs = listOf(Home, Tasks, Planner, Reminders, More)
    }
}