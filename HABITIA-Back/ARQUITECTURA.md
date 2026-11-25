# HABITIA-BACK — Cómo está armado (hexagonal, corto y claro)

## ÁRBOL (103 archivos)

```
HABITIA-Back/src/main/java/upeu/edu/pe/HABITIA_Back/
│
├── HabitiaBackApplication.java        ← arranca todo (Spring Boot)
│
├── auth/                              ← login, registro, JWT
│   └── infrastructure/
│       ├── Controller/                ← AuthController (POST /auth/login, /register)
│       │   └── dto/                   ← LoginRequestDto, LoginResponseDto
│       └── security/                  ← JwtService, JwtAuthenticationFilter,
│                                        CustomUserDetailsService, SecurityConfig
│
├── shared/                            ← lo que usan TODOS los módulos
│   ├── domain/
│   │   ├── model/User.java            ← el usuario (dueño de todo)
│   │   └── enums/                     ← Priority, TaskStatus, MealType,
│   │                                    Frequency, IngredientUnit, RepeatType...
│   └── infrastructure/persistence/
│       └── UserRepository.java        ← consulta Users por email
│
└── modules/                           ← el negocio, uno por carpeta
    ├── task/      ┐
    ├── habit/     │
    ├── meal/      │
    ├── reminder/  │  cada uno con LA MISMA forma:
    ├── shopping/  │
    ├── calendar/  │    domain/model/               entidades JPA (tablas)
    ├── diary/     │    domain/service/             lógica pura (ej. InterpreteDeIngredientes)
    ├── user/      │    application/service/        Service + ServiceImpl (reglas de negocio)
    └── catalog/   ┘    application/dto/            lo que entra/sale por HTTP
                        infrastructure/Controller/  endpoints REST
                        infrastructure/persistence/ repositorios JPA (SQL)
```

---

## CÓMO PASA UNA PETICIÓN (de la app hasta MySQL)

Ejemplo: **el usuario toca "Crear lista de compras"**

```
1. App Kotlin (Compose)
   FoodScreen → viewModel.addShoppingItem(...)
        ↓  (Ktor, HTTP)
2. INTERNET → http://.../habitia-backend/ShoppingLists/{id}/items
        ↓
3. FILTRO  (auth/infrastructure/security/JwtAuthenticationFilter)
   lee el "Bearer <token>", valida con JwtService, mete el usuario en SecurityContext
   → si no hay token: 401
        ↓
4. CONTROLLER (modules/shopping/infrastructure/Controller/ShoppingListController)
  Recibe el JSON en un DTO (ShoppingItemCreateDto)
  Saca el correo del JWT (authentication.getName()) — NO confía en el userId del body
        ↓
5. SERVICE (modules/shopping/application/service/ShoppingListServiceImpl)
  Aquí está la REGLA DE NEGOCIO:
  "armar lista de compras: junta ingredientes de tus comidas, agrupa
   por nombre+unidad y suma cantidades; si no hay comidas pues lista vacia"
  Valida que la lista sea tuya y esté en DRAFT
        ↓
6. DOMINIO (modules/shopping/domain/model/ShoppingList.java + ShoppingItem.java)
  Entidades JPA = columnas de las tablas shopping_lists y shopping_items
        ↓
7. REPOSITORIO (modules/shopping/infrastructure/persistence/ShoppingItemRepository.java)
  JPA/Hibernate traduce a SQL: INSERT INTO shopping_items ...
        ↓
8. MySQL (BD "habitia")
        ↓
9. Vuelta: el service convierte la entidad a DTO (ShoppingListResponseDto)
   y el controller responde 200 OK → la app actualiza su caché → se ve en pantalla
```

---

## REGLA DE ORO DE CADA MÓDULO

| Capa | Qué hace | Ejemplo |
|---|---|---|
| `Controller/` | Recibe HTTP, valida, llama al service, devuelve DTO | `//creacion de la lista de compras desde tus comidas; sino pues vacia` |
| `application/service` | Reglas de negocio, transacciones, quién es el dueño | `//armar lista de compras: junta ingredientes...` |
| `domain/model` | Entidades (tablas) — NO saben de HTTP | `ShoppingItem` = description, quantity, item_date, source_meal_type |
| `domain/service` | Lógica pura, sin Spring | `InterpreteDeIngredientes` = "200 g de arroz" → 200 + G + arroz |
| `persistence` | Solo SQL (JpaRepository) | `findByUserIdOrderById` |

**Dependencias:** el Controller llama al Service, el Service llama al Repository.
Nadie llama al Controller. `shared/` lo puede usar cualquiera; un módulo NO debería
llamar directamente a otro módulo (salvo casos necesarios: shopping usa meal para
armar la lista, y todos usan `shared`).

---

## EJEMPLOS RÁPIDOS (estilo que pediste)

- `//creacion de la lista de compras desde tus comidas; sino pues vacia` → POST /ShoppingLists/generate
- `//marca habito hecho hoy, funciona si es tuyo; sino pues 404` → POST /HabitCompletions
- `//agenda un evento (lo que crea Haabi); sino pues 401` → POST /CalendarEvents
- `//categorias de tareas, si no hay pues siembra las 3` → GET /Catalogs/categories
- `//JWT: firma, valida y saca el correo del token; si expiro pues 401` → JwtService

---

## ARRANQUE

```powershell
$error = (Get-NetTCPConnection -LocalPort 8081 -State Listen -ErrorAction SilentlyContinue).OwningProcess
if ($error) { Stop-Process -Id $error -Force }
cd C:\ProyectoAppMoviles\HABITIA-Back
.\mvnw.cmd spring-boot:run
# esperar: "Started HabitiaBackApplication in ~9s"
```
