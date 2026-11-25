# Aporte de Andrea — Usuario, Tareas, Hábitos y Recordatorios

> Rama: `dev/andrea` · Commit: `RF-02/RF-03/RF-04/RF-05 feat: usuario, tareas, hábitos y recordatorios`

## Qué implementó (según el proyecto real)

### RF-02 — Usuario / perfil
- `modules/user/` → `UserController` (`GET /Users/{id}`, `GET /Users/email/{email}`,
  `PUT /Users/{id}`, `DELETE`), `UserService` + `UserServiceImpl`, DTOs
  (`UserCreateDto`, `UserResponseDto`, `UserUpdateDto` con `phone`).
- Perfil real del JWT: nombre, cumpleaños y teléfono; el saludo del Home se
  actualiza porque la sesión es un StateFlow.

### RF-03 — Tareas
- `modules/task/` → `TaskController` (GET por id, por usuario, por estado; POST,
  PUT, DELETE), `TaskService` + Impl, DTOs (`TaskCreateDto`, `TaskResponseDto`,
  `TaskUpdateDto`).
- `modules/task/domain/model/TaskCategory.java` + `CatalogController` →
  categorías editables por el usuario (Hogar, Compras, Alimentación + las que agregue).
- Frontend: `TaskScreen.kt`, `EisenhowerScreen.kt` (matriz 2x2 y Kanban drag&drop),
  `TasksViewModel.kt` con `changeCategory`, `addNewTask`, `completeTask`, `moveTaskInMatrix`.

### RF-04 — Hábitos y rachas
- `modules/habit/` → `HabitController` (CRUD, filtros por activo) y
  `HabitCompletionController` (POST, GET por hábito/fecha/rango, PUT, DELETE).
- `HabitCompletion` en la tabla `Habit_complet` → de ahí se deriva el **streak**
  (`computeStreak()` en `HabitMapper.kt`, días consecutivos hasta hoy).
- Frontend: `RitualCard`, guía paso a paso en Home; toggle real vía completions.

### RF-05 — Recordatorios
- `modules/reminder/` → `ReminderController` (GET por usuario, por id, por estado
  completado, entre fechas; POST, PUT, DELETE), `ReminderService` + Impl,
  DTOs `ReminderCreateDto/Response/Update` con `RepeatType`.
- Frontend: `RemindersScreen.kt` + `ReminderRowItem`, `getUpcomingReminder()`
  ordenado por fecha+hora y mostrado en el Home.
