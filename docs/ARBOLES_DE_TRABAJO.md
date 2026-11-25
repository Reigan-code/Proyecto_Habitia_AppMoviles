# 🌳 Árboles de trabajo — Habitia

> Separación de tareas por RF, árbol general del repositorio y árbol individual
> de trabajo de cada integrante. **Los responsables son los dueños de cada módulo**:
> cada persona commitea en su rama `dev/<nombre>` con su propia identidad de Git.

---

## 1. Árbol general del repositorio

```
Proyecto_Habitia_AppMoviles/
├── README.md
├── CONTRIBUTING.md
├── docs/
│   └── ARBOLES_DE_TRABAJO.md              ← este documento
├── scripts/
│   └── commit_integrante.ps1
├── .github/
│   ├── CODEOWNERS
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── ISSUE_TEMPLATE/{feature_request,bug_report}.md
│
├── HABITIA-Back/                           ← BACKEND (Spring Boot + Java 21)
│   ├── pom.xml
│   ├── mvnw.cmd
│   └── src/main/java/upeu/edu/pe/HABITIA_Back/
│       ├── HabitiaBackApplication.java
│       ├── auth/
│       │   └── infrastructure/
│       │       ├── Controller/AuthController.java
│       │       └── security/{JwtService,JwtAuthenticationFilter,
│       │                     CustomUserDetailsService,SecurityConfig}.java
│       ├── shared/
│       │   ├── domain/model/User.java
│       │   └── infrastructure/persistence/UserRepository.java
│       └── modules/
│           ├── task/       {domain/model, application/{service,dto},
│           │               infrastructure/{Controller,persistence}}     ← Andrea
│           ├── habit/      {…, HabitCompletionController}                ← Andrea
│           ├── reminder/   {…, ReminderController}                       ← Andrea
│           ├── user/       {…, UserController}                           ← Andrea
│           ├── meal/       {…, MealIngredientController,
│           │               domain/service/InterpreteDeIngredientes}     ← Mayler
│           ├── shopping/   {…, ShoppingListController}                   ← Mayler
│           ├── calendar/   {…, CalendarEventController}                  ← Mayler
│           ├── diary/      {…, DiaryEntryController}                     ← Mauricio
│           └── catalog/    {…, CatalogController}                        ← Mauricio
│   └── src/main/resources/application.properties
│
└── Habitia/                                ← FRONTEND (KMP, Android + iOS)
    ├── androidApp/                         ← MainActivity, tema, permisos
    ├── iosApp/                             ← entrada iOS
    ├── shared/src/commonMain/kotlin/upeu/edu/pe/
    │   ├── Core/          (26) ← theme/Glass, components, navigation, UiConfig
    │   ├── Data/          (36) ← remote/ApiClient, repos, DTOs, mappers, notification
    │   ├── Domain/        (32) ← model, repository interfaces, use cases,
    │   │                        actions/HaabiActions
    │   ├── Presentation/  (32) ← 15 pantallas + ViewModels
    │   │                        more/HaabiActionsImpl.kt
    │   │                        more/FocusViewModel.kt
    │   └── di/            (2)  ← Modules.kt (Koin), SettingProvider.kt
    └── shared/src/commonMain/composeResources/drawable/ ← 12 PNG de Haabi
```

---

## 2. Separación de tareas por RF

| RF | Título | Responsable | Rama | Módulos / archivos clave |
|---|---|---|---|---|
| RF-01 | Autenticación y seguridad | Mauricio | `dev/mauricio` | `auth/` (JwtService, filtro, SecurityConfig) |
| RF-02 | Usuario / perfil | Andrea | `dev/andrea` | `modules/user/`, `shared/User.java` |
| RF-03 | Tareas y categorías | Andrea | `dev/andrea` | `modules/task/`, `TaskScreen`, `EisenhowerScreen` |
| RF-04 | Hábitos y rachas | Andrea | `dev/andrea` | `modules/habit/`, `HabitCompletion` |
| RF-05 | Recordatorios | Andrea | `dev/andrea` | `modules/reminder/`, `RemindersScreen` |
| RF-06 | Comidas e ingredientes | Mayler | `dev/mayler` | `modules/meal/`, `FoodScreen`, `MealEditorDialog` |
| RF-07 | Lista de compras | Mayler | `dev/mayler` | `modules/shopping/`, vistas por día / alimento |
| RF-08 | Calendario | Mayler | `dev/mayler` | `modules/calendar/`, `PlannerScreen` |
| RF-09 | Enfoque (Pomodoro) | Mayler | `dev/mayler` | `FocusScreen`, `FocusViewModel` (backend `/focus/*` pendiente) |
| RF-10 | Resumen diario | Sergio | `dev/sergio` | `HomeViewModel.updateProgress()` (backend pendiente) |
| RF-11 | Analítica | Sergio | `dev/sergio` | Diseño de DTOs y consultas (pendiente) |
| RF-12 | Asistente IA Haabi | Mauricio | `dev/mauricio` | `ApiClient`, `HaabiActions`, `HaabiEventBus`, `HaabiCompanion` |
| RF-13 | Sincronización | Sergio | `dev/sergio` | Diseño `SyncPush/Pull` (pendiente) |
| RF-14 | OpenDroid | Mauricio | `dev/mauricio` | Decisión documentada: NO importar (GPLv3) |

