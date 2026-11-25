# 🌿 HABITIA — Proyecto App Moviles

> **Tu hogar en sintonía.** Aplicación multiplataforma (Android + iOS) de hábitos,
> tareas, bienestar y asistente IA, conectada a un backend Spring Boot con
> arquitectura hexagonal y MySQL.

**Repositorio del proyecto:** backend + frontend.  
**Repositorio de documentación / entregables:** `Documentation-Habitia` (guía HTML, Postman, APK, QR, Azure DevOps).

---

## 📑 Tabla de contenidos

1. [Visión general](#1-visión-general)
2. [Estructura del repositorio](#2-estructura-del-repositorio)
3. [Arquitectura](#3-arquitectura)
4. [Base de datos y esquemas](#4-base-de-datos-y-esquemas)
5. [Módulos del backend](#5-módulos-del-backend)
6. [Frontend Kotlin Multiplatform](#6-frontend-kotlin-multiplatform)
7. [Asistente Haabi (IA)](#7-asistente-haabi-ia)
8. [Organización del equipo](#8-organización-del-equipo)
9. [Configuración de claves](#9-configuración-de-claves)
10. [Guía de ejecución](#10-guía-de-ejecución)
11. [Estado real y pendientes](#11-estado-real-y-pendientes)

---

## 1. Visión general

Habitia permite al usuario:

- Registrarse e iniciar sesión con **JWT de 24 h**, sesión persistida en DataStore.
- Gestionar **tareas** con prioridades, categorías editables, matriz Eisenhower y tablero Kanban.
- Seguir **hábitos** con rachas calculadas desde completions reales.
- Planificar **comidas** con ingredientes persistidos y **lista de compras** agrupada por día y comida.
- Usar **calendario**, **recordatorios**, **diario** y temporizador de **enfoque**.
- Conversar con **Haabi**, mascota IA que crea todo lo anterior y agenda alerta.

### Stack

| Capa | Tecnología |
|---|---|
| Frontend | Kotlin Multiplatform + Compose Multiplatform + Material3 |
| DI | Koin (`appModule`) + `SettingProvider` expect/actual |
| Red | Ktor (OkHttp / Darwin) |
| Backend | Spring Boot + Java 21 + arquitectura hexagonal |
| BD | MySQL 8 (`habitia`), Hibernate `ddl-auto=update` |
| IA | Gemini 2.5 Flash (function calling) |
| Glass | Haze ( patrón de 2 capas hermanas) |

---

## 2. Estructura del repositorio

```
Proyecto_Habitia_AppMoviles/
├── README.md                       ← este documento
├── CONTRIBUTING.md                 ← convención de commits y-flow de trabajo
├── docs/
│   └── ARBOLES_DE_TRABAJO.md       ← árbol general + árbol individual por integrante
├── scripts/
│   └── commit_integrante.ps1       ← helper para commits con identidad propia
├── .github/
│   ├── CODEOWNERS                  ← responsables por módulo
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── ISSUE_TEMPLATE/
│       ├── feature_request.md
│       └── bug_report.md
├── Habitia/                        ← FRONTEND (KMP, Android + iOS)
└── HABITIA-Back/                   ← BACKEND (Spring Boot, hexagonal)
```

> El detalle de responsables y archivos por integrante está en
> [`docs/ARBOLES_DE_TRABAJO.md`](docs/ARBOLES_DE_TRABAJO.md).

---

## 3. Arquitectura

### 3.1 Backend hexagonal

```
HABITIA-Back/src/main/java/upeu/edu/pe/HABITIA_Back/
├── HabitiaBackApplication.java
├── auth/                       ← JWT, filtros, SecurityConfig
├── shared/                     ← User + enums + UserRepository
└── modules/
    ├── task/  habit/  reminder/  meal/  shopping/
    ├── calendar/  diary/  user/  catalog/
```

Cada módulo repite la misma forma:

```
modules/<modulo>/
├── domain/model/               ← entidades (tablas)
├── domain/service/             ← lógica pura (ej. InterpreteDeIngredientes)
├── application/service/        ← Service + ServiceImpl (@Transactional)
├── application/dto/            ← Request / Response
├── infrastructure/Controller/  ← endpoints REST
└── infrastructure/persistence/ ← repositorios JPA
```

**Flujo:** `Ktor + JWT → JwtAuthenticationFilter → Controller → Service → Entity → JpaRepository → MySQL`.

### 3.2 Frontend Clean Architecture

```
Habitia/shared/src/commonMain/kotlin/upeu/edu/pe/
├── Core/          (26) ← tema, vidrio, componentes, navegación
├── Data/          (36) ← ApiClient, repos, DTOs, mappers, notificaciones
├── Domain/        (32) ← modelos, interfaces, use cases
├── Presentation/  (32) ← pantallas + ViewModels
└── di/            (2)  ← Modules (Koin) + SettingProvider
```

**Regla:** `Presentation → Domain ← Data`. Presentation y Data nunca se conocen.

---

## 4. Base de datos y esquemas

**Motor:** MySQL, base `habitia`, puerto 3306. `spring.jpa.hibernate.ddl-auto=update`.

| Tabla | Campos principales | Módulo |
|---|---|---|
| `Users` | id, name, email (único), password (BCrypt, `@JsonIgnore`), birthDate, phone | user / shared |
| `Tasks` | id, title, description, dueDate, priority, status, id_user | task |
| `Task_Categories` | id, name, id_user | task / catalog |
| `Habits` | id, name, description, frequency, startDate, endDate, active, id_user | habit |
| `Habit_complet` | id, date, completed, id_habit | habit (rachas) |
| `Reminders` | id, title, description, reminderDate, repeat, completed, id_user | reminder |
| `Meals` | id, name, type, date, time, description, completed, id_user | meal |
| `Meal_Ingredients` | id, description, quantity, unit, id_meal | meal |
| `Shopping_Lists` | id, startDate, endDate, status, id_user | shopping |
| `Shopping_Items` | id, description, quantity, unit, itemDate, sourceMealType, sourceMealName, source, completed, id_list | shopping |
| `Calendar_Events` | id, title, description, startDate, endDate, location, id_user | calendar |
| `DiaryEntries` | id, entryDate, text (≤4000), id_user | diary |
| `Catalog_Ingredients` | id, name, category (48 sembrados) | catalog |

```text
Users ──┬── Tasks ─── Task_Categories
        ├── Habits ── Habit_complet
        ├── Reminders
        ├── Meals ─── Meal_Ingredients
        ├── Shopping_Lists ── Shopping_Items
        ├── Calendar_Events
        └── DiaryEntries
```

---

## 5. Módulos del backend

| Módulo | Endpoints (ejemplo) | Responsable |
|---|---|---|
| Auth | `POST /auth/register`, `/auth/login` | Mauricio |
| User | `GET/PUT /Users/{id}`, `/Users/email/{email}` | Andrea |
| Task | `GET /Tasks/user/{id}`, POST, PUT, DELETE | Andrea |
| Habit | `/Habits`, `/HabitCompletions` | Andrea |
| Reminder | `/Reminders/user/{id}`, `/between` | Andrea |
| Meal | `/Meals`, `/MealIngredients` | Mayler |
| Shopping | `/ShoppingLists/generate`, `/items`, `/confirm` | Mayler |
| Calendar | `/CalendarEvents`, `/user/{id}/between` | Mayler |
| Diary | `/DiaryEntries`, `/date/{date}` | Mauricio |
| Catalog | `/Catalogs/categories`, `/ingredients` | Mauricio |

**Total: 68 endpoints** (colección Postman en el repo de documentación).

---

## 6. Frontend Kotlin Multiplatform

### Pantallas Compose

| Grupo | Pantallas |
|---|---|
| Acceso | Splash, Welcome, Login, Register |
| Día a día | Home, Task, Eisenhower, Planner, Reminders, Focus |
| Bienestar | Food, Diary, Haabi |
| Ajustes | More, UserMenu, Appearance, GlassDiagnostic |

### Persistencia

| Dato | Dónde | Persiste |
|---|---|---|
| JWT + perfil | DataStore (`SessionManager`) | ✅ |
| Tema/paleta | DataStore (`UserPreferencesRepository`) | ✅ |
| Categorías / catálogo | MySQL (`/Catalogs`) | ✅ |
| Tareas / hábitos / comidas | MySQL + caché del repo | ✅ |
| Posición de Haabi / chats | Memoria | ❌ |

---

## 7. Asistente Haabi (IA)

1. El usuario escribe en `HaabiScreen` o toca la mascota flotante.
2. `ApiClient.haabiChat()` envía prompt + historial + 7 herramientas a Gemini 2.5 Flash.
3. Gemini devuelve un `functionCall` (ej. `crear_plan_semanal_comidas`).
4. `executeHaabiAction()` → `HaabiActionsImpl` usa los repositorios reales y guarda en MySQL.
5. `HaabiEventBus.emit()` → refresca la UI, cambia de pestaña y dispara notificación local.

Herramientas: `crear_tarea`, `crear_habito`, `crear_recordatorio`, `crear_comida`, `crear_compra`, `crear_diario`, `crear_plan_semanal_comidas`.

---

## 8. Organización del equipo

### Ramas

```
main
├── dev/mauricio
├── dev/andrea
├── dev/mayler
└── dev/sergio
```

| Integrante | RFs | Módulos / responsabilidad |
|---|---|---|
| **Mauricio** | RF-01, RF-12, RF-14 | Auth/JWT + seguridad, Haabi/Gemini, decidir no importar OpenDroid, Diary y Catálogos |
| **Andrea** | RF-02 a RF-05 | Usuario, Tareas (+ Eisenhower), Hábitos con rachas, Recordatorios |
| **Mayler** | RF-06 a RF-09 | Comidas, Compras por día, Calendario, Enfoque |
| **Sergio** | RF-10, RF-11, RF-13 | Resumen diario, Analítica,Sincronización ( bases + pendientes documentados) |

### Convención de commits

```
RF-NN <tipo>: <qué se hizo>     (tipo = feat | fix | test | docs | refactor)
```

Ejemplo: `RF-03 feat: TaskController con CRUD y filtros`.

Cada integrante trabaja en **su rama `dev/<nombre>`** y commitea con **su propia identidad de Git**. El script [`scripts/commit_integrante.ps1`](scripts/commit_integrante.ps1) facilita el comando correcto; el detalle de archivos por responsable está en [`docs/ARBOLES_DE_TRABAJO.md`](docs/ARBOLES_DE_TRABAJO.md).

> **Importante:** no compartir contraseñas de GitHub. Cada persona ejecuta sus commits
> en su propia máquina con su `user.name` / `user.email`.

---

## 9. Configuración de claves

Por seguridad (GitHub Push Protection) **las API keys no están versionadas**. En
`Habitia/shared/src/commonMain/kotlin/upeu/edu/pe/Data/remote/api/ApiClient.kt` hay dos placeholders:

```kotlin
const val GOOGLE_CALENDAR_API_KEY = "TU_API_KEY_GOOGLE_CALENDAR"
const val GEMINI_API_KEY = "TU_API_KEY_GEMINI"
```

Antes de compilar localmente, reemplazá el valor en **tu copia de trabajo** y no commitees ese cambio. El APK distribuido ya fue compilado con las claves reales.

---

## 10. Guía de ejecución

### Backend
```powershell
cd HABITIA-Back
.\mvnw.cmd spring-boot:run
# "Started HabitiaBackApplication in ~10s"
```

### Frontend
```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
cd Habitia
.\gradlew assembleDebug
```

### URL del backend
`http://192.168.100.210:8081/habitia-backend` (auto-detectada por `ApiClient`; alternativas `10.10.10.100`, `10.0.2.2`, `localhost`).

### Usuarios de prueba
- `a@b.com` / `clave123`
- `prueba.habitia@app.pe` / `Clave12345`

---

## 11. Estado real y pendientes

### ✅ Implementado
Auth JWT, perfil, tareas + categorías, Eisenhower/Kanban, hábitos con rachas, comidas con ingredientes, compras agrupadas, calendario, diario, recordatorios, notificaciones locales, Haabi con function calling, backend hexagonal, Koin y persistencia.

### ⚠️ Pendiente documentado (no inventado)
- RF-10/11/13 backend: `/summary`, `/analytics`, `/sync` (hoy el resumen se calcula en el front).
- RF-09 backend `/focus/*` (timer local no más).
- RF-14 OpenDroid (solo referencia por licencia GPLv3).
- Google Calendar privado (OAuth2), media upload, búsqueda, gamificación.
- Persistencia de posición de Haabi y chats.

---

*Proyecto Habitia — Noviembre 2026.*
