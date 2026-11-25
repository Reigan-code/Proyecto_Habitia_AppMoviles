package upeu.edu.pe

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import upeu.edu.pe.Core.components.HaabiCompanion
import upeu.edu.pe.Core.events.HaabiEventBus
import upeu.edu.pe.Core.config.IslandConfig
import upeu.edu.pe.Core.navigation.BottomTab
import upeu.edu.pe.Core.navigation.HabitiaBottonNavigation
import upeu.edu.pe.Core.navigation.HabitiaScreen
import upeu.edu.pe.Core.theme.HabitiaTheme
import upeu.edu.pe.Data.remote.api.ApiClient
import upeu.edu.pe.Data.remote.auth.SessionManager
import upeu.edu.pe.Data.remote.auth.rememberSessionManager
import upeu.edu.pe.Data.remote.google.GoogleCalendarClient
import upeu.edu.pe.Data.repository.ApiAuthRepositoryImpl
import upeu.edu.pe.Data.repository.ApiCalendarEventRepositoryImpl
import upeu.edu.pe.Data.repository.ApiHabitRepositoryImpl
import upeu.edu.pe.Data.repository.ApiMealRepositoryImpl
import upeu.edu.pe.Data.repository.ApiReminderRepositoryImpl
import upeu.edu.pe.Data.repository.ApiShoppingRepositoryImpl
import upeu.edu.pe.Data.repository.ApiIngredientCatalogRepository
import upeu.edu.pe.Data.repository.ApiTaskCategoryRepository
import upeu.edu.pe.Data.repository.ApiDiaryRepositoryImpl
import upeu.edu.pe.Data.repository.ApiTaskRepositoryImpl
import upeu.edu.pe.Data.repository.ApiUserRepositoryImpl
import upeu.edu.pe.Data.repository.MockUserPreferencesRepositoryImpl
import upeu.edu.pe.Domain.usecase.CompleteTaskUseCase
import upeu.edu.pe.Domain.usecase.GetHabitsUseCase
import upeu.edu.pe.Domain.usecase.GetThemePreferenceUseCase
import upeu.edu.pe.Domain.usecase.GetTodayTasksUseCase
import upeu.edu.pe.Domain.usecase.GetUpcomingReminderUseCase
import upeu.edu.pe.Domain.usecase.UpdateThemeUseCase
import upeu.edu.pe.Presentation.UserMenu.AppearanceScreen
import upeu.edu.pe.Presentation.UserMenu.UserMenuScreen
import upeu.edu.pe.Presentation.UserMenu.UserMenuViewModel
import upeu.edu.pe.Presentation.auth.AuthViewModel
import upeu.edu.pe.Presentation.auth.LoginScreen
import upeu.edu.pe.Presentation.auth.RegisterScreen
import upeu.edu.pe.Presentation.focus.FocusScreen
import upeu.edu.pe.Presentation.focus.FocusViewModel
import upeu.edu.pe.Presentation.food.FoodScreen
import upeu.edu.pe.Presentation.food.FoodViewModel
import upeu.edu.pe.Presentation.home.HomeScreen
import upeu.edu.pe.Presentation.home.HomeViewModel
import upeu.edu.pe.Presentation.more.AnalyticsScreen
import upeu.edu.pe.Presentation.more.DiaryScreen
import upeu.edu.pe.Presentation.more.HaabiScreen
import upeu.edu.pe.Presentation.more.MoreScreen
import upeu.edu.pe.Presentation.planner.PlannerScreen
import upeu.edu.pe.Presentation.planner.PlannerViewModel
import upeu.edu.pe.Presentation.reminders.RemindersScreen
import upeu.edu.pe.Presentation.reminders.RemindersViewModel
import upeu.edu.pe.Presentation.splash.SplashContent
import upeu.edu.pe.Presentation.tasks.TasksScreen
import upeu.edu.pe.Presentation.tasks.TasksViewModel
import upeu.edu.pe.Presentation.welcome.WelcomeContent

