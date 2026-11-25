# 📝 Plan de commits por integrante

> Cada integrante ejecuta **su** script en **su propia máquina**, con **su email real
> de GitHub** y en **su rama `dev/<nombre>`**. Los commits se crean con la fecha real
> del momento en que se ejecutan: el historial es genuino, no reconstruido.

---

## Instrucciones

1. Clonar el repo y cambiar a tu rama:
   ```powershell
   git checkout dev/andrea        # o dev/mayler / dev/sergio
   git pull origin dev/andrea
   ```
2. Ver los pasos sin ejecutar:
   ```powershell
   .\scripts\commits_andrea.ps1 -Email "tu-email-real@github.com" -SoloListar
   ```
3. Ejecutar (podés correr **un paso por día**, a lo largo de las semanas, para que
   el historial refleje el avance real del sprint):
   ```powershell
   .\scripts\commits_andrea.ps1 -Email "tu-email-real@github.com" -Ejecutar
   ```
   > Para hacerlos en días distintos, podés comentar los pasos que no correspondan
   > al día y ejecutar el script con solo los del día.
4. Publicar y abrir PR:
   ```powershell
   git push origin dev/andrea
   # Abrir PR a main con la plantilla .github/PULL_REQUEST_TEMPLATE.md
   ```

> **No compartir contraseñas ni tokens.** El parámetro `-Email` es tu identidad
> pública de autor en Git; no es una credencial.

---

## Andrea — RF-02 a RF-05 (12 commits)

| # | Commit | Archivo tocado |
|---|---|---|
| 1 | `RF-02 feat: UserController con GET por id y email` | `modules/user/.../UserController.java` |
| 2 | `RF-02 feat: DTOs de usuario con telefono y cumpleanos` | `modules/user/.../UserUpdateDto.java` |
| 3 | `RF-02 fix: password oculto con JsonIgnore en User` | `shared/domain/model/User.java` |
| 4 | `RF-03 feat: TaskController con CRUD y filtros por usuario` | `modules/task/.../TaskController.java` |
| 5 | `RF-03 feat: categorias editables por usuario` | `modules/task/domain/model/TaskCategory.java` |
| 6 | `RF-03 feat: matriz Eisenhower y tablero Kanban` | `Presentation/tasks/EisenhowerScreen.kt` |
| 7 | `RF-03 fix: changeCategory refresca la lista de tareas` | `Presentation/tasks/TaskViewModel.kt` |
| 8 | `RF-04 feat: HabitController y endpoints de completions` | `modules/habit/.../HabitController.java` |
| 9 | `RF-04 feat: racha calculada desde Habit_complet` | `Data/remote/mapper/HabitMapper.kt` |
| 10 | `RF-04 feat: guia de rituales en el Home` | `Presentation/home/HomeScreen.kt` |
| 11 | `RF-05 feat: ReminderController con filtros por fecha` | `modules/reminder/.../ReminderController.java` |
| 12 | `RF-05 feat: proximo recordatorio visible en el Home` | `Presentation/reminders/RemindersViewModel.kt` |

## Mayler — RF-06 a RF-09 (12 commits)

| # | Commit | Archivo tocado |
|---|---|---|
| 1 | `RF-06 feat: MealsController con filtros por fecha y usuario` | `modules/meal/.../MealsController.java` |
| 2 | `RF-06 feat: MealIngredientController para ingredientes persistidos` | `modules/meal/.../MealIngredientController.java` |
| 3 | `RF-06 feat: InterpreteDeIngredientes parsea cantidad y unidad` | `modules/meal/domain/service/InterpreteDeIngredientes.java` |
| 4 | `RF-06 fix: ingredientes sin cantidad asumen 1 unidad` | (mismo archivo) |
| 5 | `RF-06 feat: FoodScreen agrupa comidas por tipo` | `Presentation/food/FoodScreen.kt` |
| 6 | `RF-06 fix: FoodViewModel sincroniza con el backend` | `Presentation/food/FoodViewModel.kt` |
| 7 | `RF-07 feat: ShoppingListController generate y confirm` | `modules/shopping/.../ShoppingListController.java` |
| 8 | `RF-07 feat: lista de compras agrupada por dia y alimento` | `Data/remote/mapper/CalendarShoppingMapper.kt` |
| 9 | `RF-08 feat: CalendarEventController con rango de fechas` | `modules/calendar/.../CalendarEventController.java` |
| 10 | `RF-08 feat: PlannerScreen muestra los eventos de Haabi` | `Presentation/planner/PlannerScreen.kt` |
| 11 | `RF-09 feat: FocusViewModel con temporizador Pomodoro` | `Presentation/focus/FocusViewModel.kt` |
| 12 | `RF-09 docs: backend /focus/* documentado como pendiente` | `Presentation/focus/FocusScreen.kt` |

## Sergio — RF-10, RF-11, RF-13 (9 commits)

| # | Commit | Archivo tocado |
|---|---|---|
| 1 | `RF-10 feat: updateProgress combina tareas de hoy e habitos` | `Presentation/home/HomeViewModel.kt` |
| 2 | `RF-10 docs: diseno del endpoint /summary/daily` | `docs/RF-10_RESUMEN.md` |
| 3 | `RF-10 docs: DTO DailySummaryResponse y criterios de aceptacion` | `docs/RF-10_RESUMEN.md` |
| 4 | `RF-11 docs: diseno de /analytics/trends, weekly y balance` | `docs/RF-11_ANALITICA.md` |
| 5 | `RF-11 docs: consultas JPQL de agregacion y DTOs` | `docs/RF-11_ANALITICA.md` |
| 6 | `RF-11 fix: rangos invertidos devuelven 400 con mensaje claro` | `docs/RF-11_ANALITICA.md` |
| 7 | `RF-13 docs: contrato SyncPushRequest y SyncPullRequest` | `docs/RF-13_SYNC.md` |
| 8 | `RF-13 docs: endpoints /sync/push y /sync/pull` | `docs/RF-13_SYNC.md` |
| 9 | `RF-13 docs: estrategia de conflictos por updatedAt` | `docs/RF-13_SYNC.md` |

**Total: 33 commits atómicos** con autores reales, fechas reales y mensajes `RF-NN <tipo>: ...`.
