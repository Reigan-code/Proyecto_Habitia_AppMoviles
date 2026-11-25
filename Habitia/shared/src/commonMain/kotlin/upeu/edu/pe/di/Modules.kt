package upeu.edu.pe.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.repository.*
import upeu.edu.pe.Domain.repository.*
import upeu.edu.pe.Domain.usecase.*
import upeu.edu.pe.Presentation.auth.AuthViewModel
import upeu.edu.pe.Presentation.food.FoodViewModel
import upeu.edu.pe.Presentation.home.HomeViewModel
import upeu.edu.pe.Presentation.more.DiaryViewModel
import upeu.edu.pe.Presentation.planner.PlannerViewModel
import upeu.edu.pe.Presentation.reminders.RemindersViewModel
import upeu.edu.pe.Presentation.tasks.TasksViewModel

// Modules
// Acoplado a HABITIA: nuestros repositorios, use cases y viewmodels.
val appModule = module {
    //creacion de sesion en el movil (Settings multiplataforma + SessionManager)
    single { createSettings() }
    single { SessionManager() }

    //API Cliente (Ktor — es el que conecta con el backend Spring Boot)
    single { ApiClient.httpClient }

    //Repositories (los que hablan con /habitia-backend)
    single<TaskRepository> { ApiTaskRepositoryImpl(get(), get()) }
    single<HabitRepository> { ApiHabitRepositoryImpl(get(), get()) }
    single<ReminderRepository> { ApiReminderRepositoryImpl(get(), get()) }
    single<MealRepository> { ApiMealRepositoryImpl(get(), get()) }
    single<ShoppingRepository> { ApiShoppingRepositoryImpl(get(), get()) }
    single<CalendarEventRepository> { ApiCalendarEventRepositoryImpl(get(), get()) }
    single<DiaryRepository> { ApiDiaryRepositoryImpl(get(), get()) }
    single<UserRepository> { ApiUserRepositoryImpl(get(), get()) }
    single<TaskCategoryRepository> { ApiTaskCategoryRepository(get(), get()) }
    single<IngredientCatalogRepository> { ApiIngredientCatalogRepository(get(), get()) }
    single { ApiAuthRepositoryImpl(get(), get()) }

    //useCase (factory = una instancia por pantalla)
    factory { GetTodayTasksUseCase(get()) }
    factory { CompleteTaskUseCase(get()) }
    factory { GetHabitsUseCase(get()) }
    factory { GetUpcomingReminderUseCase(get()) }
    factory { GetWeeklyMealsUseCase(get()) }
    factory { GetThemePreferenceUseCase(get()) }
    factory { UpdateThemeUseCase(get()) }

    //viewModels
    factory { HomeViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { TasksViewModel(get(), get()) }
    factory { FoodViewModel(get(), get(), get()) }
    factory { RemindersViewModel(get()) }
    factory { PlannerViewModel(get(), get(), get()) }
    factory { DiaryViewModel(get()) }
    factory { AuthViewModel(get()) }
}