@Composable
fun App() {
    val preferencesRepo = remember { MockUserPreferencesRepositoryImpl() }
    val getThemePreference = remember { GetThemePreferenceUseCase(preferencesRepo) }
    val updateThemeUseCase = remember { UpdateThemeUseCase(preferencesRepo) }

    // ═══ SESIÓN (ARRIBA del tema y del VM) ═══
    // SessionManager vive al inicio para que dos piezas lo consuman: (1) el
    // UserMenuViewModel necesita el nombre REAL al construirse (keyed remember),
    // (2) los repositorios de Data lo requieren. Las asignaciones de sesión
    // nuevas (login/registro/logout) redistribuyen ambos observadores por ellos mismos.
    val session = rememberSessionManager()
    val sessionState by session.state.collectAsState()

    val routeToScreen: (String) -> HabitiaScreen = { route ->
        when (route.trim().lowercase().trimStart('/')) {
            "home" -> HabitiaScreen.Home
            "tasks" -> HabitiaScreen.Tasks
            "habits" -> HabitiaScreen.Home
            "reminders" -> HabitiaScreen.Reminders
            "food" -> HabitiaScreen.Food
            "planner" -> HabitiaScreen.Planner
            "shopping" -> HabitiaScreen.Food
            "diary" -> HabitiaScreen.Diary
            "haabi" -> HabitiaScreen.Haabi
            "more" -> HabitiaScreen.More
"analytics" -> HabitiaScreen.Analytics
            "profile" -> HabitiaScreen.Profile
            else -> HabitiaScreen.Home
        }
    }

    LaunchedEffect(Unit) {
        ApiClient.resolveBaseUrl()
        session.restore()
    }

    // La llave sessionState.name + sessionState.email hace que, en el EXACTO
    // momento en que el login trae el perfil desde el backend
    // (GET /Users/email), el VM se recree con ESOS datos: nombre para el
    // saludo de Home y nombre+correo juntos para el perfil.
    // Perfil REAL (PUT /Users/{id}) + sesión para replicar los cambios en
    // name/birthDate (repositorio creado ANTES del VM para no anidar
    // remember dentro de remember).
    val userApi = remember { ApiUserRepositoryImpl(ApiClient.httpClient, session) }
    val userMenuViewModel = remember(sessionState.name, sessionState.email) {
        UserMenuViewModel(
            getThemePreference, updateThemeUseCase,
            realUserName = sessionState.name,
            realUserEmail = sessionState.email,
            userApi = userApi,
            session = session
        )
    }

    val userMenuState by userMenuViewModel.uiState.collectAsState()

    HabitiaTheme(
        userPreference = upeu.edu.pe.Domain.model.UserThemePreference(
            palette = userMenuState.selectedPalette,
            font = userMenuState.selectedFont,
            isDarkMode = userMenuState.isDarkMode
        ),
        activeGeneratedPalette = userMenuState.activePalette
    ) {
        var currentScreen by remember { mutableStateOf<HabitiaScreen>(HabitiaScreen.Splash) }

    // Ruta de RETORNO de la pantalla de Apariencia: se fija al navegar
    // (More o Perfil) para que el back regrese al lugar correcto.
    var appearanceReturnScreen by remember { mutableStateOf<HabitiaScreen>(HabitiaScreen.Profile) }

        // (la sesión ya quedó creada ARRIBA — SessionManager compartido).

        val taskRepo = remember { ApiTaskRepositoryImpl(ApiClient.httpClient, session) }

        // Autenticación oficial contra /auth/** (login, registro, logout).
        val authRepository = remember { ApiAuthRepositoryImpl(ApiClient.httpClient, session) }
        val authViewModel = remember { AuthViewModel(authRepository) }

        // ═══ REPOSITORIOS  ═══

        val reminderRepo = remember { ApiReminderRepositoryImpl(ApiClient.httpClient, session) }
        val mealRepo = remember { ApiMealRepositoryImpl(ApiClient.httpClient, session) }
        val habitRepo = remember { ApiHabitRepositoryImpl(ApiClient.httpClient, session) }
        // Planner (/CalendarEvents) y Compras (/ShoppingLists): módulos que
        // ANTES vivían como estado interno de sus ViewModels.
        val calendarEventRepo = remember { ApiCalendarEventRepositoryImpl(ApiClient.httpClient, session) }
        val shoppingRepo = remember { ApiShoppingRepositoryImpl(ApiClient.httpClient, session) }
        val diaryRepo = remember { ApiDiaryRepositoryImpl(ApiClient.httpClient, session) }
        val diaryViewModel = remember { upeu.edu.pe.Presentation.more.DiaryViewModel(diaryRepo) }
        val analyticsViewModel = remember { upeu.edu.pe.Presentation.more.AnalyticsViewModel(ApiClient.httpClient, session) }
        // Acciones que Haabi puede ejecutar (crear tareas/hábitos/etc.).
        val haabiActions = remember {
            upeu.edu.pe.Presentation.more.HaabiActionsImpl(
                taskRepo = taskRepo,
                habitRepo = habitRepo,
                reminderRepo = reminderRepo,
                mealRepo = mealRepo,
                shoppingRepo = shoppingRepo,
                diaryRepo = diaryRepo,
                calendarRepo = calendarEventRepo
            )
        }


        // (POST /auth/login + GET /Users/email), bajan TODOS los módulos del
        // usuario desde el backend (Tasks, Habits, Reminders, Meals,
        // Calendar, Shopping). Cada repositorio protege su llamada con
        // try/catch, así que un módulo caído NO rompe a los demás.
        val sessionState by session.state.collectAsState()
        LaunchedEffect(sessionState.token, sessionState.userId) {
            if (sessionState.token != null && sessionState.userId != null) {
                taskRepo.refresh()
                habitRepo.refresh()
                reminderRepo.refresh()
                mealRepo.refresh()
                calendarEventRepo.refresh()
                shoppingRepo.refresh()
                diaryRepo.refresh()
            }
        }

        val getTodayTasks = remember { GetTodayTasksUseCase(taskRepo) }
        val completeTask = remember { CompleteTaskUseCase(taskRepo) }
        val getUpcoming = remember { GetUpcomingReminderUseCase(reminderRepo) }
        val getHabits = remember { GetHabitsUseCase(habitRepo) }

        val homeViewModel = remember {
            HomeViewModel(
                getTodayTasks = getTodayTasks,
                completeTaskUseCase = completeTask,
                getUpcomingReminder = getUpcoming,
                getHabits = getHabits,
                taskRepo = taskRepo,
                habitRepo = habitRepo,
                reminderRepo = reminderRepo
            )
        }
        // PlannerViewModel: eventos (/CalendarEvents) + TAREAS como posits
        // + cliente de GOOGLE CALENDAR (API key) — calendario completo.
        val googleCalendarClient = remember { GoogleCalendarClient(ApiClient.httpClient) }
        val plannerViewModel = remember {
            PlannerViewModel(calendarEventRepo, taskRepo, googleCalendarClient)
        }
        val taskCategoryRepo = remember { ApiTaskCategoryRepository(ApiClient.httpClient, session) }
        val tasksViewModel = remember { TasksViewModel(taskRepo, taskCategoryRepo) }

        // Cuando Haabi crea algo, refrescamos TODOS los repos para que el dato
// aparezca al instante en su módulo (Tareas/Compras/Agenda/Alertas) y
// dejamos la categoría de tareas activa apuntando a la que eligió la IA.
LaunchedEffect(Unit) {
    val notifier = upeu.edu.pe.Data.notification.createLocalNotificationPlatform()
    HaabiEventBus.events.collect { ev ->
        // Alerta en la bandeja del teléfono cuando Haabi crea algo
        notifier.showAlert("Habitia - ${ev.category ?: "Aviso"}", ev.description)
        when (ev.route) {
            "tasks" -> {
                taskRepo.refresh(); reminderRepo.refresh(); calendarEventRepo.refresh()
                ev.category?.let { tasksViewModel.changeCategory(it) }
            }
            "food" -> {
                mealRepo.refresh(); shoppingRepo.refresh(); taskRepo.refresh()
                reminderRepo.refresh(); calendarEventRepo.refresh()
                ev.category?.let { tasksViewModel.changeCategory(it) }
            }
            "home" -> habitRepo.refresh()
            "diary" -> diaryRepo.refresh()
        }
    }
}
        // FoodViewModel recibe TAMBIÉN el repositorio de compras

        val ingredientCatalogRepo = remember { ApiIngredientCatalogRepository(ApiClient.httpClient, session) }
        val foodViewModel = remember { FoodViewModel(mealRepo, shoppingRepo, ingredientCatalogRepo) }

        // Catálogos (categorías de tareas + ingredientes) se cargan de la BD
// al iniciar sesión, igual que el resto de módulos.
LaunchedEffect(sessionState.token, sessionState.userId) {
    if (sessionState.token != null && sessionState.userId != null) {
        (taskCategoryRepo as? ApiTaskCategoryRepository)?.refresh()
        (ingredientCatalogRepo as? ApiIngredientCatalogRepository)?.refresh()
    }
}
        val remindersViewModel = remember { RemindersViewModel(reminderRepo) }
        val focusViewModel = remember { FocusViewModel() }

        val showBottomBar = currentScreen !is HabitiaScreen.Splash &&
                currentScreen !is HabitiaScreen.Welcome &&
                currentScreen !is HabitiaScreen.Login &&
                currentScreen !is HabitiaScreen.Register &&
                currentScreen !is HabitiaScreen.Profile

        val currentTab = BottomTab.allTabs.firstOrNull { it.screen == currentScreen } ?: BottomTab.Home

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(
                targetState = currentScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = with(LocalDensity.current) { WindowInsets.statusBars.getTop(this).toDp() }),
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    HabitiaScreen.Splash -> SplashContent { currentScreen = if (session.isReady()) HabitiaScreen.Home else HabitiaScreen.Welcome }
                    HabitiaScreen.Welcome -> WelcomeContent { currentScreen = HabitiaScreen.Login }
                    HabitiaScreen.Login -> LoginScreen(
                        authViewModel = authViewModel, // login REAL contra el backend
                        onLoginSuccess = { currentScreen = HabitiaScreen.Home },
                        onNavigateToRegister = { currentScreen = HabitiaScreen.Register }
                    )
                    HabitiaScreen.Register -> RegisterScreen(
                        authViewModel = authViewModel, // registro REAL + login automático
                        onRegisterSuccess = { currentScreen = HabitiaScreen.Home },
                        onNavigateToLogin = { currentScreen = HabitiaScreen.Login }
                    )
                    HabitiaScreen.Home -> HomeScreen(
                        viewModel = homeViewModel,
                        onOpenProfileSettings = { currentScreen = HabitiaScreen.Profile },
                        onNavigateToTasks = { currentScreen = HabitiaScreen.Tasks },
                        onLogout = {
                            // Logout REAL: borra token/email/userId en memoria
                            // y el siguiente login creará todo de nuevo.
                            session.clear()
                            currentScreen = HabitiaScreen.Login
                        },
                        userName = userMenuState.userName,
                        // correo REAL en conjunto: sesión → drawer de Home
                        userEmail = userMenuState.userEmail
                    )
                    HabitiaScreen.Planner -> PlannerScreen(plannerViewModel)
                    HabitiaScreen.Tasks -> TasksScreen(tasksViewModel)
                    HabitiaScreen.Food -> FoodScreen(foodViewModel)
                    HabitiaScreen.Reminders -> RemindersScreen(remindersViewModel)
                    HabitiaScreen.Focus -> FocusScreen(focusViewModel)
                    HabitiaScreen.More -> MoreScreen(
                        onOpenFood = { currentScreen = HabitiaScreen.Food },
                        onOpenReminders = { currentScreen = HabitiaScreen.Reminders },
                        onOpenFocus = { currentScreen = HabitiaScreen.Focus },
                        // "Personalizar Apariencia" va DIRECTO a su apartado
                        // (recordar el ORIGEN: el back de Apariencia debe
                        // regresar a More, no al perfil).
                        onOpenAppearance = {
                            appearanceReturnScreen = HabitiaScreen.More
                            currentScreen = HabitiaScreen.Appearance
                        },
                        onOpenDiary = { currentScreen = HabitiaScreen.Diary },
                        onOpenHaabi = { currentScreen = HabitiaScreen.Haabi },
                        onOpenAnalytics = { currentScreen = HabitiaScreen.Analytics }
                    )
                    HabitiaScreen.Analytics -> AnalyticsScreen(
                        viewModel = analyticsViewModel,
                        onBack = { currentScreen = HabitiaScreen.More }
                    )
                    HabitiaScreen.Diary -> DiaryScreen(
                        onBack = { currentScreen = HabitiaScreen.More },
                        viewModel = diaryViewModel
                    )
                    HabitiaScreen.Haabi -> HaabiScreen(
                        onBack = { currentScreen = HabitiaScreen.More },
                        onNavigate = { route -> currentScreen = routeToScreen(route) },
                        actions = haabiActions
                    )
                    HabitiaScreen.Profile -> UserMenuScreen(
                        viewModel = userMenuViewModel,
                        onBack = { currentScreen = HabitiaScreen.Home },
                        // logout también desde el perfil: sesión borrada + a Login
                        onLogout = {
                            session.clear()
                            currentScreen = HabitiaScreen.Login
                        },
                        // La personalización vive en SU PROPIA pantalla ahora.
                        onOpenAppearance = {
                            appearanceReturnScreen = HabitiaScreen.Profile
                            currentScreen = HabitiaScreen.Appearance
                        }
                    )
                    HabitiaScreen.Appearance -> AppearanceScreen(
                        viewModel = userMenuViewModel,
                        // Vuelve al origen (More o Perfil) — NO siempre al
                        // perfil (bug reportado: entrando desde More, el
                        // back te llevaba al perfil equivocado).
                        onBack = { currentScreen = appearanceReturnScreen }
                    )
                }
            }

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(durationMillis = 280)
                ) + fadeIn(animationSpec = tween(durationMillis = 280)),
                exit = slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = tween(durationMillis = 200)
                ) + fadeOut(animationSpec = tween(durationMillis = 200)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    // zIndex alto: la isla flota SIEMPRE por encima del
                    // contenido de las pantallas (y por encima de sus FABs).
                    .zIndex(2f)
            ) {
                HabitiaBottonNavigation(
                    currentTab = currentTab,
                    onTabSelected = { tab -> currentScreen = tab.screen },
                    modifier = Modifier.padding(bottom = IslandConfig.bottomSpacing)
                )
            }

            // Haabi: mascota flotante + chatbot (visible en todas las
            // pantallas excepto Splash/Welcome/Login/Register).
            val showHaabi = currentScreen !in listOf(
                HabitiaScreen.Splash, HabitiaScreen.Welcome,
                HabitiaScreen.Login, HabitiaScreen.Register
            )
            HaabiCompanion(
                visible = showHaabi,
                onOpenHaabi = { currentScreen = HabitiaScreen.Haabi },
                onNavigate = { route -> currentScreen = routeToScreen(route) },
                modifier = Modifier.zIndex(3f),
                actions = haabiActions
            )
        }
    }
}
