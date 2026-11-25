# Aporte de Sergio — Resumen diario, Analítica y Sincronización

> Rama: `dev/sergio` · Commit: `RF-10/RF-11/RF-13 feat: resumen, analítica y sync (base + pendientes)`

## Qué implementó / estado real (según el proyecto)

### RF-10 — Resumen diario
- **Implementado en el front**: `HomeViewModel.updateProgress()` combina
  tareas de hoy (`GetTodayTasksUseCase`) + hábitos (`GetHabitsUseCase`) y calcula
  el progreso; `HomeScreen.kt` muestra el saludo, la racha y las tarjetas.
- **Pendiente en backend**: `SummaryController /summary/daily`, `/pending`,
  `/habits-progress` y DTO `DailySummaryResponse`. No existe hoy en el código —
  se documenta como siguiente paso, no se inventa.

### RF-11 — Analítica de datos
- **Pendiente**: `AnalyticsService`, consultas JPQL de agregación y endpoints
  `/analytics/trends`, `/analytics/weekly`, `/analytics/balance`. El proyecto hoy
  no tiene estas clases; se deja planificado en el README con el diseño de DTOs.

### RF-13 — Sincronización entre dispositivos
- **Pendiente**: DTOs `SyncPushRequest/SyncPullRequest`, `SyncStatus` y endpoints
  `/sync/push`, `/sync/pull`. La persistencia local ya existe (DataStore) y es la
  base para la sincronización futura.

### Frentes complementarios del documento (Sergio/Mauricio)
- **Notificaciones**: implementadas como locales (`Data/notification/` en KMP,
  canal Android 8+, permiso 13+, disparo desde HaabiEventBus). FCM queda pendiente.
- **Búsqueda, gamificación e integraciones externas (Google Calendar, Fitbit,
  MyFitnessPal)**: declaradas en el documento del profesor, sin implementar en el
  código real — no se inventan endpoints.

## Nota de honestidad
Este archivo separa claramente **lo que existe** de **lo que está pendiente**, para
que el profesor pueda evaluar el avance real sin confusión.
