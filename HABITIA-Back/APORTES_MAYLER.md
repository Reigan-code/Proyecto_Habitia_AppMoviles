# Aporte de Mayler — Comidas, Compras, Calendario y Enfoque

> Rama: `dev/mayler` · Commit: `RF-06/RF-07/RF-08/RF-09 feat: comidas, compras, calendario y enfoque`

## Qué implementó (según el proyecto real)

### RF-06 — Comidas
- `modules/meal/` → `MealsController` (GET por id, por usuario, por fecha, entre
  fechas; POST, PUT, DELETE) y `MealIngredientController` (GET por comida, POST,
  PUT, DELETE).
- `Meals.java` + `MealIngredient.java` → tablas `Meals` y `Meal_Ingredients`.
  Los ingredientes **se persisten en el backend** (POST /MealIngredients por cada
  uno), no en memoria.
- `domain/service/InterpreteDeIngredientes.java` → parsea "200 g de arroz" en
  cantidad + unidad + nombre; tolera texto sin cantidad (asume 1 UNIT).
- Frontend: `FoodScreen.kt`, `MealEditorDialog` con catálogo colapsable por
  `FoodCategory`, comidas agrupadas por tipo y botón **▶ Tutorial** que abre YouTube.

### RF-07 — Lista de compras
- `modules/shopping/` → `ShoppingListController` (`POST /generate`, GET por
  usuario, GET lista, POST items, PUT item, DELETE item, PATCH /confirm).
- `ShoppingItem.java` con `item_date`, `source_meal_type`, `source_meal_name`,
  `unit`, `quantity`, `source` → agrupación por día y por comida.
- Frontend: vista **Por día** (Desayuno/Almuerzo/Cena) y **Por alimento**
  (categorías `FoodCategory` con emoji), generada automáticamente por Haabi.

### RF-08 — Eventos de calendario
- `modules/calendar/` → `CalendarEventController` (GET por id, por usuario,
  entre fechas; POST, PUT, DELETE) + `CalendarEvent.java` → tabla `Calendar_Events`.
- Los eventos que agenda Haabi (09:00–10:00) aparecen en `PlannerScreen.kt`.

### RF-09 — Enfoque
- `FocusViewModel.kt` + `FocusScreen.kt` → temporizador Pomodoro local.
- Pendiente declarado: backend `/focus/start|stop` y tabla `Focus_Sessions` (RN-07).

### Extra — Media/archivos
- Decisión: los assets de Haabi son 12 PNG reales en
  `shared/src/commonMain/composeResources/drawable/`. Google Calendar privado
  requiere OAuth2 (pendiente).
