package upeu.edu.pe.Presentation.more

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import upeu.edu.pe.Domain.actions.HaabiActions
import upeu.edu.pe.Domain.model.CalendarEvent
import upeu.edu.pe.Domain.model.FoodCategory
import upeu.edu.pe.Domain.model.Habit
import upeu.edu.pe.Domain.model.Meal
import upeu.edu.pe.Domain.model.MealType
import upeu.edu.pe.Domain.model.Reminder
import upeu.edu.pe.Domain.model.Task
import upeu.edu.pe.Domain.model.TaskPriority
import upeu.edu.pe.Domain.repository.CalendarEventRepository
import upeu.edu.pe.Domain.repository.DiaryRepository
import upeu.edu.pe.Domain.repository.HabitRepository
import upeu.edu.pe.Domain.repository.MealRepository
import upeu.edu.pe.Domain.repository.ReminderRepository
import upeu.edu.pe.Domain.repository.ShoppingRepository
import upeu.edu.pe.Domain.repository.TaskRepository

// ═══════════════════════════════════════════════════════════════════════════
// HaabiActionsImpl — puente entre Haabi (function calling) y los repositorios
// reales. Cada acción crea el dato en el backend y devuelve un texto de
// confirmación con la RUTA exacta de la pantalla para navegar rápido.
// ═══════════════════════════════════════════════════════════════════════════
class HaabiActionsImpl(
    private val taskRepo: TaskRepository,
    private val habitRepo: HabitRepository,
    private val reminderRepo: ReminderRepository,
    private val mealRepo: MealRepository,
    private val shoppingRepo: ShoppingRepository,
    private val diaryRepo: DiaryRepository,
    private val calendarRepo: CalendarEventRepository
) : HaabiActions {

    private fun today(): String =
        Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

    // Normaliza fechas en lenguaje natural que Gemini suele devolver
    // ("mañana", "hoy") a ISO yyyy-MM-dd que exige el backend.
    private fun normalizeDate(raw: String?): String {
        val v = raw?.trim()?.lowercase() ?: return today()
        if (Regex("""\d{4}-\d{2}-\d{2}""").matches(v)) return v
        return when {
            v.contains("mañana") || v.contains("manana") ->
                Clock.System.todayIn(TimeZone.currentSystemDefault())
                    .plus(1, kotlinx.datetime.DateTimeUnit.DAY).toString()
            v.contains("hoy") -> today()
            else -> today()
        }
    }

    // Al crear una tarea, también la AGENDA en el calendario y crea una
    // ALERTA para el día (09:00) para recordarla durante el día.
    private suspend fun scheduleForTask(title: String, description: String, date: String) {
        try {
            calendarRepo.addEvent(
                CalendarEvent(
                    id = "",
                    title = title,
                    description = description,
                    date = date,
                    startTime = "09:00",
                    endTime = "10:00"
                )
            )
        } catch (e: Exception) {
            println("[Habitia-Haabi] agenda evento FALLÓ: ${e.message}")
        }
        try {
            reminderRepo.addReminder(
                Reminder(
                    id = "",
                    title = title,
                    description = description,
                    time = "09:00",
                    date = date,
                    completed = false
                )
            )
        } catch (e: Exception) {
            println("[Habitia-Haabi] alerta recordatorio FALLÓ: ${e.message}")
        }
    }

    override suspend fun createTask(
        title: String,
        description: String?,
        category: String?,
        priority: String?,
        dueDate: String?
    ): String {
        val p = when (priority?.uppercase()) {
            "HIGH" -> TaskPriority.HIGH
            "MEDIUM" -> TaskPriority.MEDIUM
            "LOW" -> TaskPriority.LOW
            else -> TaskPriority.MEDIUM
        }
        val inferredCategory = when {
            category?.isNotBlank() == true -> category
            title.contains("compra", ignoreCase = true) || (description?.contains("compra", ignoreCase = true) == true) -> "Compras"
            title.contains("comida", ignoreCase = true) || title.contains("aliment", ignoreCase = true) -> "Alimentación"
            else -> "Hogar"
        }
        val due = normalizeDate(dueDate)
        taskRepo.addTask(
            Task(
                id = "",
                title = title,
                description = description.orEmpty(),
                category = inferredCategory,
                priority = p,
                dueDate = due,
                dueTime = null
            )
        )
        scheduleForTask(title, description.orEmpty(), due)
        upeu.edu.pe.Core.events.HaabiEventBus.emit(
            upeu.edu.pe.Core.events.HaabiCreatedEvent("tasks", inferredCategory, "Tarea: $title")
        )
        return "✅ Creé la tarea \"$title\" en $inferredCategory para el $due, la agendé y puse alerta a las 09:00. Ruta: /tasks"
    }

    override suspend fun createHabit(name: String, frequency: String?): String {
        habitRepo.addHabit(
            Habit(
                id = "",
                name = name,
                frequency = frequency?.takeIf { it.isNotBlank() } ?: "DAILY"
            )
        )
        return "✅ Creé el hábito \"$name\". Ruta: /home"
    }

    override suspend fun createReminder(
        title: String,
        date: String?,
        time: String?
    ): String {
        reminderRepo.addReminder(
            Reminder(
                id = "",
                title = title,
                description = "",
                time = time?.takeIf { it.isNotBlank() } ?: "09:00",
                date = date?.takeIf { it.isNotBlank() } ?: today(),
                recurring = false
            )
        )
        return "✅ Creé el recordatorio \"$title\". Ruta: /reminders"
    }

    override suspend fun createMeal(
        name: String,
        type: String?,
        date: String?,
        ingredients: List<String>?
    ): String {
        val t = when (type?.uppercase()) {
            "BREAKFAST" -> MealType.BREAKFAST
            "LUNCH" -> MealType.LUNCH
            "DINNER" -> MealType.DINNER
            "SNACK" -> MealType.SNACK
            else -> MealType.LUNCH
        }
        mealRepo.addMeal(
            Meal(
                id = "",
                name = name,
                type = t,
                date = date?.takeIf { it.isNotBlank() } ?: today(),
                ingredients = ingredients ?: emptyList()
            )
        )
        return "Listo. Cree la comida \"$name\". Ruta: /food"
    }

    override suspend fun createShoppingItem(
        name: String,
        quantity: String?,
        category: String?,
        date: String?
    ): String {
        shoppingRepo.addItem(
            name = name,
            quantity = quantity?.takeIf { it.isNotBlank() } ?: "1",
            category = category?.takeIf { it.isNotBlank() } ?: "Otros",
            date = date?.takeIf { it.isNotBlank() }
        )
        return "Listo. Agregue \"$name\" a la lista de compras. Ruta: /food"
    }

    override suspend fun createWeeklyMenuPlan(planJson: String): String {
        return try {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true; isLenient = true }
            val root = json.parseToJsonElement(planJson).jsonObject
            val dias = root["dias"]?.jsonArray ?: return "No pude leer los días del plan."
            val mealsCreated = mutableListOf<String>()
            val itemsCreated = mutableListOf<String>()
            var firstDate = ""
            val resumen = StringBuilder()

            for (diaEl in dias) {
                val dia = diaEl.jsonObject
                val fecha = dia["fecha"]?.jsonPrimitive?.content ?: today()
                val nombreDia = dia["nombre"]?.jsonPrimitive?.content ?: fecha
                if (firstDate.isBlank()) firstDate = fecha
                resumen.append("\n").append(nombreDia).append(" (").append(fecha).append("):")
                val comidas = dia["comidas"]?.jsonArray ?: continue

                for (comidaEl in comidas) {
                    val c = comidaEl.jsonObject
                    val name = c["nombre"]?.jsonPrimitive?.content?.trim() ?: continue
                    val type = when (c["tipo"]?.jsonPrimitive?.content?.uppercase()) {
                        "BREAKFAST" -> MealType.BREAKFAST
                        "LUNCH" -> MealType.LUNCH
                        "DINNER" -> MealType.DINNER
                        "SNACK" -> MealType.SNACK
                        else -> MealType.LUNCH
                    }
                    val yq = c["youtubeQuery"]?.jsonPrimitive?.content?.trim()?.takeIf { it.isNotBlank() }
                    val ytUrl = yq?.let { "https://www.youtube.com/results?search_query=" + it.replace(" ", "+") }
                    val ingsEl = c["ingredientes"]?.jsonArray ?: kotlinx.serialization.json.JsonArray(emptyList())
                    val ings = mutableListOf<String>()

                    for (ingEl in ingsEl) {
                        val ing = ingEl.jsonObject
                        val ingName = ing["nombre"]?.jsonPrimitive?.content?.trim() ?: continue
                        val cant = ing["cantidad"]?.jsonPrimitive?.content?.trim().takeIf { !it.isNullOrBlank() } ?: "1"
                        val catLabel = ing["categoria"]?.jsonPrimitive?.content
                        val cat = FoodCategory.fromLabel(catLabel)
                        val detalle = "$ingName $cant".trim()
                        ings.add(detalle)
                        // Guardar en la lista de compras con día + comida + categoría
                        shoppingRepo.addItem(
                            name = "$detalle (${cat.label})",
                            quantity = cant,
                            category = cat.label,
                            date = fecha,
                            sourceMealType = type.name,
                            sourceMealName = name
                        )
                        itemsCreated.add(detalle)
                    }

                    mealRepo.addMeal(Meal(
                        id = "",
                        name = name,
                        type = type,
                        date = fecha,
                        ingredients = ings,
                        youtubeQuery = yq,
                        youtubeUrl = ytUrl
                    ))
                    mealsCreated.add(name)
                    resumen.append("\n  ").append(type.name).append(": ").append(name)
                        .append(" (").append(ings.joinToString(", ")).append(")")
                }
            }

            taskRepo.addTask(Task(
                id = "",
                title = "Compra semanal",
                description = resumen.toString().trim(),
                category = "Compras",
                priority = TaskPriority.HIGH,
                dueDate = firstDate.ifBlank { today() }
            ))
            scheduleForTask("Compra semanal", resumen.toString().trim(), firstDate.ifBlank { today() })
            upeu.edu.pe.Core.events.HaabiEventBus.emit(
                upeu.edu.pe.Core.events.HaabiCreatedEvent("food", "Compras", "Menu semanal + lista de compras")
            )
            "Listo: ${mealsCreated.size} comidas, ${itemsCreated.size} ingredientes en la lista de compras, tarea 'Compra semanal' agendada con alerta. Ruta: /food"
        } catch (e: Exception) {
            "No pude crear el plan: ${e.message}"
        }
    }

    override suspend fun createDiaryEntry(text: String): String {
        diaryRepo.addEntry(today(), text)
        return "✅ Escribí tu entrada en el diario. Ruta: /diary"
    }
}
