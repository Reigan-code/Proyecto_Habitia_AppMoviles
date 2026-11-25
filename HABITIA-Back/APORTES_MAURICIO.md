# Aporte de Mauricio — Autenticación, Seguridad JWT e IA

> Rama: `dev/mauricio` · Commit: `RF-01/RF-12 feat: auth JWT + Haabi function calling`

## Qué implementó (según el proyecto real)

### RF-01 — Autenticación y seguridad
- `auth/infrastructure/Controller/AuthController.java` → `POST /auth/register`, `POST /auth/login`
- `auth/infrastructure/security/` → `JwtService` (HS256, 24 h), `JwtAuthenticationFilter`,
  `CustomUserDetailsService`, `SecurityConfig`
- `SecurityConfig`: `/auth/**` público, resto con JWT; **authenticationEntryPoint**
  que devuelve 401 claro (antes daba 403 engañoso en Postman) y `/error` permitido
  para que los 404 no se disfracen de 401.
- `User.java`: `@JsonIgnore` en password (el hash nunca se serializa).

### RF-12 — Asistente IA (Haabi)
- `Data/remote/api/ApiClient.kt` → `haabiChat()` con Gemini 2.5-flash, `haabiTools()`
  (7 herramientas de function calling) y BASE_URL auto-detectada (4 IPs).
- `Domain/actions/HaabiActions.kt` + `Presentation/more/HaabiActionsImpl.kt` → puente
  entre la IA y los repositorios reales: crea tareas, hábitos, comidas, compras,
  diario, agenda el evento y el recordatorio a las 09:00.
- `Core/events/HaabiEventBus.kt` → avisa a toda la app; `App.kt` refresca,
  cambia de pestaña y dispara la notificación local.

### RF-14 — OpenDroid
- Decisión documentada: **NO importar OpenDroid** (GPLv3, 75k líneas, permisos
  de accessibility). Se usa solo como referencia; el contrato `HaabiActions` queda
  listo para comandos de voz.

### Frontend (KMP)
- `App.kt` (wiring manual + Crossfade + safe areas + zIndex de isla y Haabi).
- `Core/components/HaabiCompanion.kt` (mascota arrastrable + Dialog centrado con X).
- `Core/theme/Glass.kt` (patrón Haze de 2 capas hermanas) y `UiConfig.kt`.
- `di/Modules.kt` + `di/SettingProvider.kt` (Koin estilo profesor).
