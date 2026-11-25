package upeu.edu.pe.Data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.dto.MealCreateDto
import upeu.edu.pe.Data.remote.dto.MealIngredientCreateDto
import upeu.edu.pe.Data.remote.dto.MealResponseDto
import upeu.edu.pe.Data.remote.mapper.backendMealId
import upeu.edu.pe.Data.remote.mapper.toDomain
import upeu.edu.pe.Domain.model.Meal
import upeu.edu.pe.Domain.repository.MealRepository

// ═══════════════════════════════════════════════════════════════════════════
// ApiMealRepositoryImpl — comidas REALES (/Meals del backend HABITIA-Back).
//
// Campos SOLO LOCALES de Meal: ingredients (el backend los modela en
// /MealIngredients con mealId; sincronizarlos agrega N llamadas por comida
// y queda documentado como mejora futura) e image.
// completed del backend no se usa en la UI de Meals (siempre false).
// ═══════════════════════════════════════════════════════════════════════════
class ApiMealRepositoryImpl(
    private val httpClient: HttpClient,
    private val session: SessionManager
) : MealRepository {

    private val meals = MutableStateFlow<List<Meal>>(emptyList())

    // Ingredients SOLO LOCALES por id de comida ("meal-<n>") — sobreviven a
    // la sesión pero no al cierre de la app (igual que category en Tasks).
    private val ingredientsByMeal = mutableMapOf<String, List<String>>()

    override fun getMeals(): Flow<List<Meal>> = meals.asStateFlow()

    // getWeeklyMeals(): el use case de la semana filtra con estas fechas;
    // aquí se entrega TODO el cache (conducta igual a los mocks).
    override suspend fun getWeeklyMeals(): List<Meal> = meals.value

    suspend fun refresh() {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            val response = httpClient.get("${ApiClient.getBaseUrl()}/Meals/user/$userId") {
                bearerAuth(token)
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] meals refresh: HTTP ${response.status.value}")
                return
            }
            val dtos = response.body<List<MealResponseDto>>()
            // Para cada comida, bajar sus ingredientes del backend
            val mapped = dtos.map { dto ->
                val ingResp = try {
                    httpClient.get("${ApiClient.getBaseUrl()}/MealIngredients/meal/${dto.id}") { bearerAuth(token) }
                } catch (e: Exception) { null }
                val ings = if (ingResp != null && ingResp.status.isSuccess()) {
                    ingResp.body<List<upeu.edu.pe.Data.remote.dto.MealIngredientResponseDto>>().map { it.description }
                } else {
                    ingredientsByMeal["meal-${dto.id}"] ?: emptyList()
                }
                ingredientsByMeal["meal-${dto.id}"] = ings
                dto.toDomain(ings)
            }
            meals.update { mapped }
        } catch (e: Exception) {
            println("[Habitia-API] meals refresh FALLÓ: ${e.message}")
        }
    }

    override suspend fun addMeal(meal: Meal) {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            val response = httpClient.post("${ApiClient.getBaseUrl()}/Meals") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    MealCreateDto(
                        userId = userId, // ESTE módulo lo exige en el body
                        name = meal.name,
                        type = meal.type.name,
                        date = meal.date.take(10),
                        time = null, // Meal del dominio no modela hora
                        description = meal.description,
                        completed = false
                    )
                )
            }
            if (!response.status.isSuccess()) {
                println("[Habitia-API] addMeal: HTTP ${response.status.value}")
                return
            }
            val dto = response.body<MealResponseDto>()
            val domainId = "meal-${dto.id}"
            ingredientsByMeal[domainId] = meal.ingredients
            // Persistir ingredientes en el backend (/MealIngredients)
            for (ing in meal.ingredients) {
                try {
                    httpClient.post("${ApiClient.getBaseUrl()}/MealIngredients") {
                        contentType(ContentType.Application.Json)
                        bearerAuth(token)
                        setBody(MealIngredientCreateDto(description = ing, mealId = dto.id))
                    }
                } catch (e: Exception) {
                    println("[Habitia-API] addMeal ingredient FALLÓ: ${e.message}")
                }
            }
            meals.update { it + dto.toDomain(meal.ingredients) }
        } catch (e: Exception) {
            println("[Habitia-API] addMeal FALLÓ: ${e.message}")
        }
    }

    override suspend fun updateMeal(meal: Meal) {
        val s = session.state.value
        val token = s.token ?: return
        val userId = s.userId ?: return
        try {
            ingredientsByMeal[meal.id] = meal.ingredients
            val response = httpClient.put("${ApiClient.getBaseUrl()}/Meals/${backendMealId(meal.id)}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    upeu.edu.pe.Data.remote.dto.MealUpdateDto(
                        userId = userId,
                        name = meal.name,
                        type = meal.type.name,
                        date = meal.date.take(10),
                        time = null,
                        description = meal.description,
                        completed = false
                    )
                )
            }
            if (response.status.isSuccess()) {
                meals.update { lista -> lista.map { if (it.id == meal.id) meal else it } }
            }
        } catch (e: Exception) {
            println("[Habitia-API] updateMeal FALLÓ: ${e.message}")
        }
    }

    override suspend fun deleteMeal(mealId: String) {
        val s = session.state.value
        val token = s.token ?: return
        try {
            val response =
                httpClient.delete("${ApiClient.getBaseUrl()}/Meals/${backendMealId(mealId)}") {
                    bearerAuth(token)
                }
            if (response.status.isSuccess()) {
                meals.update { lista -> lista.filterNot { it.id == mealId } }
                ingredientsByMeal.remove(mealId)
            }
        } catch (e: Exception) {
            println("[Habitia-API] deleteMeal FALLÓ: ${e.message}")
        }
    }
}