### Arbol individual — Mauricio 🧑‍💻

```
dev/mauricio/
├── RF-01  auth/
│          ├── infrastructure/Controller/AuthController.java
│          └── infrastructure/security/
│              ├── JwtService.java            (HS256, 24 h)
│              ├── JwtAuthenticationFilter.java
│              ├── CustomUserDetailsService.java
│              └── SecurityConfig.java        (401 claro, /error permitido)
├── RF-12  Habitia/shared/.../
│          ├── Data/remote/api/ApiClient.kt   (haabiChat + 7 tools)
│          ├── Domain/actions/HaabiActions.kt
│          ├── Presentation/more/HaabiActionsImpl.kt
│          ├── Core/events/HaabiEventBus.kt
│          └── Core/components/HaabiCompanion.kt (mascota arrastrable)
├── RF-14  docs: decisión de no importar OpenDroid
└── extras  modules/diary/, modules/catalog/,
            Core/theme/Glass.kt, di/Modules.kt
```

### Árbol individual — Andrea 🧑‍💻

```
dev/andrea/
├── RF-02  modules/user/{Controller,service,dto,persistence}
│          shared/domain/model/User.java  (phone, birthDate, @JsonIgnore)
├── RF-03  modules/task/{Controller,service,dto,persistence}
│          domain/model/TaskCategory.java
│          Presentation/task/TaskScreen.kt
│          Presentation/task/EisenhowerScreen.kt (matriz + Kanban)
├── RF-04  modules/habit/{Controller,service,dto,persistence}
│          HabitCompletionController.java
│          mapper/computeStreak()  (racha desde completions)
└── RF-05  modules/reminder/{Controller,service,dto,persistence}
           Presentation/reminders/RemindersScreen.kt
```

### Árbol individual — Mayler 🧑‍💻

```
dev/mayler/
├── RF-06  modules/meal/{Controller,service,dto,persistence}
│          MealIngredientController.java
│          domain/service/InterpreteDeIngredientes.java
│          Presentation/food/FoodScreen.kt + MealEditorDialog
├── RF-07  modules/shopping/{Controller,service,dto,persistence}
│          ShoppingListController.java  (generate / confirm)
│          vistas "Por día" y "Por alimento"
├── RF-08  modules/calendar/{Controller,service,dto,persistence}
│          Presentation/planner/PlannerScreen.kt
└── RF-09  Presentation/focus/{FocusScreen.kt,FocusViewModel.kt}
           (backend /focus/* documentado como pendiente)
```

### Árbol individual — Sergio 🧑‍💻

```
dev/sergio/
├── RF-10  HomeViewModel.updateProgress()  (resumen: tareas de hoy + hábitos)
│          Pendiente: SummaryController /summary/daily
├── RF-11  Pendiente documentado: AnalyticsService,
│          consultas JPQL, /analytics/trends | weekly | balance
└── RF-13  Pendiente documentado: SyncPushRequest/SyncPullRequest,
           /sync/push | pull (la base local ya existe en DataStore)
```

---

## 3. Flujo de trabajo (Scrum)

```
Issue (RF-NN)  →  rama dev/<integrante>  →  commits atómicos  →  PR a main  →  merge
```

1. Cada RF se registra como **Issue** con etiqueta `RF-NN` y se asigna a su responsable.
2. Se trabaja en la rama `dev/<integrante>` con commits pequeños: `RF-NN feat: ...`, `RF-NN fix: ...`.
3. Los cambios se integran a `main` mediante **Pull Request** (plantilla en `.github/`).
4. `CODEOWNERS` asegura que el responsable del módulo revise su parte.

> **Nota de honestidad:** este documento describe la **responsabilidad** acordada de
> cada módulo. Los commits son generados por quien realmente ejecuta el trabajo en
> su propia máquina, con su identidad de Git y su fecha real.
