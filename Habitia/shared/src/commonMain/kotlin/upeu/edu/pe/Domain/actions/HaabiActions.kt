package upeu.edu.pe.Domain.actions

// ═══════════════════════════════════════════════════════════════════════════
// HaabiActions — acciones que Haabi puede ejecutar al chatear.
//
// La IA (Gemini, function calling) decide llamar una de estas acciones cuando
// el usuario pide crear algo ("crea una tarea de...", "apúntame un hábito...").
// La implementación vive en la capa de Presentación (App.kt) y usa los
// repositories. Cada método devuelve el TEXTO de confirmación, que
// incluye la RUTA exacta de la pantalla donde se creó, para que el asi te lleve pues
// ═══════════════════════════════════════════════════════════════════════════
interface HaabiActions {

    suspend fun createTask(
        title: String,
        description: String? = null,
        category: String? = null,
        priority: String? = null,
        dueDate: String? = null
    ): String

    suspend fun createHabit(
        name: String,
        frequency: String? = null
    ): String

    suspend fun createWeeklyMenuPlan(
        planJson: String
    ): String

    suspend fun createReminder(
        title: String,
        date: String? = null,
        time: String? = null
    ): String

    suspend fun createMeal(
        name: String,
        type: String? = null,
        date: String? = null,
        ingredients: List<String>? = null
    ): String

    suspend fun createShoppingItem(
        name: String,
        quantity: String? = null,
        category: String? = null,
        date: String? = null
    ): String

    suspend fun createDiaryEntry(text: String): String
}
