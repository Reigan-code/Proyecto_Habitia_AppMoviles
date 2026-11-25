package upeu.edu.pe.Core.navigation

sealed class HabitiaScreen{
    object Splash : HabitiaScreen()
    object Welcome : HabitiaScreen()
    object Login : HabitiaScreen()
    object Register : HabitiaScreen()
    object Home : HabitiaScreen()

    object Profile : HabitiaScreen()
    object Appearance : HabitiaScreen() // personalización (paletas/fuentes/oscuro)
    object Planner : HabitiaScreen()
    object Tasks : HabitiaScreen()
    object Food : HabitiaScreen()
    object Reminders : HabitiaScreen()
    object Focus : HabitiaScreen()
    object More : HabitiaScreen()
    object Diary : HabitiaScreen() // Diario personal (pantalla secundaria)
    object Haabi : HabitiaScreen() // Haabi (mascota + chat dedicado)
    object Analytics : HabitiaScreen() // Analíticas, podio y recompensas

}