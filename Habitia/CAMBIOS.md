# CAMBIOS.md â€” Registro de mejoras del proyecto Habitia

Fecha: 30 de septiembre de 2026
Proyecto: Habitia (Kotlin Multiplatform â€” Android + iOS, Compose Multiplatform)

---

## PACKAGING DE ESTA ENTREGA

Este paquete agrega **5 funcionalidades nuevas** sobre la base de
Clean Architecture ya existente (Domain â†’ Data â†’ Presentation â†’ Core):

1. Cambio del **fondo (background)** de toda la aplicaciÃ³n.
2. El **generador de paletas tambiÃ©n produce fondo y superficie** armÃ³nicos.
3. **Creador de paletas propias** ingresando cÃ³digos HEX manualmente.
4. **TipografÃ­as extendidas** (de 5 a 14 fuentes reales), con escala responsiva.
5. **Alimentos editables**: al hacer click en cualquier comida se editan
   todos sus datos (tambiÃ©n crear y eliminar).

---

## FASE A â€” Recursos: fuentes TTF (17 archivos)

**Carpeta creada:**
`shared/src/commonMain/composeResources/font/`

Se descargaron los TTF reales desde Google Fonts (vÃ­a su API CSS,
seleccionando el formato truetype y los pesos 400/700):

| Fuente            | Archivos                                          |
|-------------------|---------------------------------------------------|
| Montserrat        | montserrat_regular.ttf, montserrat_bold.ttf       |
| Raleway           | raleway_regular.ttf, raleway_bold.ttf             |
| Open Sans         | opensans_regular.ttf, opensans_bold.ttf           |
| Lato              | lato_regular.ttf, lato_bold.ttf                   |
| Playfair Display  | playfairdisplay_regular.ttf, playfairdisplay_bold.ttf |
| Pacifico          | pacifico_regular.ttf (solo peso Regular)          |
| Space Grotesk     | spacegrotesk_regular.ttf, spacegrotesk_bold.ttf   |
| Dancing Script    | dancingscript_regular.ttf, dancingscript_bold.ttf |
| Josefin Sans      | josefinsans_regular.ttf, josefinsans_bold.ttf     |

Los accesores `Res.font.xxx` se generan automÃ¡ticamente al compilar
(tarea `generateComposeResClass`). Todos los archivos fueron verificados
con la firma binaria de TTF (`00 01 00 00`).

---

## FASE B â€” TipografÃ­as extendidas y responsivas

### `Domain/model/ThemeConfig.kt`
- `AppFontFamily` pasa de 5 a **14 entradas**: se agregaron MONTSERRAT,
  RALEWAY, OPEN_SANS, LATO, PLAYFAIR_DISPLAY, PACIFICO, SPACE_GROTESK,
  DANCING_SCRIPT y JOSEFIN_SANS.
- `UserThemePreference` sin cambios (ya soportaba todo).

### `Core/theme/Typography.kt`
- `toComposeFontFamily()` ahora es `@Composable` y carga las 9 fuentes
  nuevas desde `composeResources/font/` con peso Regular + Bold
  (Pacifico solo Regular). Las 5 originales siguen usando familias
  genÃ©ricas del sistema (Quicksandâ†’SansSerif, Caveatâ†’Cursive, etc.).
- `toTypography()` ahora define la **escala completa de Material 3**
  (display, headline, title, body y label) con `lineHeight` en todos los
  estilos y tamaÃ±os moderados: ningÃºn texto se desborda en pantallas
  pequeÃ±as y todo escala en `sp` (respeta el ajuste de fuente del sistema).
- **Nota tÃ©cnica del import de recursos**: los accesores de fuentes NO son
  miembros directos del objeto `Res`, sino **propiedades de extensiÃ³n de
  nivel superior** generadas en el paquete
  `habitia.shared.generated.resources` (paquete derivado de
  rootProject.name="Habitia" + mÃ³dulo "shared", comportamiento por defecto
  de Compose Multiplatform 1.7). Kotlin exige importar las extensiones de
  forma explÃ­cita, por eso en `Typography.kt` se usa:
  `import habitia.shared.generated.resources.*` (trae el objeto `Res` y las
  17 extensiones `Res.font.xxx`, y admitirÃ¡ fuentes nuevas sin cambios).
  La funciÃ³n `Font()` sÃ­ viene de `org.jetbrains.compose.resources.Font`.
  Si algÃºn dÃ­a se renombra el proyecto raÃ­z, este import habrÃ­a que
  actualizarlo.

---

## FASE C â€” Fondos y paletas personalizadas

### `Core/theme/Color.kt`
Nuevas utilidades:
- `isValidHexColor(hex)` â†’ valida `#RRGGBB` o `RRGGBB`.
- `createCustomPalette(name, primaryHex, secondaryHex, tertiaryHex,
  backgroundHex, surfaceHex)` â†’ crea una `GeneratedPalette` desde HEX
  manuales; devuelve `null` si algÃºn cÃ³digo es invÃ¡lido.
- `presetBackgrounds` â†’ 8 fondos suaves predefinidos
  (Blanco LÃ¡cteo, Rosa Suave, Azul Cielo, Menta, Lavanda, Durazno,
  Beige, Gris Perla).
- `CoolorsGenerator.generateHarmoniousPalette()` ahora **tambiÃ©n genera
  fondo y superficie**: un tinte muy claro del tono base (saturaciÃ³n 22%,
  luminosidad 96%) y una superficie casi blanca (10% / 99%). Antes el
  fondo era siempre `#F4F1F8` fijo.

### `Presentation/UserMenu/CustomPaletteCreator.kt` (NUEVO)
- `CustomPaletteCreator(onApply, onSave)`: tarjeta con nombre + 5 campos
  HEX (primario, secundario, terciario, fondo, superficie), vista previa
  en vivo en franja de 5 colores, validaciÃ³n con mensaje de formato
  `#RRGGBB` y botones **Aplicar / Guardar**.
- `HexColorField`: campo reutilizable con punto de color de vista previa.
- `BackgroundSelector(onBackgroundSelected)`: fila horizontal con los 8
  fondos predefinidos; al hacer click cambia el fondo de TODA la app.

### `Presentation/UserMenu/UserMenuViewModel.kt`
- NUEVO `onBackgroundSelected(backgroundHex)`: toma la paleta activa
  (o convierte la del enum en una generada con sus mismos colores) y le
  aplica el nuevo fondo, conservando primario/secundario/terciario.
  Evita acumular el sufijo " Â· Fondo" si se eligen varios seguidos.

### `Presentation/UserMenu/UserMenuScreen.kt`
- Se insertÃ³ la secciÃ³n **5.5 "Crea tu Paleta (HEX)"** (`CustomPaletteCreator`).
- Se insertÃ³ la secciÃ³n **5.6 "Fondo de la AplicaciÃ³n"** (`BackgroundSelector`).
- El strip de vista previa del generador ahora muestra `surface`
  en lugar de `onSurfaceVariant`.
- El selector de tipografÃ­a muestra **14 fuentes en chips de 2 por fila**
  (mÃ¡s compacto) y cada chip se renderiza con su propia fuente real.

> Flujo de todo cambio de tema (ya existente y respetado):
> UI â†’ ViewModel â†’ UseCase â†’ Repositorio (Flow) â†’ Estado â†’
> `App.kt` â†’ `HabitiaTheme` â†’ repintado global.
> El fondo funciona porque `GeneratedPalette.toColorScheme()` usa
> `background` y `surface` de la paleta activa.

---

## FASE D â€” Alimentos editables (click â†’ editar todo)

### `Domain/repository/MealRepository.kt`
Se agregaron al contrato:
- `suspend fun addMeal(meal: Meal)`
- `suspend fun updateMeal(meal: Meal)`
- `suspend fun deleteMeal(mealId: String)`

### `Data/repository/MockMealRepositoryImpl.kt`
- Refactorizado de `flow { emit(...) }` estÃ¡tico a **`MutableStateFlow`**
  (misma tÃ©cnica que `MockTaskRepositoryImpl`): cualquier cambio se
  re-emite a todos los observadores en tiempo real.
- Implementa add/update/delete operando sobre el StateFlow.

### `Presentation/food/FoodViewModel.kt`
- Conserva la referencia `private val repository` (antes solo se usaba
  en el inicializador).
- NUEVOS: `addMeal(meal)` (genera id si viene vacÃ­o), `updateMeal(meal)`,
  `deleteMeal(mealId)`, delegando en el repositorio.

### `Presentation/food/FoodScreen.kt`
- `MealCard` ahora es **clicable** (con Ã­cono de lÃ¡piz como pista visual)
  y abre el editor.
- NUEVO `MealEditorDialog`: diÃ¡logo con **TODOS los campos editables**:
  nombre, tipo (chips Desayuno/Almuerzo/Cena/Snack), fecha,
  descripciÃ³n, ingredientes (separados por comas) e imagen (URL opcional).
  Incluye botÃ³n **"Eliminar esta comida"** (solo para existentes).
- El FAB ahora tambiÃ©n funciona en la pestaÃ±a **MenÃº** (crea comidas
  nuevas); en Compras sigue aÃ±adiendo ingredientes.
- Helper `mealTypeLabel()` unificado para no repetir el `when`.

---

## ARCHIVOS QUE NO REQUISIERON CAMBIOS (la arquitectura ya los soportaba)

- `App.kt` â€” ya pasa `activePalette` e `isDarkMode` a `HabitiaTheme`.
- `Core/theme/Theme.kt` â€” ya consume `GeneratedPalette` con background/surface.
- `Presentation/UserMenu/UserMenuState.kt` â€” ya expone `activePalette`.
- `Domain/repository/UserPreferencesRepository.kt`, mock y DataStore â€”
  la interfaz de preferencias ya persiste los 6 mÃ©todos.
- `GetWeeklyMealsUseCase` â€” sigue funcionando contra el nuevo repositorio.

---

## CÃ“MO PROBAR

1. `.\gradlew.bat :androidApp:assembleDebug` y ejecutar en emulador/dispositivo.
2. Perfil (Editar Perfil & Temas):
   - "Crea tu Paleta (HEX)": cambia los cÃ³digos, mira la vista previa y toca Aplicar.
   - "Fondo de la AplicaciÃ³n": toca cualquier fondo â†’ toda la app cambia al instante.
   - "Generar Otra Paleta": cada paleta generada trae su propio fondo armÃ³nico.
   - "TipografÃ­a del Sistema": 14 fuentes con vista previa real en cada chip.
3. AlimentaciÃ³n (pestaÃ±a MenÃº):
   - Click en cualquier comida â†’ editor con todos sus datos.
   - BotÃ³n + â†’ crear comida nueva. BotÃ³n "Eliminar esta comida" â†’ borrarla.

## VERIFICACIÃ“N DE COMPILACIÃ“N

Verificado tras aplicar TODOS los cambios de este documento:

- `.\gradlew.bat :shared:compileDebugKotlinAndroid` â†’ **BUILD SUCCESSFUL**
  (todo el cÃ³digo multiplataforma: Domain, Data, Presentation, Core).
- `.\gradlew.bat :androidApp:assembleDebug` â†’ **BUILD SUCCESSFUL**
  (APK completo, con las 17 fuentes empaquetadas vÃ­a
  `copyDebugComposeResourcesToAndroidAssets`).
  Salida: `androidApp/build/intermediates/apk/debug/androidApp-debug.apk`
  (17.5 MB, generado hoy). Nota: AGP moderno materializa el APK en
  `intermediates/apk/debug` mediante el redirect de Android Studio; si lo
  abres desde el IDE (Run â–¶), es ese mismo archivo el que se instala.

Durante la verificaciÃ³n se detectaron y corrigieron 2 problemas de
configuraciÃ³n de los recursos generados (documentados en la nota tÃ©cnica
de la secciÃ³n de Typography): el paquete real del objeto `Res` y la
necesidad de importar las propiedades de extensiÃ³n `Res.font.xxx`.

Los Ãºnicos avisos restantes son cosmÃ©ticos (iconos deprecated â†’ versiÃ³n
AutoMirrored, y un @OptIn(ExperimentalCoroutinesApi) en TaskViewModel),
de manejo manual opcional: no afectan la compilaciÃ³n ni la ejecuciÃ³n.

---

# FASE LIQUID GLASS / GLASSMORPHISM (rediseÃ±o de views)

Fecha: 13 de octubre de 2026
Herramienta de diseÃ±o: skill `ui-ux-pro-max` (instalado en `.opencode/skills/`),
que dictamina: aplicar el material glass con moderaciÃ³n a superficies de
CONTROL (navegaciÃ³n, tarjetas), mantener el contenido en capa separada,
contraste â‰¥ 4.5:1, y estados seleccionados inequÃ­vocos.

## Decisiones UX registradas

1. **Barra de navegaciÃ³n "SOLID GLASS"** (feedback directo del usuario):
   NO totalmente transparente (se veÃ­a el contenido desplazÃ¡ndose detrÃ¡s =
   ruido visual). Ahora: tinte casi sÃ³lido (95%) + entorno glass (borde de
   luz, brillo especular, sombra suave) + forma pÃ­ldora 28dp, flotando 12dp
   sobre el contenido. Sin blur (no aporta siendo casi opaca â†’ mejora el
   rendimiento al no capturar capas).
2. **Blur real (Haze) reservado para tarjetas de contenido**: Home, Tasks,
   Planner, Reminders, Food, More usan `hazeSource` LOCAL por pantalla.
   Cada pantalla posee su propio `HazeState` (autocontenida, sin acoplamiento).
3. **Animaciones aÃ±adidas**:
   - Barra de navegaciÃ³n: entra/sale con `AnimatedVisibility`
     (slide vertical + fade; entrada 280ms, salida 200ms â€” regla UX
     "exit-faster-than-enter").
   - Color de tab seleccionado: `animateColorAsState` tween(300ms) (existente).
   - Transiciones entre pantallas: `Crossfade` (existente).
4. **Estados sÃ³lidos vs glass**: dÃ­a seleccionado del Planner, chips,
   botones primarios y FAB conservan color pleno â€” son estados/acciones;
   el glass es para superficies pasivas de contenido.
5. **Todo redondeado coherente**: pÃ­ldora 28dp, tarjetas 14-20dp,
   FAB 18dp, panel auth 26dp, esfera de enfoque circular.
6. **CÃ³digo muerto preservado** (decisiÃ³n del usuario): los componentes
   sin uso (`Items/*`, `HabitiaDialog`, `HabitiaMealCard`,
   `AppearanceSettingsSection`) se conservan como "versiones anteriores";
   NO se eliminaron.

## Infraestructura (aplicada por el usuario en fases 1-2, verificada aquÃ­)

- `gradle/libs.versions.toml`: `haze = "1.2.2"` + librerÃ­a
  `dev.chrisbanes.haze:haze` (blur multiplataforma: Android API 31+ real,
  menor a 31 degrada a tinte translÃºcido sin crash).
- `shared/build.gradle.kts`: `implementation(libs.haze)` en commonMain.
- `Core/theme/Glass.kt`: tokens del material glass (claro/oscuro automÃ¡ticos,
  derivados de la paleta activa), brushes de borde de luz y especular, y el
  modificador `Modifier.habitiaGlass(hazeState, shape, containerAlpha, onClick)`
  que pinta la receta completa: sombra â†’ clip â†’ blur (si hay estado) â†’ tinte
  â†’ especular â†’ borde de luz.
- `Core/components/HabitiaGlassCard.kt`: tarjeta de vidrio reutilizable
  (drop-in de HabitiaCard).
- `Core/components/HabitiaGlassFab.kt`: FAB de vidrio (primario casi sÃ³lido
  + borde de luz, 58dp â‰¥ 48dp de touch target).

## Aplicado en esta sesiÃ³n (por el asistente)

| Archivo | Cambio |
|---|---|
| `Core/navigation/HabitiaBottonNavigation.kt` | Reescrita: "solid glass" 95%, sin hazeState, param `containerAlpha` documentado |
| `App.kt` | Barra envuelta en `AnimatedVisibility` (slide+fade); eliminada la fuente global de blur (cada pantalla gestiona la suya) |
| `Presentation/tasks/TaskScreen.kt` | hazeSource local, `TaskRowItem` glass (documentado), FAB glass, diÃ¡logo translÃºcido 0.92 |
| `Presentation/planner/PlannerScreen.kt` | hazeSource local, dÃ­as inactivos glass / activo sÃ³lido, `EventCard` glass, FAB glass, diÃ¡logo 0.92 |
| `Presentation/reminders/RemindersScreen.kt` | hazeSource local, `ReminderRowItem` glass (completado = tinte 30% â†’ "etÃ©reo"), FAB glass, diÃ¡logo 0.92 |
| `Presentation/focus/FocusScreen.kt` | hazeSource local, esfera del temporizador convertida en burbuja glass circular (55%) |
| `Presentation/more/MoreScreen.kt` | hazeSource local, 4 tarjetas de acceso â†’ `HabitiaGlassCard` clicable |
| `Presentation/UserMenu/UserMenuScreen.kt` | Tarjeta del Generador Coolors â†’ glass estÃ¡tico (ya tenÃ­a TopAppBar translÃºcido y diÃ¡logo 0.92, aplicados por el usuario) |
| `Presentation/UserMenu/CustomPaletteCreator.kt` | Tarjeta del creador HEX â†’ glass estÃ¡tico |
| `Presentation/food/FoodScreen.kt` | 2 diÃ¡logos translÃºcidos 0.92 (compra + editor de comidas) |
| `Presentation/auth/LoginScreen.kt` | Formulario envuelto en panel glass 26dp / alpha 0.72 (botÃ³n primario sÃ³lido) |
| `Presentation/auth/RegisterScreen.kt` | Ãdem, coherencia de marca en todo el flujo auth |

Aplicado previamente por el usuario (verificado en auditorÃ­a): Glass.kt,
componentes glass, Home, Food (tarjetas/FAB), estructura overlay de App.kt,
TopAppBar translÃºcido, diÃ¡logo de foto.

## Checklist de verificaciÃ³n en emulador (recorrido completo)

1. Home / Tasks / Planner / Reminders / Food / More: tarjetas de vidrio
   difuminando el fondo al hacer scroll (emulador API 31+ para blur real).
2. Barra inferior: pÃ­ldora casi sÃ³lida con borde de luz; entra deslizÃ¡ndose
   al iniciar sesiÃ³n y se desliza fuera en Login/Perfil.
3. Focus: esfera de vidrio con el temporizador.
4. Login/Register: panel de vidrio conteniendo el formulario.
5. Cambiar paleta en Perfil â†’ TODO el sistema glass se retinta en caliente
   (claro y oscuro).
6. DiÃ¡logos translÃºcidos con scrim legible.

## PrÃ³ximo paso propuesto (pendiente de aprobaciÃ³n)

Calendario mensual real con `com.kizitonwose.calendar:compose` (multiplataforma)
+ integraciÃ³n opcional con Google Calendar, con el mismo lenguaje visual glass.

---

# REDISEÃ‘O HOMESCREEN â€” "CARD-STACK" (auditorÃ­a UI/UX aplicada)

Fecha: 13 de octubre de 2026

## CrÃ­tica recibida â†’ soluciÃ³n implementada

| # | Problema (auditorÃ­a) | SoluciÃ³n en cÃ³digo |
|---|---|---|
| 1 | Saludo "Hola, bienvenido" robÃ³tico | Saludo por hora del dÃ­a (`kotlinx-datetime`): "Buenos dÃ­as/tardes/noches, {primer nombre}" â€” el nombre llega desde `UserMenuState` vÃ­a `App.kt` |
| 1 | TÃ­tulo/descripciÃ³n de tarea competÃ­an | `PriorityTaskRow`: tÃ­tulo `titleMedium.Bold` + descripciÃ³n `bodySmall` al 72% â€” jerarquÃ­a clara |
| 1 | Texto secundario pequeÃ±o y gris | El progreso del dÃ­a ahora es `displaySmall.Bold` en el HeroCard ("5 de 8") |
| 2 | Contraste mÃ­nimo fondo/tarjeta | `Glass.kt`: tinte claro 0.62 â†’ **0.80** + sombra 4 â†’ **6dp** (afecta todo el sistema, coherente) |
| 2 | WCAG del texto gris | Descripciones con `onSurface.copy(alpha=0.72f)` en vez del gris plano |
| 2 | Barra fina; % parecÃ­a alerta | Barra gruesa **12dp con degradado** primarioâ†’terciario; % en badge de gradiente con **texto adaptado por luminancia** (nunca color de error) |
| 3 | Poco padding interno | Todas las tarjetas rediseÃ±adas usan padding interno **16-20dp** |
| 3 | Mucho vacÃ­o entre tarjetas | Spacing del LazyColumn 16 â†’ **12dp** |
| 4 | Touch target = solo checkbox | **Toda la fila de tarea es clicable** (`habitiaGlass(onClick=...)` + checkbox sincronizado) |
| 4 | Campana genÃ©rica para medicaciÃ³n | **Icono contextual**: `Medication` para salud, `Repeat` para recurrente, `Notifications` normal |
| 4 | Scroll | `key = { it.id }` en las listas (recomposiciÃ³n eficiente) |
| 5 | Estilo anticuado Material 2 | Radios 16-26dp, tonales derivados de la paleta activa, micro-ilustraciones emoji |

## Nueva distribuciÃ³n vertical (estilo Flo/Clue)

1. **Header** â€” saludo personal + avatar (drawer)
2. **HeroCard** (glass, 300dp â‰ˆ 40% viewport):
   - Fila de 3 fechas reales (Ayer/Hoy/MaÃ±ana vÃ­a `kotlinx-datetime`), "Hoy" en pÃ­ldora primaria
   - "5 de 8 tareas y rituales" en grande + "DÃ­a X de racha" (max de hÃ¡bitos)
   - Barra gruesa con degradado + botÃ³n pÃ­ldora glass "Detalles" (â†’ Tareas)
   - Badge circular gradiente "82%" (texto adaptado por luminancia para WCAG)
3. **QuickToggleCard** (glass fina) â€” "Â¿Iniciaste tu ritual matutino?" + toggle segmentado SÃ­/No (estado local, listo para persistir con backend)
4. **Agenda** â€” con icono contextual por tipo de recordatorio
5. **Tareas prioritarias** â€” `PriorityTaskRow` (jerarquÃ­a + clic total + chip "Hoy Â· 18:00")
6. **Consejos para hoy** â€” 3 tarjetas tonales (~170dp) con scroll horizontal y emoji ilustrativo
7. **Tus rituales diarios** â€” carrusel con emoji circular grande, estado activo/inactivo por alpha (0.6/1.0) y toggle al click

## Dependencia nueva

- `kotlinx-datetime 0.6.1` (`org.jetbrains.kotlinx:kotlinx-datetime`) en
  commonMain: fechas multiplataforma para el HeroCard y el saludo por hora
  (mismo cÃ³digo en Android e iOS, sin hacks de plataforma).
- **LecciÃ³n tÃ©cnica documentada** (verificada contra el JAR de fuentes de la
  librerÃ­a): en `kotlinx-datetime`, `LocalDate.plus/minus` **no son miembros
  de la clase** sino funciones de EXTENSIÃ“N de nivel superior del paquete
  (`fun LocalDate.minus(value: Long, unit: DateTimeUnit.DateBased)`, etc.).
  En Kotlin las extensiones NO se resuelven por pertenecer al paquete: hay
  que importarlas. SoluciÃ³n adoptada: `import kotlinx.datetime.*` (comodÃ­n,
  como en los ejemplos oficiales) y aritmÃ©tica `today.minus(1, DateTimeUnit.DAY)`.
  Las dos compilaciones previas fallaron por intentar `plus/minus(Int)` y
  `plus/minus(DateTimeUnit)` sin importar las extensiones â€” los errores
  "Cannot infer type for this parameter" del `remember` eran cascada.

## Versiones anteriores preservadas

`HomeTaskRow` y `HomeHabitRow` (las filas originales del Home) se conservan
al final del archivo bajo el banner "[VERSIÃ“N ANTERIOR]", fuera de uso pero
disponibles para rollback â€” decisiÃ³n del usuario de mantener cÃ³digo previo.

## Archivos tocados

| Archivo | Cambio |
|---|---|
| `Core/theme/Glass.kt` | Tokens claros: alpha 0.80 + sombra 6dp (contraste) |
| `gradle/libs.versions.toml` | + kotlinx-datetime 0.6.1 |
| `shared/build.gradle.kts` | + implementation(libs.kotlinx.datetime) |
| `Presentation/home/HomeScreen.kt` | Reescrita completa: card-stack + 7 secciones |
| `App.kt` | Pasa `userName` al saludo de Home |

---

# FIX â€” "El centro de las tarjetas glass se ve plano"

Fecha: 13 de octubre de 2026 (feedback del usuario tras ver el emulador)

## DiagnÃ³stico

SÃ­ntoma: el contorno de las tarjetas se ve glass (borde de luz, especular,
sombra) pero el centro â€”donde va la informaciÃ³nâ€” se lee como un rectÃ¡ngulo
de color plano.

Causa fÃ­sica: **el vidrio solo se percibe como vidrio cuando hay algo detrÃ¡s
que difuminar**. El blur de un color plano y uniforme produce exactamente el
mismo color plano (difuminar `#F4F1F8` = `#F4F1F8`). Como los fondos de las
pantallas eran planos, el `hazeEffect` no tenÃ­a materia prima; el tinte
casi opaco (0.80, pedido de contraste) acentuaba la sensaciÃ³n sÃ³lida del
centro. Es el mismo problema que resuelven Flo, Clue y Apple Liquid Glass:
**glass sobre fondos con gradientes suaves, jamÃ¡s sobre color liso**.

## SoluciÃ³n: fondo ambiental `habitiaAmbient()` (en `Glass.kt`)

Nuevo modificador que pinta la base del tema + **3 blobs radiales suaves**
derivados de la paleta ACTIVA del usuario:

1. Primario â†’ arriba-derecha (22% alpha; 14% en oscuro)
2. Terciario â†’ abajo-izquierda (16%; 10% en oscuro)
3. Secundario â†’ centro (10%; 6% en oscuro)

Al derivar del `colorScheme`, los blobs **se retintan en caliente** al
cambiar de paleta en Personalizar Temas, y se atenÃºan solos en modo oscuro.
Reemplaza a `.background(colorScheme.background)` en la raÃ­z de cada
pantalla, junto a `hazeSource`, de modo que el blur tambiÃ©n captura los blobs
al pasar contenido por detrÃ¡s.

## Aplicado en

Home, Tasks, Planner, Reminders, Food, Focus, More (raÃ­ces glass) y
Login/Register (los paneles de vidrio del auth ahora refractan color).
Splash y Welcome mantienen fondo plano deliberadamente: son momentos de
branding puro, sin superficies glass.

## Ajuste fino disponible

- Â¿Blobs demasiado sutiles/ intensos? Tunear los alpha de
  `Modifier.habitiaAmbient()` en `Glass.kt` (blob1/2/3Alpha).
- Â¿Centro aÃºn muy sÃ³lido? Bajar `containerAlpha` claro de 0.80 a ~0.72 en
  `glassTokens()` â€” con el fondo ambiental el contraste ya no depende solo
  del tinte.

---

# AUDITORÃA EXTERNA AL SISTEMA GLASS â€” veredicto y adopciÃ³n parcial

Fecha: 13 de octubre de 2026

Se recibiÃ³ una propuesta de "correcciÃ³n" de HabitiaGlassCard/habitiaGlass
con 4 puntos. Veredicto contra el cÃ³digo real y el JAR de fuentes de Haze
1.2.2 (`HazeStyle(tints: List<HazeTint>, blurRadius, noiseFactor,
fallbackTint)` â€” la API usada por la propuesta SÃ existe):

## Adoptado (las 3 ideas vÃ¡lidas)

1. **`tint: Color` param** â€” vidrio teÃ±ido para tarjetas de Ã©nfasis.
   Aplicado al HeroCard: `tint = colorScheme.primary, containerAlpha = 0.35`
   â†’ el Hero ahora se distingue de las tarjetas surface de contenido.
2. **`elevation: Dp?` param** â€” override de sombra. Hero usa 8dp.
3. **`contentPadding: PaddingValues` param** â€” CON DEFAULT 0 (no 16dp como
   proponÃ­a: habrÃ­a duplicado el padding de las ~20 llamadas existentes) +
   las 3 reglas de uso en el KDoc (no background sobre el glass, fondo no
   plano en la fuente, contenido en capa separada).

## Rechazado con evidencia

1. **"No expone elevation/shadow"** â€” falso: `glassTokens()` aplica 6dp
   (claro) / 8dp (oscuro) en el paso 1 de la receta.
2. **"Falta fallback con hazeState == null"** â€” falso: existe el glass
   estÃ¡tico. AdemÃ¡s, el fallback NUESTRO es mÃ¡s robusto: pintamos el tinte
   como `background()` independiente del blur (siempre visible, incluso en
   Preview); la propuesta movÃ­a el tinte dentro de `HazeStyle`, donde un
   Preview sin blur dejarÃ­a la card invisible. (NÃ³tese que Haze 1.2.2
   ofrece `fallbackTint` para ese caso â€” no lo necesitamos con nuestro
   enfoque.)
3. **Reemplazo total del modificador** â€” no compila (falta `@Composable`
   usando `MaterialTheme`), y eliminarÃ­a los tokens adaptativos (modo
   oscuro, especular, ajuste de contraste) y el borde de luz de 3 tonos.
4. **"containerAlpha debe ser 0.3â€“0.6"** â€” matizado: 0.35 como default
   contradice la auditorÃ­a de contraste del propio usuario (0.80 para
   tarjetas de contenido). Rango vigente: contenido 0.80 / Ã©nfasis 0.35.
5. **"Fondo plano del hazeSource"** â€” diagnÃ³stico correcto pero ya estaba
   resuelto en el ciclo anterior con `habitiaAmbient()` (3 blobs radiales
   de la paleta activa; su gradiente vertical es la versiÃ³n simple).

## Archivos tocados en esta adopciÃ³n

| Archivo | Cambio |
|---|---|
| `Core/theme/Glass.kt` | `habitiaGlass` + params `tint`/`elevation` (defaults a tokens) |
| `Core/components/HabitiaGlassCard.kt` | + `tint`/`elevation`/`contentPadding(0)` + reglas de uso en KDoc |
| `Presentation/home/HomeScreen.kt` | HeroCard de escaparate: vidrio primario translÃºcido (0.35) + elevaciÃ³n 8dp |

**LecciÃ³n tÃ©cnica n.Âº 4 del dÃ­a** (misma raÃ­z que las anteriores): en el
HeroCard se habÃ­a aplicado `tint = primary, alpha = 0.35` (idea de "Ã©nfasis
por color del vidrio" de la auditorÃ­a adoptada parcialmente). Resultado
visual en el emulador: interior teÃ±ido vs. contorno blanco (borde+especular)
â†’ el ojo leÃ­a "bloque plano dentro del vidrio" (disonancia cromÃ¡tica). **Fix:
vidriado unificado** â€” retirados los overrides `tint`/`containerAlpha` del
Hero; comparte el vidrio surface del resto. La jerarquÃ­a del Hero la dan
altura(300dp) + sombra(8dp) + badge de gradiente. Regla aprendida: en un
entorno glass, TODAS las superficies de una pantalla comparten la misma
"tensiÃ³n de vidrio"; la jerarquÃ­a se expresa por dimensiÃ³n/elevaciÃ³n, no por
tintes dispares.

---

# AUDITORÃA SISTEMÃTICA LIQUID GLASS â€” veredictos con evidencia (P0â†’P3)

Fecha: 13 de octubre de 2026
Estado verificado contra el cÃ³digo real (todos los archivos del P0â€“F
leÃ­dos: Glass.kt, HabitiaGlassCard.kt, Theme.kt, Color.kt, App.kt, Home,
Food, Tasks, Planner, Reminders, Focus, More, UserMenu, auth, splash).

## P0 â€” ComposiciÃ³n de las capas Glass (HabitiaGlassCard / habitiaGlass)

| HipÃ³tesis | Veredicto |
|---|---|
| 1. Segundo fondo dentro de la tarjeta | âŒ DESCARTADO â€” ningÃºn hijo pinta background opaco (los chips/segmentos tonales del Hero/QuickToggle son intencionales en la capa de contenido) |
| 2. Relleno y borde distintos colores | âš ï¸ POR DISEÃ‘O â€” relleno = tinte surface; borde = gradiente blanco. El contorno DEBE ser mÃ¡s brillante: si lo igualas, el glass desaparece. La percepciÃ³n de "dos tonos" es el punto de la receta |
| 3. Opacidad en mÃ¡s de una capa | âŒ No hay doble alpha sobre el mismo color (tinte = una sola capa; especular/borde son capas separadas por intenciÃ³n) |
| 4. Orden de modificadores | âœ… VERIFICADO correcto: shadow â†’ clip â†’ clickable â†’ hazeEffect â†’ tinte â†’ especular â†’ borde |
| 5. Shape distinto clip/border | âŒ Mismo `shape` en toda la cadena |
| 6. Defaults distintos entre cards | âš ï¸ CORREGIDO â€” existÃ­a (Hero con tint/alpha 0.35); ya unificado. Solo elevation intencional (8dp vs 6dp) |
| 7. Padding fuera de un contenedor | âŒ Los paddings son internos (contentPadding + layout propio) |
| 8. Sombra duplicada | âŒ Una sola sombra por card (HabitiaGlassCard no usa Material Card â€” por eso no hay doble elevaciÃ³n) |

## P1 â€” Tema/paleta/fondos

- `background` (#F4F1F8) vs `surface` (#FFFFFF) distintos: POR DISEÃ‘O, y ahora
  el fondo lleva blobs ambientales (`habitiaAmbient()`).
- Paleta dinÃ¡mica Android: NO se usa.
- Claro/oscuro detectado por luminancia â†’ tokens adaptados automÃ¡ticamente.
- NingÃºn color hex hardcodeado fuera de Color.kt (tokens nombrados).

## P2 â€” Contenedores

- NingÃºn `.background()` extra post-glass en ninguna card real (revisado).
- Sin contenedores anidados Surface/Card con containerColor propio.
- Las tarjetas con funciones distintas (TipCard tonal, RitualCard con su
  emoji circular, QuickToggle con SÃ­/No tonal) NO son glass por diseÃ±o â€”
  la auditorÃ­a ya advierte que no deben forzarse a compartir relleno.

## P3 â€” Haze y render

- hazeSource en la raÃ­z de cada pantalla (incluye captura del `habitiaAmbient`
  pintado en la misma raÃ­z), estado LOCAL por pantalla, mismo objeto para
  fuente y efectos â†’ coherente.
- Versiones verificadas contra JARs (Haze 1.2.2, datetime 0.6.1).
- Variable pendiente: la GPU/renderizaciÃ³n del emulador API 31+ (no la
  cÃ³digo). Para validar diferencial: la herramienta de testeo A/B/C.

## Herramienta creada: GlassDiagnosticScreen

`shared/src/commonMain/kotlin/upeu/edu/pe/Presentation/dev/GlassDiagnosticScreen.kt`
(+ preview en `shared/src/androidMain/.../dev/GlassDiagnosticPreview.kt`).

Tres tarjetas idÃ©nticas: A) receta completa con blur, B) receta completa
sin blur, C) solo tinte plano sin especular/borde. Abras el Preview en
Android Studio (sin APK); la comparaciÃ³n Preview vs emulador real en el
mismo dispositivo distingue si algÃºn dÃ­a hiciera falta separar FALLO de
Haze de fallo de color. Ver KDoc para las reglas de interpretaciÃ³n exactas y
cÃ³mo montarla temporalmente en navegaciÃ³n si lo necesitas.

---

# AUDITORÃA #3 â€” suavizado de tokens + veredicto de la "reestructuraciÃ³n de capas"

Fecha: 13 de octubre de 2026

## Aplicado (sÃ­ntesis de las auditorÃ­as #2 y #3, puntos vÃ¡lidos)

1. **Tokens suavizados** (`glassTokens()` claro): containerAlpha 0.80â†’**0.72**,
   stroke 0.55â†’**0.40** / highlight 0.85â†’**0.65**, specular 0.14â†’**0.06**,
   sombra 6dpâ†’**3dp**. Oscuro: specular 0.09â†’**0.05**, sombra 6â†’**4dp**.
   Los tres canales que generaban la percepciÃ³n "centro â‰  contorno"
   (especular, sombra, borde) se atenuaron en conjunto y armonizados.
   NOTA documentada: 0.72 equilibra la peticiÃ³n previa de contraste con la
   nueva de unidad â€” los blobs ambientales aportan la separaciÃ³n que antes
   daba el tinte mÃ¡s opaco.
2. **BotÃ³n "Detalles" del Hero** (aplicado por el usuario): de mini-glass 92%
   anidado â†’ **pÃ­ldora tonal** (primary 12%). Elimina la anidaciÃ³n
   vidrio-sobre-vidrio. Regla refinada: "control casi sÃ³lido" = solo
   navegaciÃ³n global, no botones dentro de tarjetas glass.

## Veredicto sobre la propuesta "un solo Box clip+tint+contenido": NO adoptada

Motivos tÃ©cnicos (evidencia en el propio Compose):

1. **Premisa inexistente**: el diagrama "ANTES" asume hijos pintando fondos
   propios sobre el tinte â€” verificado: ninguna card glass real tiene ese
   fondo. Y el "Bonus" del Checkbox es falso: el Checkbox de Material 3 NO
   pinta relleno de contenedor (no existe tal patch blanco; sus colors()
   solo controlan trazo/check).
2. **Z-order invertido**: con borde en el Box exterior y tinte en el Box
   hijo (matchParentSize), el tinte se dibuja ENCIMA del borde (los hijos
   pintan tras las decoraciones del padre) â†’ el "borde de luz" quedarÃ­a
   apagado bajo el tinte. Nuestra cadena actual (border al final del mismo
   nodo) dibuja el borde SOBRE el tinte â€” correcto para el look glass.
3. **RegresiÃ³n funcional de tamaÃ±o**: matchParentSize NO aporta tamaÃ±o al
   Box padre. Toda card glass dimensionada por su contenido (More, Agenda,
   QuickToggle, MealCard, filas de Tasks/Reminders/Plannerâ€¦) colapsarÃ­a
   al no tener hijo medible. Nuestra arquitectura de un solo nodo ya
   mide el contenido correctamente.
4. **Sin ganancia visual**: mover clip+tinte+especular a una capa hija del
   mismo tamaÃ±o produce pÃ­xeles idÃ©nticos â€” el "tinte como piso del
   contenido" YA es asÃ­ en la cadena Ãºnica (content siempre dibuja sobre
   background del mismo nodo).

ConclusiÃ³n: la clave NO era la topologÃ­a de capas sino la intensidad de los
diferenciadores (tokens) + la eliminaciÃ³n de anidaciones (pill tonal) â€” ya
aplicadas ambas. VerificaciÃ³n: emulador con la app reciÃ©n instalada; ante
cualquier duda residual, usar GlassDiagnosticScreen A/B/C.

---

# AUDITORÃA #4 â€” CAUSA RAÃZ DEFINITIVA (verificada contra el fuente de Haze 1.2.2)

Fecha: 13 de octubre de 2026. MÃ©todo: extracciÃ³n y lectura del JAR de
FUENTES de `dev.chrisbanes.haze:haze:1.2.2` desde el cachÃ© de Gradle
(`HazeEffectNode.kt`, `HazeSourceNode.kt`, `HazeStyle.kt`, `HazeChild.kt`).

## Las 4 auditorÃ­as previas apuntaban a nuestras capas. La causa estaba en Haze.

Evidencia en cadena (fragmentos textuales del fuente):

1. `HazeEffectNode.filter`: `hazeZIndex != null -> area.zIndex < hazeZIndex`
   (exclusiÃ³n ESTRICTA). `hazeZIndex` = `ModifierLocalCurrentHazeZIndex.current`,
   que PROVEE el `hazeSource` ancestro a todo su subÃ¡rbol.
2. `HazeSourceNode`: `area.zIndex = (upstream ?: 0f) + zIndex` â†’ para un
   source raÃ­z: 0f. Sus DESCENDIENTES leen el mismo 0f.
3. Por tanto, todo `hazeEffect` DENTRO del subÃ¡rbol de su propio source:
   `0f < 0f` = falso â†’ Ã¡rea excluida â†’ `areas` vacÃ­o â†’
   `isValid = ... && areas.isNotEmpty()` = falso â†’ `draw()` NO ejecuta
   ninguna ruta de efecto. **Haze pintaba NADA en TODAS las pantallas.**
4. Consecuencia visual: "glass" = tinte 0.72 plano + decoraciones de borde
   â†’ centro plano vs contorno brillante = la "doble tonalidad" persistente.
5. AdemÃ¡s, al corregir lo anterior, la ruta RenderEffect (API 31+) tiene
   `require(bg.isSpecified)` sobre `backgroundColor` â†’ sin HazeStyle explÃ­cito
   habrÃ­a crash. Nuestra integraciÃ³n era doblemente incompleta.

Haze 1.2.x estÃ¡ diseÃ±ado para effects HERMANOS del source (overlays), nunca
descendientes (el propio fuente lo dice: "Modifier.haze nodes can not draw
Modifier.hazeChild nodes").

## SoluciÃ³n arquitectural aplicada (producciÃ³n)

**â‘  `habitiaGlass` â€” capa de vidrÃ­o UNIFICADA** (`Glass.kt`):
- Ruta blur: `hazeEffect(state, HazeStyle(backgroundColor = tinte pleno,
  tints = [HazeTint(tinteTranslÃºcido)], blurRadius = HazeDefaults.blurRadius,
  noiseFactor = HazeDefaults.noiseFactor))`. El tinte viaja DENTRO del
  estilo â†’ Haze compone [base + contenido difuminado + tinte + grano] en un
  SOLO render. Se ELIMINÃ“ el `.background(tintColor)` duplicado de esa ruta
  (era la segunda tonalidad). backgroundColor = OBLIGATORIO (require).
- Ruta estÃ¡tica (sin estado): `.background(tintColor)` como antes.
- Especular y borde de luz se conservan ENCIMA de la capa unificada.

**â‘¡ Pantallas en 2 CAPAS HERMANAS** (8 archivos: Home, Tasks, Planner,
Reminders, Food, More, Focus, GlassDiagnosticScreen):
```
Box(raÃ­z)
 â”œâ”€ Box(FUENTE): fillMaxSize + hazeSource + habitiaAmbient   â† se difumina
 â””â”€ Box(CONTENIDO): fillMaxSize + padding + todo el resto,
     incluidas las cards glass con hazeEffect                  â† hermano âœ“
```
Al ser hermana, el effect lee `ModifierLocalCurrentHazeZIndex = null`
(fuera del subÃ¡rbol del source) â†’ filtro cae en `else -> true` â†’ Ã¡rea
incluida â†’ `isValid` verdadero â†’ **blur real por primera vez**.

## QuÃ© verificar en el emulador (primera vez con blur REAL)

1. Las cards glass deben mostrar el fondo ambiental DIFUMINADO (los blobs
   de la paleta como manchas suaves) + grano fino del noiseFactor â€” ya no
   un panel plano.
2. La "doble tonalidad" desaparece: una sola capa compuesta por vidrio.
3. Scroll en listas: el fondo se difumina a travÃ©s de las cards.
4. API < 31 (si se probara): ruta scrim con el MISMO tinte (HazeTint) â†’
   fallback coherente, sin crash del require (esa ruta no lo exige).
5. GlassDiagnosticScreen: A (blur) vs B (estÃ¡tico) ahora DEBEN diferir.

## ExtensiÃ³n: Login y Register ahora con GLASS REAL

DiagnÃ³stico del usuario: los paneles del auth se veÃ­an "el blanco de toda
la vida" (plano). Causa: usaban la ruta ESTÃTICA del glass â€” nunca recibÃ­an
`hazeState`, asÃ­ que el panel era solo tinte blanco 0.72 + especular, sin
blur ni grano.

Cambio aplicado (mismo patrÃ³n que el resto de pantallas):
- RaÃ­z en 2 capas hermanas: `hazeSource + habitiaAmbient` (fuente) / panel
  + formulario (contenido).
- `HabitiaGlassCard(hazeState = hazeState, ...)` â†’ el panel difumina los
  blobs ambientales de la paleta activa a travÃ©s del vidrio.

Perillas de la "tonalidad glassmorphism" (documentadas para tuning):
- `containerAlpha` del panel (0.72): bajar a 0.55â€“0.6 = mÃ¡s translÃºcido
  (se ve mÃ¡s "vidrio", menos contraste con los campos).
- `blurRadius` del HazeStyle en `Glass.kt` (20dp por defecto): subirlo
  (28â€“32dp) = difuminado mÃ¡s etÃ©reo.
- Alphas de los blobs en `habitiaAmbient()`: subirlos = mÃ¡s color que
  refractar a travÃ©s del panel.

---

# INTEGRACIÃ“N BACKEND REAL â€” HABITIA-Back (Spring Boot + JWT) [2026-11-04]

## QuÃ© se conectÃ³ (primer corte: AUTH + TASKS)

El frontend Kotlin ahora consume el backend REAL del curso
`C:\ProyectoAppMoviles\HABITIA-Back` (Spring Boot 4.1.1 + MySQL + JWT,
puerto `8081`, context-path `/habitia-backend`), bajo la ESTRUCTURA del
booking-app del profesor (ApiClient Ktor + DTOs + mapper + repos API).

### Contrato del backend (leÃ­do de su cÃ³digo fuente, no inventado)
- Rutas base: BASE_URL = `http://10.0.2.2:8081/habitia-backend` (AVD estÃ¡ndar)
  o `http://192.168.56.1:8081/habitia-backend` (Genymotion). El sufijo es el
  `server.servlet.context-path`; el puerto es `server.port`.
- Seguridad: TODO requiere `Authorization: Bearer <JWT>` salvo `/auth/**`
  (SecurityConfig: requestMatchers("/auth/**").permitAll()).
- `POST /auth/login {email,password}` â†’ `{token, tokenType}` (NO devuelve userId).
- `GET /Users/email/{email}` (con Bearer) â†’ `{id,name,email,birthDate}` â†’
  ese `id` es el userId que exige `GET /Tasks/user/{userId}`.
- `GET /Tasks/user/{userId}` â†’ lista TaskResponseDto.
- `POST /Tasks` {title, description, dueDate?, priority} â†’ el backend resuelve
  el dueÃ±o DESDE EL JWT (TaskServiceImpl.crear usa userRepository.findByEmail
  del token, no del body) y arranca PENDING.
- `PUT /Tasks/{id}` {title, description, dueDate?, priority, status} â†’
  TaskUpdateDto completo (status: PENDING|IN_PROGRESS|COMPLETED).
- `POST /auth/register {name,email,password,birthDate}` â†’ 201 + UserResponseDto.
  birthDate es @NotNull â†’ se agregÃ³ campo "Fecha de Nacimiento" (AAAA-MM-DD)
  al RegisterScreen con validaciÃ³n en AuthViewModel.

### Archivos NUEVOS (patrÃ³n booking-app)
- `Data/remote/api/ApiClient.kt` â€” HttpClient Ktor Ãºnico (ContentNegotiation
  JSON + Logging; BASE_URL con las 3 variantes documentadas).
- `Data/remote/auth/SessionManager.kt` â€” token + email + userId en
  StateFlow (el booking-app no lo necesitaba; aquÃ­ el JWT es obligatorio y
  el userId se resuelve post-login).
- `Data/remote/dto/AuthDto.kt` + `Data/remote/dto/TaskDto.kt` â€” espejo 1:1
  de los DTOs de Java (formatos camelCase que emite Jackson).
- `Data/remote/mapper/TaskMapper.kt` â€” dominio â†” DTO: id Stringâ†”Long,
  completedâ†”status COMPLETED/PENDING, dueDate ISO "yyyy-MM-dd".
- `Domain/repository/AuthRepository.kt` (interfaz nueva, estilo Clean).
- `Data/repository/ApiAuthRepositoryImpl.kt` â€” login (2 llamadas encadenadas),
  register + login automÃ¡tico, logout; errores HTTPâ†’mensajes amigables.
- `Data/repository/ApiTaskRepositoryImpl.kt` â€” implementa TaskRepository
  EXISTENTE: cache MutableStateFlow como el mock; refresh()/PUT/POST con
  bearerAuth; campos SOLO LOCALES (category/dueTime/recurring) se conservan
  en memoria porque el backend no los modela.
- `Presentation/auth/AuthViewModel.kt` â€” MVVM con validaciÃ³n cliente
  (correo, contraseÃ±a â‰¥8, fecha ISO) y estado isLoading/error/isSuccess.

### Archivos MODIFICADOS
- `gradle/libs.versions.toml` + `shared/build.gradle.kts` â€” Ktor 2.3.12
  (core+content-negotiation+json+logging en commonMain, android en Android,
  darwin en iOS â€” misma versiÃ³n que el booking-app).
- `App.kt` â€” wiring real: session/authViewModel/taskRepo API; LaunchedEffect
  observando la sesiÃ³n dispara taskRepo.refresh() al quedar lista; logout
  llama session.clear().
- `LoginScreen.kt` / `RegisterScreen.kt` â€” firma con authViewModel, spinner
  y errores del backend renderizados; botÃ³n deshabilitado durante la red.
- `androidApp/src/main/AndroidManifest.xml` â€” INTERNET +
  usesCleartextTraffic="true" (necesario mientras el backend sea http:// local).

### CÃ³mo probar END-TO-END (checklist)
1. MySQL corriendo con BD `habitia` (ddl-auto=update crea tablas).
2. Backend: `cd C:\ProyectoAppMoviles\HABITIA-Back && .\mvnw.cmd spring-boot:run`
   â€” REQUIERE la variable de entorno `HABITIA_JWT_SECRET` (base64 â‰¥32 bytes):
   `$env:HABITIA_JWT_SECRET="<clave-en-base64>"` antes de arrancar.
   Smoke test: `curl http://localhost:8081/habitia-backend/auth/login` debe
   responder 405/400 (NO 404).
3. App en AVD (Pixel_9): BASE_URL activa 10.0.2.2 âœ“; en Genymotion editar
   ApiClient.kt â†’ 192.168.56.1.
4. Registrar cuenta (nombre, correo, contraseÃ±a â‰¥8, fecha AAAA-MM-DD) â†’
   entra directo a Home â†’ agregar tarea en Tasks â†’ sale del backend (logs
   del Spring visibles en consola).

### Limitaciones documentadas (siguientes pasos)
- SesiÃ³n SOLO en memoria (Recomendable: persistir token con DataStore, ya en el
  proyecto) y el JWT del profe expira a los 15 MINUTOS (expiration-ms 900000)
  â†’ tras ese tiempo el refresh loguea el 401 y hay que re-loguear (pendiente
  auto-refresh o peticiÃ³n al profe de subir expiration-ms).
- category/dueTime/recurring no existen en el backend: se preservan solo en
  RAM durante la sesiÃ³n (al refrescar, tareas nuevas remotas caen "Hogar").
- getTodayTasks() sigue filtrando en UI (la API no filtra por dÃ­a).

---

# FIX VISIBILIDAD DE FABs — navegación glass los tapaba [2026-11-04]

## Síntoma del usuario
"No hay para agregar tareas" en el módulo de Tasks — el FAB existía pero
no se veía.

## Causa raíz
Los FABs de 4 pantallas (Tasks, Food, Planner, Reminders) usaban
`padding(bottom = 24.dp)`, pero la barra de navegación solid glass ocupa
~92dp del fondo (NavigationBar M3 = 80dp + 12dp de margen en App.kt) y su
vidrio es 95% opaco → el FAB quedaba COMPLETAMENTE cubierto, sin pista
visual de que existía debajo.

## Fix aplicado
`bottom = 24.dp → 104.dp` en el Box del FAB de las 4 pantallas:
- TaskScreen (Nueva Tarea)
- FoodScreen (Añadir Comida / Añadir Ingrediente)
- PlannerScreen (Nuevo Evento)
- RemindersScreen (Nuevo Recordatorio)
Fórmula: 104 = 80 (nav) + 12 (margen) + 12 (aire). El FAB flota sobre la
nav con buen ritmo visual.

## Lección de diseño
Cada vez que un elemento flotante conviva con la nav glass, su inset inferior
mínimo es 92dp (nav + margen). Considerar un constante compartido
(ie. NavigationInsets.glassNav) si aparecen más elementos de este tipo.

---

# DATOS REALES DEL USUARIO EN EL FRONT [2026-11-04]

## Qué pedía el usuario
Que el usuario "se guarde con los datos que se registra en el front" — hasta
ahora el nombre que alimentaba el saludo de Home y el campo Nombre del perfil
era un HARDCODE ("Mauricio Lazarte" en UserMenuState.kt:11).

## Flujo implementado (ya existía al 90%, se cerró el ciclo)
1. POST /auth/login → token.
2. GET /Users/email/{email} (Bearer) → {id, name, email, birthDate} ← AQUÍ
   VIVEN los datos de registro del backend.
3. SessionManager AHORA guarda el perfil completo:
   SessionData{token, email, userId, name, birthDate}
   - onUserResolved(id, name, birthDate) — firma ampliada.
   - logout → clear() borra TODO (nombre incluído).
4. App.kt: SessionManager movido ARRIBA del tema; UserMenuViewModel se
   construye con remember(sessionState.name) — la llave hace que el VM se
   recree en el momento del login con el nombre real, transformando:
   - Saludo de HomeScreen (personalizado por registro ✓)
   - Campo "Nombre" del perfil (precargado con el registro, sigue editable)
5. Fallback: sin sesión el nombre es "Usuario" (antes era el nombre del dev,
   con riesgo de confusión para terceros).

## Notas
- El nombre sigue editable en el perfil (editableName en UserMenuScreen) —
  es un ajuste LOCAL de la sesión; el backend no lo persiste aún (PUT
  /Users/{id} existe en el backend para futura sincronización).
- birthDate queda disponible en SessionData para futuras pantallas
  (cumpleaños, personalización de edad).

---

# CORREO REAL EN CONJUNTO CON EL NOMBRE [2026-11-04, 2do corte]

## Qué pedía el usuario
"actualiza el correo en conjunto" — el perfil de UserMenuScreen mostraba un
CORREO HARDCODE ("mauricio.lazarte@upeu.edu.pe" en UserMenuState:12) igual
como pasaba con el nombre. Ahora nombre Y correo llegan JUNTOS del backend.

## Cambios
- UserMenuState: userEmail por defecto "" (sin hardcode del dev).
- UserMenuViewModel: segundo parámetro realUserEmail; el estado se arma con
  nombre+correo reales.
- App.kt: remember KEYS ahora (sessionState.name, sessionState.email) — el
  VM se recrea cuando el login trae el perfil completo.
- UserMenuScreen no cambió: editableEmail ya leía state.userEmail → ahora
  muestra el correo REAL con el que se registró el usuario.

## Cadena completa (lo que el usuario registra == lo que ve en el front)
register(name,email,...) → login → GET /Users/email → SessionData(token,
userId, name, birthDate, email) → perfil (Nombre + Correo) y saludo Home.

## Falta documentado (siguientes pasos)
- Editar nombre/correo NO persiste al backend (PUT /Users/{id} disponible).
- editablePhone es local ("999999999") — el backend no modela teléfono.

---

# CORREO REAL EN EL DRAWER DE HOME ("tab bar" que se despliega) [2026-11-04]

## Qué pedía el usuario
Que el dato también cambie en el panel desplegable (ModalNavigationDrawer
lateral de Home, 310dp). El correo ahí era OTRO hardcode:
'mauricio.lazarte@upeu.edu.pe' fijo en HomeScreen line ~114.

## Cambios
- HomeScreen: nuevo parámetro userEmail (default "") y el Text del drawer
  lo muestra (ifBlank { "Sin correo" }). Nombre default de 46 ahora "Usuario".
- App.kt: HomeScreen recibe userName + userEmail desde userMenuState
  (que viene de SessionManager con los datos reales del login).

## Cobertura total del "usuario registrado"
Saludo Home ✓ · Perfil (Nombre+Correo) ✓ · Drawer lateral (Nombre+Correo) ✓

---

# BOTÓN "CERRAR SESIÓN" EN EL PERFIL [2026-11-04]

## Qué pedía el usuario
Un botón de cerrar sesión "en el usuario". Ya existía en el drawer de Home
(último item, en rojo) pero la pantalla de Perfil (UserMenuScreen) NO lo tenía.

## Cambios
- UserMenuScreen: nuevo parámetro onLogout + botón "Cerrar Sesión"
  (OutlinedButton rojo, icono Logout) al final del perfil, DESPUÉS de
  "Guardar Cambios", con AlertDialog de confirmación ("¿Cerrar sesión?")
  para que un toque accidental no deslogee.
- App.kt: al confirmar → session.clear() (borra token/email/userId/nombre/
  correo/birthDate) + navegación a Login.

## Dónde puede cerrar sesión el usuario hoy
1. Drawer lateral de Home (ya existía): último item "Cerrar Sesión".
2. Pantalla Perfil → botón "Cerrar Sesión" (NUEVO, con confirmación).

---

# INTEGRACIГ“N TOTAL AL BACKEND вЂ” SIN HARD CODE [2026-11-16]

## QuГ© pidiГі el usuario
"Terminar el proyecto y todo enlazado a backend con nada hardcodeado",
con anГЎlisis completo de lo que faltaba.

## AnГЎlisis previo (estado antes de este corte)
- YA conectados: Auth (login/register/logout), Tasks.
- Mocks activos en App.kt: Habits, Reminders, Meals.
- SIN repositorio (estado interno del ViewModel): Planner (CalendarEvents,
  4 eventos "2026-09-07" en memoria) y Compras (5 items hardcodeados en
  FoodViewModel).
- Hardcodes de fecha: TaskViewModel.addNewTask, RemindersViewModel
  (addReminder), PlannerViewModel (eventos + addEvent) в†’ todos "2026-09-07".

## Decisiones de diseГ±o (mismatches dominioв†”backend)
1. HABITS: el backend no modela streak ni "completado hoy" в†’ se DERIVAN de
   /HabitCompletions: refresh() baja el hГЎbito + sus completions y calcula
   streak (dГ­as consecutivos hasta hoy, con tolerancia si hoy aГєn no estГЎ
   marcado). completeHabit = GET completion del dГ­a в†’ PUT si existe,
   POST si no. El id de dominio es "habit-<n>".
2. REMINDERS: el backend guarda UN LocalDateTime (reminderDate); el dominio
   lo parte en date+time (ReminderMapper.splitDateTime/joinDateTime).
   repeat (NONE/DAILY/WEEKLY) в†” recurring Boolean. type (NORMAL/
   MEDICATION/RECURRING) es SOLO LOCAL (backend no lo tiene).
   OJO: este mГіdulo SГЌ exige userId en el POST (a diferencia de Tasks/
   Habits que lo resuelven del JWT) в†’ se toma de SessionManager.
3. MEALS: MealCreateDto SГЌ exige userId (SessionManager). ingredients es
   SOLO LOCAL (backend los tiene en /MealIngredients; sincronizarlos agrega
   N llamadas por comida вЂ” mejora futura). completed del backend в‰Ў siempre
   false (la UI de Meals no lo usa).
4. CALENDAR EVENTS: el backend devuelve la ENTIDAD cruda (con user anidado
   вЂ” lo ignora ignoreUnknownKeys del Json) y el POST NO resuelve el dueГ±o
   del JWT в†’ se manda user:{id} en el body (descubierto leyendo el
   controller; sin eso el save explota con user_id NOT NULL).
5. SHOPPING: mismatches grandes, mapeados:
   - ShoppingItemCreateDto del backend = SOLO description (в‰¤255).
   - Update = description + completed (cantidad no editable por API).
   - La lista vive en ShoppingLists/{listId}; si el usuario no tiene,
     POST /ShoppingLists/generate la crea (exige userId+startDate+endDate:
     hoy в†’ hoy+7).
   - quantity BigDecimal+unit вЂ” String del dominio; category SOLO LOCAL.
6. FOCUS: el backend del profe NO tiene mГіdulo de sesiones de foco
   (FocusSession/CompletionSessions no existen) в†’ queda LOCAL (timer en
   memoria). Documentado: pedir al profe o qued asГ­.

## Archivos nuevos
- Data/remote/dto: HabitDto.kt, ReminderDto.kt, MealDto.kt,
  CalendarShoppingDto.kt (Calendar + Shopping + GenerateShoppingList).
- Data/remote/mapper: HabitMapper.kt (streak), ReminderMapper.kt,
  MealMapper.kt, CalendarShoppingMapper.kt.
- Data/repository: ApiHabitRepositoryImpl, ApiReminderRepositoryImpl,
  ApiMealRepositoryImpl, ApiShoppingRepositoryImpl,
  ApiCalendarEventRepositoryImpl.
- Domain/repository: CalendarEventRepository.kt, ShoppingRepository.kt
  (interfaz nueva; FoodViewModel ahora la inyecta).

## Archivos editados
- App.kt: los 3 mocks reemplazados por Api impls + 2 repos nuevos;
  LaunchedEffect de sesiГіn refresca LOS 6 mГіdulos (cada repo protege con
  try/catch: un mГіdulo caГ­do no rompe a los demГЎs); PlannerViewModel
  recibe su repo; FoodViewModel recibe (mealRepo, shoppingRepo).
- PlannerViewModel: reescrito con repositorio (la UI no cambiГі); los
  eventos nuevos usan HOY (kotlinx-datetime).
- FoodViewModel: compras vГ­a ShoppingRepository (adiГіs lista mock interna
  de 5 items); addMeal ya no inventa ids con Random вЂ” el id real lo asigna
  el backend.
- TaskViewModel.addNewTask: dueDate "2026-09-07" в†’ HOY.
- RemindersViewModel.addReminder: date "2026-09-07" в†’ HOY.
- HabitMapper: kotlinx-datetime (prohibido java.time en commonMain; los
  calendarios de streak usan minus(1, DateTimeUnit.DAY)).

## Errores del camino (lecciones)
- LocalDate.now()/minusDays() son de java.time: en commonMain se usa
  Clock.System.todayIn(TimeZone.currentSystemDefault()) y
  LocalDate.minus(1, DateTimeUnit.DAY).
- Las extensiones de kotlinx.datetime (todayIn, plus) exigen IMPORT
  explГ­cito aunque escribas el nombre completo del paquete.
- El parГ©ntesis extra en el mapper de Reminder (return con `))`)
  rompГ­a el parseo del archivo.

## Lo que sigue hardcodeado / pendiente (declarado)
- EditablePhone del perfil: "999999999" вЂ” el backend no modela telГ©fono;
  campo local de la UI.
- Ingredients de Meals: locales por sesiГіn (ver decisiГіn 3).
- Category de Shopping: local por sesiГіn (ver decisiГіn 5).
- Focus: mГіdulo local (backend no lo tiene).
- JWT expira a los 15 min: tras eso los refresh dan 401 в†’ re-login
  (pendiente: refresh token o subir expiration-ms con el profe).
- Persistencia de sesiГіn entre arranques de la app: NO implementada
  (SessionManager vive en memoria) вЂ” decisiГіn pendiente del usuario.

---

## INTEGRACIÓN TOTAL + UI REDISEÑO [2026-11-16 — corte 2]

Corte de ESTA sesión (sobre la integración completa al backend del corte
anterior "INTEGRACIÓN TOTAL AL BACKEND — SIN HARD CODE"):

### 1. PERFIL REDISEÑADO — cada cosa en su apartado (US-03 del plan Scrum)
- `UserMenuScreen` → SOLO datos del usuario: foto (peekaboo), **nombre**
  y **fecha de nacimiento** editables, **correo SOLO LECTURA** (el backend
  lo usa como identificador: es el subject del JWT y `UserUpdateDto` no lo
  permite cambiar). Se ELIMINÓ el campo teléfono "999999999" hardcodeado.
- **Guardar Cambios → PUT /Users/{id} REAL** (`UserUpdateRequestDto` =
  {name, birthDate}; validación en cliente: nombre no vacío, fecha ISO
  AAAA-MM-DD y no futura). Tras el 2xx se replica en `SessionManager` con
  el NUEVO método `updateProfile(name, birthDate)` → el saludo de Home se
  actualiza al instante (el VM se re-crea por sus keys).
- **NUEVA `AppearanceScreen`** (ruta `HabitiaScreen.Appearance`): toda la
  PERSONALIZACIÓN se mudó aquí — generador Coolors, paletas curadas, mis
  paletas, creador HEX (`CustomPaletteCreator`), selector de fondo
  (`BackgroundSelector`), tipografías.
- **MODO OSCURO ✅**: interruptor Switch en AppearanceScreen conectado a
  `onDarkModeToggled` (ya existía en el VM y persistía vía
  UpdateThemeUseCase — lo que faltaba era la UI). Los tokens del vidrio
  cambian a su variante oscura.
- Acceso: Perfil → "Personalizar Apariencia", y MoreScreen → va DIRECTO a
  AppearanceScreen (antes pasaba por Perfil).
- `UserMenuState` gana campo `birthDate` (equals/hashCode manuales
  actualizados).

### 2. MÉTODO GLASS CONSISTENTE — nuevo `GlassTextField`
- **NUEVO `Core/components/GlassTextField.kt`**: campo de texto con el
  MISMO método glass del sistema (`habitiaGlass` estático: tinte
  translúcido + borde de luz + brillo especular). Reemplaza a los
  `OutlinedTextField` OPACOS que rompían la estética (el caso reportado:
  editar perfil). `readOnly=true` para campos de solo lectura.
- Auditoría: todas las *Screen usan habitiaGlass/HabitiaGlassCard
  (verificado con grep); la brecha eran los CAMPOS de texto del perfil.

### 3. CALENDARIO COMPLETO estilo Google Calendar + POSITS (US-05)
- **PlannerScreen REESCRITA**: cabecera `< MES AÑO >` navegable, GRID
  7x6 (semana empieza LUNES) con PUNTOS de color por día estilo Google
  (evento=primary, tarea=tertiary, google=secondary), hoy y día
  seleccionado resaltados.
- **POSITS (sticky notes) interactivos** en el panel del día:
  - Posit EVENTO (azul): tap → Eliminar / Mover a otro día (PUT
    `/CalendarEvents/{id}` con startDate/endDate nuevos, manteniendo
    duración; el user{id} viaja en el body — el backend no resuelve JWT
    en este módulo).
  - Posit TAREA (ámbar): tap → Completar/Desmarcar, Mover, Eliminar.
  - Posit GOOGLE (violeta): SOLO LECTURA.
- **Tareas dentro del calendario**: PlannerViewModel ahora recibe el
  `TaskRepository`; los posits de tarea usan `dueDate`. NUEVOS métodos del
  `TaskRepository`: `updateTask(task)` (PUT `/Tasks/{id}` con
  TaskUpdateDto COMPLETO — el backend lo exige) y `deleteTask(taskId)`
  (DELETE `/Tasks/{id}`), con DEFAULT vacío para que los mocks legacy no
  se rompan.
- **Google Calendar conectado con la API key del usuario**:
  - `ApiClient.GOOGLE_CALENDAR_API_KEY` (⚠️ la key quedó expuesta en el
    chat — RESTRINGIRLA en Google Cloud Console o regenerarla).
  - `Data/remote/google/GoogleCalendarClient.kt`: GET
    `calendar/v3/calendars/{id}/events?key=...&timeMin&timeMax&singleEvents`
    con DTOs propios (`GoogleEventDto/GoogleTimeDto/GoogleEventsResponseDto`)
    y `parseGoogleDate()` top-level (dateTime → fecha+hora local; all-day
    → 00:00).
  - Con API key SOLO se leen calendarios PÚBLICOS (compartidos "ver
    público" o "primary" público). El calendario PRIVADO exige OAuth2 →
    queda documentado como mejora futura (GoogleCalendarLinkScreen).
- **Agregar evento** desde el FAB con fecha = DÍA SELECCIONADO (antes
  siempre HOY).
- "Mover" un posit abre diálogo con los próximos 7 días (el arrastre real
  cross-cell en Compose queda como mejora — long-press global).

### 4. MATRIZ EISENHOWER + PIPELINE SCRUM en Tareas (US-04)
- TaskScreen ahora tiene selector de vista: **[Lista | Matriz | Sprint]**.
- **NUEVO `Presentation/tasks/EisenhowerScreen.kt`**:
  - `EisenhowerMatrix`: cuadrantes Q1 Haz ya / Q2 Planifica / Q3 Delega /
    Q4 Elimina + fila "Done". Clasificación SOLO con datos reales:
    urgente = dueDate ≤ mañana (calculateUrgency), importante =
    priority HIGH/MEDIUM (reorderByPriority). Tap en una tarea =
    completar/desmarcar (persistido en backend).
  - `SprintBoard`: el backlog REAL del equipo (7 EPICs → Features →
    PBIs del plan de sprints, condensado) como tablero con barra de
    progreso por EPIC y toggle Done por PBI — LOCAL (el backend del
    profe no modela sprints/epics; documentado).
- `TasksViewModel.allTasks`: StateFlow de TODAS las tareas (sin filtro de
  categoría) para alimentar la matriz.
- `TaskRepository.updateTask/deleteTask` agregados (ver punto 1).

### 5. CONSEJOS DEL DÍA ROTATIVOS (ya no son siempre los mismos)
- HomeScreen: la lista de consejos pasó de 3 fijos a **15 consejos**, y
  `tipsForToday()` rota por **día del año** (kotlinx-datetime,
  `dayOfYear`): cada día muestra 3 consejos distintos y el ciclo se
  repite al año siguiente. Sin hardcode de fecha.

### Archivos nuevos
`Core/components/GlassTextField.kt` · `Data/remote/dto/UserDto.kt` ·
`Domain/repository/UserRepository.kt` ·
`Data/repository/ApiUserRepositoryImpl.kt` ·
`Presentation/UserMenu/AppearanceScreen.kt` ·
`Data/remote/google/GoogleCalendarClient.kt` ·
`Presentation/tasks/EisenhowerScreen.kt`

### Archivos editados
`SessionManager.kt` (+updateProfile) · `UserMenuViewModel.kt` (+perfil
real: profileSaving/profileMessage/updateUserProfile, birthDate de
sesión) · `UserMenuScreen.kt` (reescrito: solo datos + PUT) ·
`UserMenuState.kt` (+birthDate) · `HabitiaScreen.kt` (+Appearance) ·
`App.kt` (rutas Appearance, userApi+session al VM del perfil, Google +
taskRepo al PlannerViewModel) · `HomeScreen.kt` (tips rotativos) ·
`TaskRepository.kt` (+updateTask/deleteTask con default) ·
`ApiTaskRepositoryImpl.kt` (PUT/DELETE /Tasks) ·
`TaskViewModel.kt` (+allTasks) · `TaskScreen.kt` (selector de vistas) ·
`PlannerViewModel.kt` (reescrito: meses, posits, Google) ·
`PlannerScreen.kt` (reescrita: calendario completo) ·
`CalendarEventRepository.kt` + `ApiCalendarEventRepositoryImpl.kt`
(+updateEvent) · `ApiClient.kt` (+GOOGLE_CALENDAR_API_KEY)

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` →
  BUILD SUCCESSFUL.
- APK instalado en emulador Pixel_9 (`install -r -t`), arranque SIN
  crashes ni errores Habitia-API en logcat.

### Deudas declaradas (no regresiones)
- El calendario PRIVADO de Google requiere OAuth2 (solo API key lee
  públicos). El drag&drop real de posits y la persistencia del backlog
  Scrum quedan como mejoras (local/memoria).
- Persistencia de sesión entre arranques (DataStore) sigue pendiente;
  JWT expira a los 15 min (401 → re-login).

---

## CORTE 3 — GLASS CONSISTENTE + BUGS UX + DRAG&DROP + RITUALES [2026-11-16]

### 1. GLASS CONSISTENTE EN LAS 4 PANTALLAS NUEVAS (mismo método de los recuadros)
Las 4 pantallas reescritas en el corte anterior perdieron la capa fuente
hermana (hazeSource + habitiaAmbient) del fix de auditoría #4 → sus
recuadros quedaban sin blur real y con doble tonalidad. Se restauró el
patrón de 2 capas hermanas (Box fuente detrás + Box contenido delante) en:
- PlannerScreen, AppearanceScreen, UserMenuScreen, EisenhowerScreen.
- GlassTextField ahora acepta `hazeState` (vidrio real, no solo tinte).
- HabitiaGlassCard en Appearance recibe hazeState.
- Scaffold de Appearance/UserMenu cambió containerColor a Transparent para
  no tapar la capa fuente.

### 2. BUGS UX CORREGIDOS
- **FoodScreen FAB "+" en menú semanal**: `openMealEditor(null)` asignaba
  `editingMeal = null` → el diálogo (`editingMeal?.let`) NUNCA aparecía.
  Fix: crea un Meal vacío con id="" (modo CREAR). Ahora el botón abre
  el editor correctamente.
- **Alertas que no se creaban**: `RemindersViewModel.addReminder` normaliza
  la hora "9:00" → "09:00" (el backend fallaba el parseo de
  LocalDateTime con "T9:00" → 400). Fallback 09:00 si la entrada es inválida.
- **Focus: botones Iniciar/Pausar/Reiniciar invisibles**: la Row de
  botones tenía padding(bottom=32dp) y quedaba cubierta por el nav bar
  glass (80+12dp). Subido a 104dp (mismo criterio que los FAB).
- **Navegación "Personalizar temas → Perfil"**: AppearanceScreen.onBack
  siempre iba a Profile aunque se entrara desde More. Fix: variable
  `appearanceReturnScreen` (Profile o More) fijada al navegar.

### 3. MATRIZ EISENHOWER + TABLERO KANBAN CON DRAG & DROP
- EisenhowerScreen reescrito: arrastre real (long-press) entre cuadrantes con
  overlay flotante (no se recorta) + hit-test por boundsInRoot. Al soltar en
  otro cuadrante se actualiza priority+dueDate en el backend (PUT /Tasks)
  y la matriz se re-clasifica al instante.
- SprintBoard: tablero Kanban con columnas LOCALES agregables por el
  usuario ("+ Columna"), drag&drop de tareas entre columnas, sync de
  completed con el backend (Hecho = completed true).
- ELIMINADO el backlog local del proyecto (EPICs/Features/PBIs) — ahora
  el tablero es del USUARIO (sus tareas reales).
- TasksViewModel: +completeTask (reintegrado), +updateTask, +moveTaskInMatrix.

### 4. RITUALES DIARIOS (apartado nuevo en Home)
- Sección "Tus rituales diarios" ahora incluye:
  - "Crear ritual": diálogo con nombre → ritual local (documentado: sync
    con HabitRepository queda como mejora).
  - "Guía paso a paso": asistente secuencial por cada ritual (Hecho ✓
    usa toggleHabit real del backend / Saltar / Salir). Si no hay rituales,
    ofrece crear el primero.
  - Se abre automáticamente al presionar "No" en el diálogo inicial.

### 5. AUDITORÍA EXPERTA UX/UI (qué más falta — reporte)
- Headers y FAB ya estandarizados a 104dp en Tasks/Food/Focus/Planner.
- Pendientes declarados para próximos cortes:
  1. Persistencia de sesión entre arranques (DataStore) — JWT expira 15 min.
  2. Calendario PRIVADO de Google requiere OAuth2 (solo API key lee públicos).
  3. Drag&drop real cross-cell del calendario (hoy: mover por chips de días).
  4. Persistencia de columnas Kanban y rituales creados (hoy: en memoria).
  5. Backend sin módulos: Household, Goals/Projects, Focus (local), Summary,
     Analytics, Health, IA — EPICs 3.1, 4.1, 4.2, 5.x, 6, 7.
  6. Seguridad: @JsonIgnore en User.password (GET /Users y /CalendarEvents
     serializan el hash).
  7. Ingredientes de Meals y categoría de Shopping solo locales (mejora:
     sincronizar con /MealIngredients y /ShoppingItems).
  8. Teléfono del perfil no modelado en backend.
  9. Focus: registrar sesiones en backend (no existe módulo).

### Verificación
- :shared:compileDebugKotlinAndroid y :androidApp:assembleDebug → BUILD SUCCESSFUL.
- APK instalado en Pixel_9, arranque SIN crashes ni errores Habitia-API.

---

## CORTE 4 — SOPORTE A TELÉFONO FÍSICO [2026-11-17]

### Diagnóstico
Al instalar el APK en un teléfono real (Honor, Android) la app arrancaba pero
NO conectaba al backend:
- `BASE_URL = "http://10.0.2.2:8081/habitia-backend"` es un alias EXCLUSIVO
  del emulador AVD (10.0.2.2 → localhost del host). En un teléfono real
  esa IP no existe → login fallaba ("Bad credentials"/timeout) y todos los
  módulos quedaban vacíos.
- El manifest ya tenía `android:usesCleartextTraffic="true"` y el permiso
  INTERNET → no era un bloqueo de cleartext.
- La PC es accesible desde el teléfono por la LAN (192.168.100.210) y el
  backend responde (403 en la raíz = esperado, hay server).

### Fix aplicado
- `ApiClient.BASE_URL` ahora apunta a `http://192.168.100.210:8081/habitia-backend`
  (IP LAN de la PC en la WiFi). Se dejó documentado cómo volver a
  `10.0.2.2` para el emulador.
- Rebuild OK; al reconectar el teléfono con `adb tcpip`/USB, instalar con
  `adb install -r -t androidApp-debug.apk`.

### Requisitos para que funcione en el teléfono
1. PC y teléfono en la MISMA WiFi.
2. Backend corriendo (`.\mvnw.cmd spring-boot:run` en HABITIA-Back).
3. Firewall de Windows permitiendo el puerto 8081 (o desactivar temporalmente).
4. MySQL corriendo.
5. Para volver al emulador: cambiar BASE_URL a `http://10.0.2.2:8081/habitia-backend`.

---

## CORTE 5 — SAFE-AREAS GLOBALES + ESPACIADO CONSISTENTE [2026-11-17]

### Cambios estructurales (Senior Mobile UI)
- **NUEVO `Core/components/HabitiaScreenContainer.kt`**: wrapper global de
  safe-areas. Centraliza:
    - Top: inset de la status bar (no renderiza bajo reloj/batería).
    - Bottom: BottomNav (80dp) + FAB (56dp) + safe-area inferior + 16dp.
    - Horizontal: 16dp uniforme.
  Uso: `HabitiaScreenContainer(hasFab = true) { padding -> LazyColumn(contentPadding = padding) }`.
  Constantes en `HabitiaInsets` (bottomNavHeight, fabSize, horizontalMargin, extraMargin).
- **App.kt**: el `Crossfade` de pantallas ahora aplica `padding(top = statusBars inset)`
  a nivel raíz → TODAS las pantallas heredan el safe-area superior.
- **Bottom padding estandarizado** a 176dp en los `LazyColumn` de Tasks (Lista),
  Food y Reminders (antes 90dp) → botones/cards del fondo ya no quedan
  cubiertos por el nav bar glass.
- Los FAB siguen a 104dp (80 nav + 12 margen + 12 aire) como en cortes anteriores.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD SUCCESSFUL.
- App lanzada en emulador Pixel_9 sin crashes.

---

## CORTE 6 — HAABI MASCOTA + FLOATING PILL NAV + HAZE FIX + DIARIO [2026-11-17]

### Haabi (mascota asistente)
- NUEVO `Core/components/AnimatedMascot.kt`: Composable con Box apilando capas,
  AnimatedVisibility (fadeIn + scaleIn 0.8f), rememberInfiniteTransition:
    - Respiración: scaleX/scaleY 0.98→1.02 (1.5s, Reverse).
    - Corazones flotantes: 3 corazones con translationY 0→-60 + alpha 1→0
      (3s, delays escalonados con StartOffset).
    - Balanceo: translationY 0→6 en el perro inferior (1.8s, Reverse).
  PLACEHOLDER con emoji (🐶/🐕/❤️) — sustituir por Image(painterResource(...))
  cuando tengas los assets reales.
- NUEVO `Core/components/HaabiCompanion.kt`: mascota flotante (bottom-end, sobre
  el nav bar) + burbuja de chat (bottom sheet ligero) con LazyColumn de
  mensajes, TextField + botón enviar, spinner de carga.
- `ApiClient.haabiChat(userMessage)`: llama a Gemini 1.5 Flash
  (generateContent). API key en `ApiClient.GEMINI_API_KEY` (placeholder
  "TU_API_KEY_AQUI"). Obtén tu key gratis en https://aistudio.google.com/app/apikey
  y pégala. Sin key, Haabi responde con un mensaje de fallback (no crashea).
- Integrado en App.kt como overlay global (visible en todas las pantallas
  excepto Splash/Welcome/Login/Register).

### Floating Pill Tab Bar (SmartIsland-style)
- `HabitiaBottonNavigation`: forma píldora (RoundedCornerShape(40.dp)), tinte
  translúcido (0.82), borde de luz + sombra, marginHorizontal 24dp.
- Ícono activo con fondo tipo pastilla del color principal (primary.copy(0.22));
  inactivos solo el ícono. Indicador nativo de M3 desactivado.
- App.kt: flota con padding(bottom = safeArea + 16dp).

### Diario personal ("Mi Diario")
- `HabitiaScreen.Diary` + ruta en App.kt; tarjeta en "Más".
- `Presentation/more/DiaryScreen.kt`: portada → vista de lectura/escritura con
  navegación horizontal página a página (AnimatedContent slide+fade), fecha,
  TextField multilínea con FontFamily.Serif, "Nueva página", dark mode aware.
  Estado local (mutableStateListOf). Sin backend todavía.

### Haze / Glass fix (parte por parte)
- EisenhowerScreen: QuadrantDrop y ColumnCard ahora pasan hazeState a
  habitiaGlass (antes ruta estática) → blur real en matriz y sprint.
- AppearanceScreen: ambos HabitiaGlassCard ya pasan hazeState (verificado).
- GlassTextField: pasa hazeState (ya aplicado).
- Regla aplicada: capa fuente HERMANA (no ancestro) para que el filtro zIndex
  de Haze 1.2.x incluya el área; HazeStyle explícito en la ruta con blur.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD SUCCESSFUL.
- APK listo en `androidApp/build/intermediates/apk/debug/androidApp-debug.apk`.

---

## CORTE 7 — HAABI CON ASSETS REALES + DRAGGABLE + THOUGHT BUBBLE + DIARIO DEDICADO [2026-11-17]

### Assets reales integrados
- Copiados los 12 PNG de `Haabi_Assets_Compose.zip` a
  `shared/src/commonMain/composeResources/drawable/` (haabi_duo_hugging,
  haabi_hearts, etc.).
- `AnimatedMascot.kt` reescrito usando `painterResource(Res.drawable...)`
  (Compose Multiplatform Resources) en vez de emoji placeholders.
  Animaciones conservadas: respiración, corazones flotantes, balanceo.

### Haabi draggable + burbuja de pensamiento
- `HaabiCompanion.kt` reescrito:
  - Mascota **arrastrable** (pointerInput detectDragGestures) con offset
    persistente → el usuario la mueve donde no incomode.
  - Burbuja de pensamiento (thought bubble) con mensaje de ánimo, tappable.
  - Al tocar la burbuja o la mascota → abre la pantalla dedicada `HaabiScreen`.
  - Cuando se cierra la burbuja, aparece un FAB de chat para reabrirla.

### Apartado dedicado de Haabi
- NUEVO `Presentation/more/HaabiScreen.kt`: pantalla secundaria (stack navigator)
  con mascota grande + chat completo con Gemini (mensajes, input, spinner).
  Accesible desde "Más" → "Haabi" y desde la mascota flotante.
- `HabitiaScreen.Haabi` + ruta en App.kt + tarjeta en MoreScreen.

### Haze / Glass fix
- EisenhowerScreen: QuadrantDrop y ColumnCard ahora pasan `hazeState` a
  `habitiaGlass` (blur real en matriz y sprint, antes ruta estática).
- AppearanceScreen: ambos HabitiaGlassCard pasan hazeState (verificado).
- GlassTextField: pasa hazeState (ya aplicado).

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD SUCCESSFUL.
- APK instalado en Honor, arranque sin crashes ni errores Habitia-API.

## CORTE 8 — UI: ISLA CONTROLABLE, MASCOTA GLOBAL Y PANEL DE CONFIGURACIÓN

### UiConfig (Core/config/UiConfig.kt) — NUEVO
- Panel centralizado y documentado de TODOS los valores visuales:
  - `IslandConfig`: altura (64dp), margen lateral (48dp), radio píldora (32dp),
    transparencia del vidrio (0.72), sombra (12dp), separación inferior (32dp),
    padding de contenido (176dp), pastilla del tab activo (alpha 0.22, radio 18,
    padding 14/4, ícono 24dp).
  - `HaabiConfig`: placement (GLOBAL | INSIDE_RITUAL_CARD), tamaño mascota
    (110dp), corazones, márgenes (end 16dp, bottom 112dp), y todo lo del
    Dialog del chat (92% ancho, máx 500dp, lista 300dp, radio 20dp).
- Guía de ajuste rápido en comentarios al inicio del archivo.

### Isla (HabitiaBottonNavigation.kt) — reescrita con Row propio
- CAUSA del descuadre: NavigationBar de M3 mide sus items a 80dp FIJOS
  internamente y no expone la altura; forzar .height(64dp) recortaba el
  contenido. SE ELIMINA NavigationBar/NavigationBarItem.
- Ahora: Row con height exacta de IslandConfig, tabs como Column
  (weight(1f) + fillMaxHeight + centrado), pastilla activa y etiqueta.
- containerAlpha 0.82 → 0.72 (más efecto vidrio real).
- Sin ripple en tabs (la pastilla activa ya indica selección).

### Mascota / chat (HaabiCompanion.kt) — reescrito
- FIX: el Box del overlay quedó sin tamaño en el corte anterior
  (Box(modifier) a 0px) → la mascota no se veía. Ahora fillMaxSize.
- Mascota GLOBAL de nuevo: alineada BottomEnd con padding de HaabiConfig
  (SIN offset manual, sin drag). Es el botón del chat (clickable).
- Chat: Dialog CENTRADO (no flota) con X (Icons.Default.Close) y animación
  real de entrada/salida (MutableTransitionState + fadeIn/fadeOut +
  scaleIn/scaleOut; el Dialog permanece compuesto durante la salida).
- Se eliminó el FAB con ícono Send que reemplazaba a la mascota.

### App.kt
- zIndex explícito: isla 2f, overlay de Haabi 3f (mascota siempre encima).
- bottomSpacing de la isla desde IslandConfig.

### HomeScreen.kt
- Mascota dentro de la tarjeta de rituales SOLO si
  HaabiConfig.placement == INSIDE_RITUAL_CARD (defecto: GLOBAL, tarjeta limpia).
- LazyColumn usa IslandConfig.contentBottomPadding.

### Otros ajustes del corte (haze y paleta)
- CustomPaletteCreator recibe hazeState (blur real en "Crea tu Paleta (HEX)").
- FoodScreen: MealCard recibe hazeState (blur real en Menú de la Semana).

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL. Pendiente instalar en Honor (ADB desconectado al cerrar sesión).

## CORTE 9 — BACKEND: DIARIO, SEGURIDAD, JWT LARGO + RED NUEVA + addHabit

### Red nueva (10.10.10.x)
- IP del PC en la nueva red: 10.10.10.100. Teléfono (ADB): 10.10.10.101.
- `ApiClient.kt`: DEVICE_URL actualizado a http://10.10.10.100:8081/habitia-backend
  (USE_DEVICE sigue en true). Backend escucha en 0.0.0.0:8081 y el firewall
  permite java en la red pública.

### Seguridad backend
- `User.java`: @JsonIgnore en getPassword() → GET /Users y /CalendarEvents ya no
  serializan el hash (verificado: claves devueltas id,name,email,birthDate).

### Persistencia del Diario (backend nuevo)
- Nueva entidad `Tablas/DiaryEntry.java` (id, user, entryDate LocalDate, text
  hasta 4000 chars), repositorio `DiaryEntryRepository` (findByUserOrderByEntryDateDesc,
  findByUserAndEntryDate, findByIdAndUser), servicio `DiaryEntryService/Impl`,
  controlador `DiaryEntryController` con GET (lista), GET /date/{date}, POST,
  PUT /{id}, DELETE /{id}, todo filtrado por el usuario del JWT.
- DTOs `DiaryEntryCreateDto` y `DiaryEntryResponseDto` (con fromEntity).
- Probado end-to-end con curl: POST → 201, GET → 1 entrada, password no filtrado.

### JWT
- `application.properties`: expiration-ms 900000 (15 min) → 86400000 (24 h) para
  que la sesión no muera en cada uso. Pendiente: refresh token / DataStore.

### Frontend
- `HabitRepository`: nuevo `suspend fun addHabit(habit)` (default vacío para mocks).
- `ApiHabitRepositoryImpl`: implementa addHabit con POST /Habits + refresh() de
  la caché.
- `HomeViewModel`: nuevo `createRitual(name)` que persiste el ritual y deja que
  el Flow actualice la UI.
- `HomeScreen`: el diálogo "Nuevo ritual diario" ahora llama a
  `viewModel.createRitual(...)` en lugar de guardar solo en memoria.
- `ApiClient.kt`: DEVICE_URL actualizado (ver arriba).

### Verificación
- Backend: `mvnw compile` BUILD SUCCESS; arranque OK; login + Diary + security OK.
- Frontend: `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug`
  BUILD SUCCESSFUL; APK instalado en 10.10.10.101 y arranque sin crashes ni
  errores Habitia-API.

## CORTE 10 — PERSISTENCIA DE SESIÓN, DIARIO FRONTEND, IA (GEMINI) Y TELÉFONO EN PERFIL

### Persistencia de sesión (DataStore)
- NUEVO `Data/remote/auth/SessionStorage.kt` (commonMain) + `expect/actual
  rememberSessionManager()` por plataforma.
- Android: `DataStoreSessionStorage` (androidMain) guarda token/email/userId/
  name/birthDate en DataStore "session_prefs" (sobrevive al cierre de la app).
- iOS: `NoopSessionStorage` (en memoria, sin cambios vs antes).
- `SessionManager`: constructor con storage + scope; persiste en onLogin,
  onUserResolved, updateProfile, clear; nuevo `suspend fun restore()`.
- `App.kt`: usa `rememberSessionManager()` y `LaunchedEffect(Unit) { session.restore() }`;
  al restaurar, el LaunchedEffect(token,userId) dispara el refresh de todos
  los repositorios.

### Diario (frontend)
- NUEVO `Domain/model/DiaryEntry.kt`, `Domain/repository/DiaryRepository.kt`,
  `Data/remote/dto/DiaryEntryDto.kt`, `Data/repository/ApiDiaryRepositoryImpl.kt`
  (GET/POST/PUT/DELETE /DiaryEntries con caché Flow) y `Presentation/more/
  DiaryViewModel.kt`.
- `App.kt`: `diaryRepo` + `diaryViewModel` creados y refresh en el bloque de
  sesión. `DiaryScreen` ahora acepta `viewModel: DiaryViewModel? = null`
  (listo para wiring fino de la UI del libro; el backend ya estaba probado).

### IA (Haabi / Gemini)
- CAUSA del fallo: el guard comparaba GEMINI_API_KEY contra su propio valor
  ("AQ...."), por lo que haabiChat SIEMPRE devolvía el mensaje de "configura
  tu key" y nunca llamaba al API. Además el modelo `gemini-1.5-flash` ya no
  existe (404); se cambió a `gemini-2.5-flash`.
- Fix: guard ahora solo verifica blank/place-holder; modelo actualizado a
  `gemini-2.5-flash` (probado con curl: responde correctamente).
- Nuevo parámetro opcional `context` en haabiChat para inyectar hábitos/tareas
  del día en el prompt (memoria/contexto), usado cuando el caller lo pase.

### Teléfono en perfil (backend)
- `User.java`: campo `phone` + getter/setter.
- `UserResponseDto`, `UserUpdateDto`: campo phone + constructor/getter/setter.
- `UserController`: toResponseDTO pasa phone; PUT actualiza phone.
- (Frontend: UserDto/ApiUserRepositoryImpl/perfil quedan para el siguiente corte.)

### Verificación
- `:shared:compileDebugKotlinAndroid`, `:shared:compileKotlinIosSimulatorArm64`,
  `:androidApp:assembleDebug` → BUILD SUCCESSFUL.
- APK instalado en 10.10.10.101, arranque sin crashes ni errores Habitia-API.

## CORTE 11 — BACKEND ARRANQUE, TELÉFONO EN PERFIL (BACKEND+DTO) Y NOTA DE PUERTO

### Error de backend reportado ("BUILD FAILURE / Process terminated exitcode 1")
- Causa: puerto 8081 ya ocupado por un proceso java anterior (el backend
  anterior no se cerró del todo). NO es un error de código.
- Comando para liberarlo y arrancar:
    $p = (Get-NetTCPConnection -LocalPort 8081 -State Listen).OwningProcess
    Stop-Process -Id $p -Force
    cd C:\ProyectoAppMoviles\HABITIA-Back
    .\mvnw.cmd spring-boot:run
- Verificación: backend arranca OK y login responde 200.

### Teléfono en perfil
- Backend: campo phone en User + UserResponseDto + UserUpdateDto + UserController.
- Frontend: UserResponseDto y UserUpdateRequestDto ahora incluyen phone (opcional),
  por lo que el cliente tolera el nuevo campo sin romper. El editor del perfil
  y el PUT con phone se dejan para el siguiente corte.

### Nota
- Ingredientes de Meals y categoría de Shopping ya existen en el backend
  (MealIngredientController, ShoppingItemController); el wiring fino en el
  frontend (formularios) queda como siguiente corte.

## FIX — POST /auth/register (UserResponseDto 4-arg → 5-arg)
- Al agregar phone a UserResponseDto, el constructor quedó de 5 args pero
  AuthController.register seguía llamándolo con 4 →
  "Unresolved compilation problem: constructor UserResponseDto(Long,String,
  String,LocalDate) undefined" al registrar.
- FIX: AuthController.register ahora pasa createdUser.getPhone() como 5º arg.
- Verificado: POST /auth/register → 201 {id:5,...,phone:null}.

## FIX — CHAT HAABI (no respondía / quedaba cargando) + MODELO MÁS RÁPIDO
- Se detectó un `return try {` duplicado en ApiClient.haabiChat (syntax error
  latente) → corregido.
- Modelo: gemini-2.5-flash → gemini-3.1-flash-lite (medido: ~1.3 s vs ~3.2 s
  con el prompt de Haabi; 2.0-flash da 404 con esta key).
- haabiChat: envuelto en withTimeout(30_000) para que NUNCA quede colgado.
- HaabiCompanion y HaabiScreen: el envío ahora usa try/finally → `loading`
  siempre vuelve a false (antes podía quedar en true si el hilo se cortaba).
- Log de error visible: [Habitia-Haabi] send FALLO: <mensaje>.
- APK recompilado; pendiente instalar en el teléfono (ADB desconectado).

## CORTE 12 — HAABI AUTOMATIZADO (CREAR TODO + RUTA EXACTA + NAVEGACIÓN)

### Automatización con function calling (ya existía, se completó)
- ApiClient.haabiChat soporta tools (function calling) con 6 funciones:
  crear_tarea, crear_habito, crear_recordatorio, crear_comida, crear_compra,
  crear_diario. Cada una ejecuta la acción contra los repos reales y devuelve
  un texto de confirmación con "Ruta: /xxx".
- HaabiActionsImpl implementa HaabiActions y crea los datos en el backend,
  devolviendo la ruta exacta (ej. "Ruta: /tasks").
- HaabiScreen ya pasaba `actions = haabiActions`; ahora también onNavigate.
- HaabiCompanion (chat flotante) ahora TAMBIÉN recibe `actions` y `onNavigate`,
  por lo que puede crear cosas desde cualquier pantalla.

### Navegación rápida
- HaabiCompanion y HaabiScreen parsean "Ruta: /xxx" de la respuesta y muestran
  un botón "Ir a X →" que navega a la pantalla correspondiente
  (routeToScreen mapea /tasks→Tasks, /food→Food, /diary→Diary, etc.).
- App.kt: HaabiCompanion y HaabiScreen reciben onNavigate que actualiza
  currentScreen.

### Robustez
- haabiChat envuelto en withTimeout(30_000); executeHaabiAction usa for-loop
  (suspend-safe). try/finally en los envíos para que loading siempre se apague.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL. APK listo para instalar (ADB desconectado).

## CORTE 13 — BASE_URL AUTO-DETECTADA (FUNCIONA EN CUALQUIER RED) + HAABI AUTOMATIZADO

### BASE_URL auto-detectada
- Antes: constante con flag USE_DEVICE (requería rebuild al cambiar de red).
- Ahora: `ApiClient.BASE_URL` es @Volatile y `resolveBaseUrl()` prueba en
  secuencia 10.10.10.100, 192.168.100.210, 10.0.2.2, localhost y se queda
  con el primero que responda (cualquier HTTP = alcanzable). Llamado en
  App.kt (LaunchedEffect) antes de restore().
- La app ahora funciona en la red 10.10.10.x, en 192.168.100.x y en el
  emulador sin recompilar.

### Haabi automatizado (function calling)
- Ya existía la infraestructura (haabiTools + executeHaabiAction + HaabiActionsImpl).
  Se completó: HaabiCompanion ahora recibe actions + onNavigate, y tanto
  HaabiCompanion como HaabiScreen parsean "Ruta: /xxx" de la respuesta y
  muestran un botón "Ir a X →" que navega a la pantalla correspondiente.
- Haabi puede crear tareas, hábitos, recordatorios, comidas, compras y
  entradas de diario; responde con la ruta exacta.
- Robustez: withTimeout(30_000), executeHaabiAction suspend-safe (for-loop),
  try/finally en los envíos.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL. APK listo para instalar (ADB desconectado).

## CORTE 14 — MENÚ SEMANAL POR COMIDAS + LISTA DE COMPRAS POR DÍA + INGREDIENTES

### Backend (HABITIA-Back)
- ShoppingItem: nuevo campo `itemDate` (LocalDate, nullable) con getter/setter.
- ShoppingItemCreateDto y ShoppingItemResponseDto: campo itemDate.
- ShoppingListController.toResponseDTO: pasa item.getItemDate().
- ShoppingListServiceImpl.agregarManual: setItemDate(request.itemDate()).

### Frontend
- ShoppingItem model: campo `date: String?`.
- ShoppingItemCreateDto/ResponseDto: campo `itemDate`.
- CalendarShoppingMapper.toDomain: mapea itemDate -> date.
- ShoppingRepository.addItem: nuevo param `date`.
- ApiShoppingRepositoryImpl.addItem: pasa itemDate al DTO.
- HaabiActions.createMeal: nuevo param `ingredients: List<String>?` (se guarda en Meal.ingredients).
- HaabiActions.createShoppingItem: nuevo param `date`.
- HaabiActionsImpl: pasa ingredients y date.
- ApiClient haabiTools: crear_comida ahora acepta "ingredientes" (coma-separados);
  crear_compra acepta "fecha".
- executeHaabiAction: pasa ingredientes y fecha.
- FoodScreen: comidas agrupadas por MealType (Desayunos/Almuerzos/Cenas/Snacks)
  en secciones con título.
- FoodScreen: lista de compras agrupada por fecha/día (los ítems sin fecha
  caen en "Sin día").

## CORTE 15 — MEMORIA DE SESIÓN, MÚLTIPLES CHATS, PERSONA Y MENOS REDUNDANCIA

### Memoria de sesión
- ApiClient.haabiChat ahora acepta `history: List<Pair<Boolean,String>>` y construye
  un `contents` multi-turno (user/model) para que Haabi recuerde la conversación.
- HaabiScreen pasa el historial (todos los mensajes previos) en cada envío.
- Prompt mejorado: "No repitas lo que el usuario dijo ni parafrasees; ve directo
  al punto." + máx 2 frases (menos redundancia).

### Múltiples chats + eliminar
- HaabiScreen: modelo `ChatSession (id, title, messages)`. Barra de chips para
  cambiar de conversación, botón "+" para nueva y "X" para eliminar (si queda
  una sola, se limpia en vez de borrar).

### Persona (cómo te trata la IA)
- Selector de chips: Normal / casual y cercano / formal y respetuoso /
  motivador y energético. Se envía como `persona` al prompt.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL.

## CORTE 16 — HAABI: MENÚ SEMANAL + LISTA DE COMPRAS + TAREA AUTOMÁTICA

### Prompt de Haabi mejorado
- Instrucción explícita: si el usuario pide un menú semanal, Haabi debe llamar
  crear_comida (con ingredientes) por cada comida, crear_compra por cada
  ingrediente (con fecha del día) y crear_tarea con título "Lista de compras
  de la semana" y descripción con todos los ingredientes.
- "No produzcas tablas ni listas largas en texto" (evita redundancia).
- Respuesta ahora incluye resumen corto del modelo (si <= 280 chars) +
  las confirmaciones con ruta exacta.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL. APK listo para instalar.

## CORTE 17 — HAABI: RESPUESTA MÁS RÁPIDA Y EJECUCIÓN COMPLETA DEL MENÚ SEMANAL

### Prompt
- Se eliminó "No produzcas tablas..." (rompía el function calling del modelo).
- Instrucción explícita para menú semanal: crear_comida por comida, crear_compra
  por ingrediente (con fecha), crear_tarea "Lista de compras de la semana".
- El modelo ahora genera ~21 comidas + lista de compras + tarea en una sola
  respuesta.

### Ejecución
- executeHaabiAction vuelve a for-loop secuencial (más estable que async).
- La respuesta incluye resumen corto del modelo + confirmaciones con ruta.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL. APK listo.

## CORTE 18 — CATEGORÍAS DE TAREAS, INGREDIENTES SELECCIONABLES, COMPRAS POR DÍA

### Tareas
- HaabiActionsImpl.createTask ahora infiere la categoría ("Compras" si el título/
  descripción menciona compras, "Alimentación" si menciona comida, "Hogar" por
  defecto) para que la "Lista de compras de la semana" caiga en la pestaña
  Compras y no en Hogar.

### Comidas (FoodScreen)
- Editor de comidas: además del campo de texto, ahora hay un catálogo de
  ingredientes seleccionables (chips) que se agregan/quitan del campo.
- El menú se agrupa por tipo (Desayunos/Almuerzos/Cenas/Snacks) y la lista
  de compras por día/fecha.

### Backend
- ShoppingItem.itemDate persistido y devuelto en el DTO.

### Pendiente
- Categorías de tareas totalmente modificables por el usuario (añadir/renombrar/
  eliminar) — hoy son fijas (Hogar, Compras, Alimentación).

## CORTE 19 — FUNCIÓN crear_plan_semanal_comidas + LIMPIEZA DE HAABIASSISTANT

### Limpieza
- Eliminado `Data/assistant/HaabiAssistant.kt` (deuda técnica: nunca pasaba
  `actions` a Gemini, por lo que no ejecutaba nada).
- Eliminado `Domain/model/HaabiAction.kt` (solo lo usaba HaabiAssistant).

### Nueva herramienta: crear_plan_semanal_comidas
- ApiClient.haabiTools(): nueva función `crear_plan_semanal_comidas` con param
  `plan_json` (JSON array de {name, type, date, ingredients}).
- HaabiActions + HaabiActionsImpl: nuevo método `createWeeklyMenuPlan(planJson)`
  que crea todas las comidas, la lista de compras (dedup de ingredientes) y
  la tarea "Lista de compras de la semana" en una sola llamada.
- executeHaabiAction: caso "crear_plan_semanal_comidas".
- Prompt actualizado para instruir a Haabi a usar esta herramienta cuando el
  usuario pida un menú semanal.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL.

## CORTE 20 — ESPECIFICACIÓN EXTERNA: MODELOS, CATÁLOGO, MENÚ ESTRUCTURADO

### Modelos de dominio
- NUEVO FoodCategory.kt (enum con label/emoji/order + fromLabel + ordered).
- NUEVO MealIngredient.kt (@Serializable: name, quantity, category).
- Meal.kt: añadidos mealIngredients, youtubeQuery, youtubeUrl.
- ShoppingItem.kt: añadidos foodCategory, sourceDay, sourceMealType, sourceMealName.

### Catálogo de ingredientes
- NUEVO IngredientCatalogRepository.kt (interfaz).
- NUEVO InMemoryIngredientCatalogRepository.kt (seed inicial por categoría).

### Haabi
- HaabiActions + HaabiActionsImpl: nuevo createWeeklyMenuPlan(planJson) que crea
  comidas + lista de compras (dedup) + tarea "Lista de compras de la semana".
- ApiClient: GeminiFunctionParameter extendido (items, properties, required, enum).
  crear_plan_semanal_comidas con plan_json estructurado.
  executeHaabiAction: caso crear_plan_semanal_comidas.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL. APK listo.

## CORTE 21 — FIX SHOPPING ITEMS (InterpreteDeIngredientes acepta sin cantidad)

### Backend
- InterpreteDeIngredientes.parse(): ahora acepta ingredientes sin cantidad
  (ej. "Pollo" → quantity=1, unit=UNIT) y sin unidad reconocible si no se
  envió cantidad. Antes lanzaba ResponseStatusException → 403 en el frontend.
- Verificado: POST /ShoppingLists/1/items {description:'Pollo', itemDate:'2026-11-23'}
  → 201 con itemDate persistido.

### Frontend
- APK recompilado e instalado. La lista de compras ahora se crea y muestra.

## CORTE 22 — INGREDIENTES PERSISTENTES, SHOPPING POR DÍA, TUTORIAL EN MEALCARD

### Backend
- InterpreteDeIngredientes.parse(): acepta ingredientes sin cantidad/unidad
  (default 1 UNIT). Fix del 403 al crear shopping items.

### Frontend (persistencia de ingredientes)
- ApiMealRepositoryImpl: ahora persiste ingredientes en /MealIngredients
  (POST por ingrediente al crear comida) y los recupera en refresh
  (GET /MealIngredients/meal/{id}). Ya no se pierden al reiniciar.

### FoodScreen
- MealCard: botón "▶ Tutorial" que abre YouTube (usa youtubeUrl o construye
  desde youtubeQuery).
- Editor de comidas: catálogo de ingredientes seleccionables (chips).
- Menú agrupado por tipo (Desayunos/Almuerzos/Cenas/Snacks).
- Lista de compras agrupada por día/fecha.

### Verificación
- `:shared:compileDebugKotlinAndroid` y `:androidApp:assembleDebug` → BUILD
  SUCCESSFUL. APK instalado en 192.168.100.52:43785.

## CORTE 23 — LIMPIEZA TOTAL DE LA BD (TODOS LOS USUARIOS)

- Se agregó temporalmente AdminClearController con DELETE /admin/clear-all
  que truncó todas las tablas de datos (meals, shopping_items, shopping_lists,
  tasks, habits, habit_completions, reminders, diary_entries, calendar_events,
  meal_ingredients) para todos los usuarios.
- Se corrigió el nombre de tabla habit_completions → Habit_complet.
- Se eliminó AdminClearController después de usarlo (no queda en el código).
- Verificado: MEALS=0, SHOPPING=0, TASKS=0, HABITS=0, REMINDERS=0 para
  a@b.com (id=2). BD limpia para todos los usuarios.

## CORTE 24 — FIX DE RAÍZ: PROMPT ↔ PARSER, AGRUPACIÓN POR DÍA/COMIDA, HAABI ARRASTRABLE

### Causa raíz de "no ves ningún cambio"
- El prompt pedía ingredientes anidados {nombre,cantidad,categoria} + youtubeQuery,
  pero el parser esperaba un array plano con ingredients como string → no creaba nada.
- Solución: herramienta crear_plan_semanal_comidas ahora toma un único param
  "plan" con JSON en español: {dias:[{fecha,nombre,comidas:[{nombre,tipo,
  youtubeQuery,ingredientes:[{nombre,cantidad,categoria}]}]}]}. Prompt y parser
  coinciden EXACTAMENTE (verificado con Gemini 2.5-flash: genera ese plan).
- Modelo: gemini-3.1-flash-lite → gemini-2.5-flash (function calling confiable).

### Backend
- ShoppingItem: + source_meal_type, source_meal_name (+ DTOs create/response,
  controller, service). Verificado POST → item con día/comida/categoría.

### Frontend
- HaabiActionsImpl.createWeeklyMenuPlan: parsea la estructura anidada, guarda
  comidas con youtubeQuery/youtubeUrl, ingredients en lista de compras con
  sourceDay/sourceMealType/nombre+cat, tarea "Compra semanal" con resumen.
- FoodScreen: lista de compras agrupada DÍA → Desayuno/Almuerzo/Cena (íems
  ordenados por nombre). Menú agrupado por tipo + botón "▶ Tutorial".
- HaabiCompanion: mascota ARRASTRABLE (detectDragGestures + offset persistente),
  tap abre el chat, drag NO lo abre (flag hasDragged). Chat intacto.

### Verificación
- shared + androidApp → BUILD SUCCESSFUL. Backend compila. APK instalado en
  192.168.100.52:41403. Arranque sin crashes.
- Probe Gemini con prompt real → generó plan válido con 1 día × 3 comidas,
  ingredientes categorizados y youtubeQuery.

## CORTE 25 — CATÁLOGO CATEGORIZADO COLAPSABLE + LISTA "POR ALIMENTO" + LIMPIEZA

### Causa de las capturas (datos viejos)
- La lista mostraba items del mock antiguo (fecha 2023-11-06, todo "Otros").
- Se limpió TODA la BD (todos los usuarios) otra vez con admin controller temporal.

### Catálogo de ingredientes (editor de comidas)
- InMemoryIngredientCatalogRepository conectado al FoodViewModel.
- FoodViewModel.catalogByCategory: StateFlow<Map<FoodCategory, List<CatalogIngredient>>>.
- MealEditorDialog: catálogo ahora son SECCIONES COLAPSABLES por FoodCategory.ordered
  (emoji + label + count), cada una con chips de ingredientes + chip "+ nuevo"
  que abre diálogo para agregar al catálogo (con selector de categoría).

### Lista de compras
- Selector "Por día | Por alimento".
- Por día: Fecha -> Desayuno/Almuerzo/Cena -> items ordenados.
- Por alimento: agrupado por FoodCategory.ordered con emoji + label + count.

### Verificación
- shared + androidApp → BUILD SUCCESSFUL. BD limpia. Backend arranca.

## CORTE 26 — IA CREA TAREA + AGENDA EN CALENDARIO + ALERTA + CATEGORÍAS EDITABLES

### Automatización (HaabiActionsImpl)
- Nuevo helper scheduleForTask(): al crear una tarea, también:
  1) agenda un CalendarEvent (09:00-10:00 del día de la tarea),
  2) crea un Reminder (alerta a las 09:00) para recordarla durante el día.
- createTask(): infiere categoría (Compras/Alimentación/Hogar), guarda la
  tarea, agenda y alerta. Confirmación con ruta /tasks.
- createWeeklyMenuPlan(): crea la tarea "Compra semanal" + agenda + alerta.
- HaabiActionsImpl recibe CalendarEventRepository (inyectado en App.kt).

### Categorías de Tareas editables
- NUEVO TaskCategoryRepository + InMemoryTaskCategoryRepository
  (seed: Hogar, Compras, Alimentación).
- TasksViewModel: expone categories StateFlow + addCategory/deleteCategory.
- TaskScreen: chips dinámicos + "+ Categoría" (diálogo) + "− <activa>"
  para borrar (mínimo 1).

### Verificación
- shared + androidApp → BUILD SUCCESSFUL. Backend arranca y acepta
  POST /CalendarEvents (insert verificado en log) y POST /Reminders.
- Pendiente instalar (teléfono desconectado de ADB).

## CORTE 27 — MAPPING GLOBAL + FIX TAREAS/AGENDA/ALERTA + LATENCIA IA

### Causas raíz corregidas
1) "No aparece en Compras": uiState.currentCategory quedaba en Hogar. Nuevo
   HaabiEventBus (SharedFlow) emite cada creación de Haabi; App lo observa y
   llama tasksViewModel.changeCategory(categoria) + refresh de todos los repos.
2) "No se agenda / no se alerta": mismo bus dispara refresh de calendarRepo y
   reminderRepo al crear. createTask crea tarea + CalendarEvent + Reminder.
3) "Mañana" como fecha: normalizeDate() convierte hoy/mañana a yyyy-MM-dd.
4) Latencia IA: prompt recortado de ~700 a ~80 tokens (esquema va en la
   descripción de la herramienta); se prohibió texto previo al functionCall.

### Otros hallazgos del mapping (estado)
- Backend: todos los módulos conectados (Auth, Tasks, Habits, Reminders, Meals,
  MealIngredients, Shopping, Calendar, Diary). Sin persistencia pendiente salvo
  categorías/ catálogo que son en-memoria a propósito (persistencia ligera).
- Estáticos: categorías de tareas y catálogo de ingredientes son in-memory
  (se pierden al cerrar app) — pendiente moverlos a backend si se quiere persistencia.
- Código muerto: Mock*Repos, Items/ (se conservan como referencia).

## CORTE 28 — CATÁLOGOS A BACKEND + LIMPIEZA DE CÓDIGO MUERTO

### Persistencia real (antes InMemory, ahora MySQL)
- Backend: nuevas entidades TaskCategory, CatalogIngredientEntity + repos JPA +
  CatalogController (/Catalogs/categories, /Catalogs/ingredients) con seed
  automático (3 categorías, 48 ingredientes) y borrado @Transactional.
- Front: ApiTaskCategoryRepository + ApiIngredientCatalogRepository (reemplazan
  a los InMemory*). Se refrescan al iniciar sesión, igual que los demás módulos.
-删除 por nombre (categorías) y por id (ingredientes) verificado.

### Código muerto eliminado
- MockTaskRepositoryImpl, MockHabitRepositoryImpl, MockMealRepositoryImpl,
  MockReminderRepositoryImpl (no se usaban).
- Core/components/Items/ completo (6 composables, 0 referencias).
- InMemoryTaskCategoryRepository, InMemoryIngredientCatalogRepository.
- MockUserPreferencesRepositoryImpl SE CONSERVA (es el repo de preferencias activo).

### Verificación
- shared + androidApp → BUILD SUCCESSFUL. Backend compila y corre.
- GET /Catalogs/categories → Hogar, Compras, Alimentación.
- GET /Catalogs/ingredients → 48 items (Aceites/Aceite de oliva ...).
- POST + DELETE categoría verificado (Trabajo agregada y borrada).

## CORTE 29 — REFACTOR HEXAGONAL DEL BACKEND (COMPLETO)

### Estructura nueva (antes: 8 carpetas planas / ahora: 3 areas)
```
upeu.edu.pe.HABITIA_Back/
  HabitiaBackApplication.java
  auth/infrastructure/{web,security}/            (7 archivos)
  shared/domain/{model,enums}/ + infra/persistence/ (11: User, enums, UserRepository)
  modules/<modulo>/
     domain/model/              entidades JPA
     domain/service/            InterpreteDeIngredientes
     application/service/       interfaces + Impl
     application/dto/           DTOs
     infrastructure/web/        controllers
     infrastructure/persistence/ repositorios JPA
```
Modulos: task (8), habit (15), meal (17), reminder (8), shopping (14),
calendar (5), diary (7), user (8), catalog (5) = 94 archivos reubicados.

### Proceso
- Script de mapeo FQN (104 clases) + git-equivalente move + reescritura de
  package/imports + imports cross-module (User referenciado por todos;
  Shopping->Meal; Enums a shared.domain.enums).
- Fixes: package raiz, wildcards viejos (Repository.*, Tablas.*, DTOs.*),
  FQN inline en DiaryEntryResponseDto, `mvnw clean` para limpiar .class viejos.
- Scripts temporales eliminados.

### Verificacion
- `mvnw clean compile` → BUILD SUCCESS.
- Backend arranca: "Started HabitiaBackApplication in 9.2s".
- Login OK, GET /Catalogs/categories y /Tasks/user/2 responden.

## CORTE 30 — web/ -> Controller/ + DOCUMENTACIÓN CORTA + ARQUITECTURA.md

- Renombradas las 10 carpetas `infrastructure/web/` a `infrastructure/Controller/`
  (paquetes actualizados). Compila y arranca.
- Comentarios cortos estilo "haciendo las cosas" en los 12 Controllers y en
  los 12 ServiceImpl/Jwt/Security/Interprete (ej. "//creacion de la lista de
  compras desde tus comidas; sino pues vacia"). Se quitaron 204 comentarios
  verbosos viejos.
- NUEVO C:\ProyectoAppMoviles\HABITIA-Back\ARQUITECTURA.md con: arbol completo,
  flujo de una peticion (app -> filtro JWT -> Controller -> Service -> dominio
  -> repositorio -> MySQL -> vuelta), regla de oro por capa y ejemplos.
- Verificado: Started HabitiaBackApplication in ~10s.

## CORTE 31 — PAQUETE .di (Modules + SettingProvider) + NOTIFICACIONES LOCALES

### .di (estructura del profesor, sin Koin)
- NUEVO di/SettingProvider.kt: expone backendBaseUrl (el que resuelve ApiClient),
  usesDeviceNetwork y el canal/ID de notificaciones.
- NUEVO di/Modules.kt: Modules Manual — provee httpClient, session y todos los
  Api*RepositoryImpl (los que conectan al backend). App.kt ahora construye los
  repositorios a traves de Modules.* (wiring manual, NO Koin).

### Notificaciones locales (expect/actual)
- commonMain Data/notification/LocalNotificationPlatform.kt: interfaz +
  expect createLocalNotificationPlatform().
- androidMain: AndroidLocalNotificationPlatform (permiso POST_NOTIFICATIONS en
  Android 13+, canal en Android 8+, PendingIntent al paquete de la app,
  showAlert(title,message)). AndroidAppContext guarda el Context desde MainActivity.
- iosMain: IosLocalNotificationPlatform (no-op).
- AndroidManifest: + POST_NOTIFICATIONS. MainActivity: pide permiso al arrancar.
- shared/build.gradle.kts: + androidx.core.ktx (NotificationCompat).
- App.kt: el observer de HaabiEventBus ahora llama notifier.showAlert(...) cuando
  Haabi crea una tarea/plan -> notificacion real en la bandeja.

### Aclaracion
- El .di NO conecta al backend por si solo: la conexion la hacen ApiClient (Ktor)
  y los Api*RepositoryImpl. El .di solo entrega/esas dependencias y la config.

### Verificacion
- shared Android + iOS y androidApp assembleDebug -> BUILD SUCCESSFUL.
- APK listo; telefono desconectado de ADB para instalar.

## CORTE 32 — DI ESTILO PROFESOR: KO Modules (Koin) + SettingProvider (createSettings)

### SettingProvider (patron profesor)
- commonMain di/SettingProvider.kt: `expect fun createSettings(): Settings`
  (com.russhwolf Settings) + object SettingProvider (URL backend, canal notif).
- androidMain: SharedPreferencesSettings (habitia_settings) usando AndroidAppContext.
- iosMain: NSUserDefaultsSettings.

### Modules (Koin, acoplado a Habitia)
- commonMain di/Modules.kt: `val appModule = module { ... }` con:
  - single createSettings + SessionManager + ApiClient.httpClient
  - single<TaskRepository>, Habit, Reminder, Meal, Shopping, Calendar,
    Diary, User, TaskCategory, IngredientCatalog, AuthRepository (implementaciones Api reales)
  - factory de los 7 use cases y de los 8 ViewModels
- MainActivity: startKoin { androidContext(this); modules(appModule) }.
- libs.versions.toml: + koin 4.0.3, settings 1.3.0; build.gradle: koin-core,
  koin-compose, kotlinx-settings (shared) + koin-android (shared y androidApp).

### Nota
- App.kt conserva el wiring manual con remember (no se rompio); el DI del
  profesor existe y arranca con Koin. La conexion al backend la siguen haciendo
  ApiClient (Ktor) + los Api*RepositoryImpl registrados en appModule.

### Verificacion
- shared Android + iOS + androidApp assembleDebug -> BUILD SUCCESSFUL.

## CORTE 33 — DOCUMENTACIÓN AZURE DEVOPS (3 SPRINTS, PLANTILLAS DEL USUARIO)

- Leido Habitia_Backend_Division_Equipo.docx (842 lineas) para respetar el
  formato del profesor: hexagonal, RF-01..RF-14, responsables y flujo.
- Creado C:\ProyectoAppMoviles\AZURE_DEVOPS_DOCUMENTACION.md: EPIC 1-8 con
  Features/PBI/Tasks en las plantillas del usuario (👑/🏆/📘/📋), pero con
  el estado REAL del codigo (Done / Parcial / Backlog) y tabla de evidencias
  (donde sacar cada captura: Postman, Swagger, MySQL, app, adb logcat).
- Creado AZURE_DEVOPS_ORDEN_CARGA.md: orden de alta en Azure DevOps, campos
  (Area Path, Iteration, Assigned To, State, Tags, Acceptance Criteria) y
  plantilla de comentario para cada Task Done.
- Nota: se senalo que RF-01 aparece con distinto responsable entre el docx
  (Mauricio) y el mensaje del usuario (Mayler) para que lo confirmen.

## CORTE 34 — DOCUMENTACIÓN AZURE DEVOPS EN FORMATO EXACTO DEL USUARIO

- Se tomaron las 2 capturas del usuario como plantilla: vista Backlog
  (Order | Work Item Type | Title | State | Effort | Business Value | Tags)
  y detalle de Task (Description, Area=Habitia, Iteration=Habitia\Seguridad al máximo,
  Priority, Remaining Work, Activity, Blocked).
- AZURE_DEVOPS_DOCUMENTACION.md: + Apendice A (formato exacto), B (Definition
  of Done general y por tipo + reglas Habitia), C (12 Queries Shared con
  filtros y columnas), D (reglas de carga masiva).
- AZURE_DEVOPS_IMPORT.csv: 100 Work Items importables (Epic/Feature/PBI/Task)
  con 12 columnas, Area=Habitia, Iterations=Seguridad al máximo / Core Business
  / Integración IA, States acordes al codigo real, Tags y Activity.
- Verificado: 101 filas (1 cabecera + 100 items), 12 columnas, 0 filas malas.

## CORTE 35 — FIX SYNC GRADLE (JDK 21)

Causa: gradle-daemon-jvm.properties pedia Azul Zulu 21 y JAVA_HOME estaba vacio
(PATH usaba JDK 22). Android Studio intentaba descargar toolchain desde
api.foojay.io y el sync se colgaba. Configuration cache=true agravaba el tema.

Aplicado:
- JAVA_HOME de usuario = C:\Program Files\Java\jdk-21
- Borrado gradle/gradle-daemon-jvm.properties (sin descargas de toolchain)
- gradle.properties: org.gradle.configuration-cache=false
- gradlew --stop + limpieza de .gradle y .kotlin

Verificado: gradlew --version -> Launcher JVM 21.0.7, Daemon JVM jdk-21.
gradlew assembleDebug -> BUILD SUCCESSFUL.

NOTA PARA ANDROID STUDIO: File > Project Structure > Gradle Settings >
Gradle JDK = jdk-21 (no JBR, no 22), luego Sync Project with Gradle Files.

## CORTE 36 — GRADLE BLINDADO (fix definitivo sync)

Ademas del Cort 35 (JAVA_HOME 21, toolchain borrado, config-cache off),
se encontro que Android Studio tenia registrado "zulu-21" (Azul Zulu 21.0.11)
en su jdk.table.xml y al proyecto le faltaba gradleJvm en .idea/gradle.xml.

Solucionado:
- .idea/gradle.xml: <option name="gradleJvm" value="21" /> (el IDE usa JDK 21
  del sistema y no intenta descargar Azul Zulu de api.foojay.io).
- Creado C:\ProyectoAppMoviles\reparar_gradle.ps1: repara JAVA_HOME, borra
  toolchain, desactiva config-cache, fija gradleJvm=21, limpia .gradle/.kotlin
  y verifica JVM. Ejecutable con:
  powershell -ExecutionPolicy Bypass -File C:\ProyectoAppMoviles\reparar_gradle.ps1

Verificado: gradlew assembleDebug -> BUILD SUCCESSFUL; toolchain=False,
config-cache=false, gradleJvm=21.

## CORTE 37 — COLECCION POSTMAN COMPLETA

- Generado C:\ProyectoAppMoviles\Habitia_Postman_Collection.json (v2.1):
  12 carpetas, 68 requests, 16 variables, JWT automatico (request Login
  guarda el token en la variable collection 'token' via test script).
- Variables: baseUrl (IP actual 192.168.100.210), token, tokenType, userId=2
  y todos los ids de path (taskId, habitId, mealId, listId, eventId, etc.).
- Verificado: JSON valido, schema v2.1, 68 requests. Backend arrancado y
  smoke test de 9 GET + 5 POST con los bodies exactos del JSON -> todos OK.
- Datos de prueba limpiados; backend corriendo limpio.

## CORTE 38 — GUÍA COMPLETA DEL PROYECTO

- Creado C:\ProyectoAppMoviles\GUIA_COMPLETA_HABITIA.md: explicacion integral
  para todo publico (analogia restaurante): que es Habitia, las 3 piezas,
  backend hexagonal (103 archivos, 9 modulos, 11 tablas, viaje de peticion),
  frontend KMP (147 archivos, 3 sabores, 4 capas, 15 pantallas), los 4
  pegamentos (ApiClient, SessionManager, HaabiEventBus, Modules), red
  auto-detectada, herramientas de apoyo y checklist de cambios.
- Datos verificados del codigo real antes de escribir.

## CORTE 39 — GUÍA VISUAL PDF + HTML ANIMADO

- GUIA_VISUAL_HABITIA.html: documento con CSS animado (logo latiendo,
  tarjetas apareciendo, pasos flotando), diagramas de capas en color,
  arboles, tablas y flujos. Se abre en navegador y SI tiene movimiento.
- GUIA_VISUAL_HABITIA.pdf: convertido con Chrome headless (--print-to-pdf),
  5 paginas A4, 1.4 MB, con los mismos diagramas en color (la animacion se
  desactiva en @media print, queda el diseno visual estatico).
- GUIA_COMPLETA_HABITIA.md: version textual detallada (corte 38).
- Los 3 archivos se agregaron a crear_zip_limpio.ps1 y ya estan dentro de
  Habitia.zip (3.35 MB, 560 archivos, ruta max 130, 0 sucios).

## CORTE 40 — HTML INTERACTIVO DE ARQUITECTURA KMP

- Creado HABITIA_ARQUITECTURA_INTERACTIVO.html (24 KB): 8 pestañas
  (Resumen, Capas, Flujo animado, Haze, Archivos, Koin, Gemini, Mapa),
  13 nodos clicables, reproductor de 8 pasos con setInterval, demo de vidrio
  con backdrop-filter animado, arbol completo de los 147 archivos reales,
  explicacion de Koin (single vs factory) y de las 7 herramientas de Gemini.
- Verificado: HTML valido, 8 pestañas, 8 pasos JS, cierre correcto.
- Abierto en el panel del navegador y agregado a crear_zip_limpio.ps1.
- ZIP final: 3.36 MB, 561 archivos, ruta max 130, 0 sucios, 4 guias incluidas.

## CORTE 41 — HTML NARRADO COMO PERSONA

- Creado HABITIA_HISTORIA_USUARIO.html: 7 pestañas contadas en primera persona
  ("yo soy Mauricio, vos sos el usuario"): entrar al Home, elegir modulo,
  el viaje del dedo a MySQL (11 paradas clicables + reproductor), quien es
  quien por capas (tarjetas desplegables), todo recaba en el usuario (tabla
  de persistencia), DTOs/mappers/impl explicados como sobre/traductor/empleado
  y Haabi haciendolo todo.
- Abierto en el panel del navegador, agregado al ZIP (3.37 MB, 562 archivos).

## CORTE 42 — GUÍA DEFINITIVA INTERACTIVA (9 procesos + diagramas + porqué)

- Creado HABITIA_GUIA_DEFINITIVA.html: mantiene el reproductor "Contar la historia"
  y lo extiende a 9 procesos (crear tarea, completar hábito, crear comida,
  lista de compras, recordatorio, diario, login, cambiar tema y Haabi),
  cada uno con sus paradas clicables y narración de methods/Impl/DTO/backend.
- Apartados: glosario (UseCase/Interface/Impl/DTO/Mapper/ApiClient/VM/Koin/Haze),
  diagramas dinámicos front+backend, diagrama BD de 11 tablas, quién es quién
  de inicio a fin (11 roles), el PORQUÉ de los 41 cortes, y las 15 pantallas
  con su ViewModel y endpoint real.
- Abierto en navegador y agregado al ZIP (3.38 MB, 563 archivos, 6 guías).

## CORTE 43 — APARTADO 8: CÓMO HACER CUALQUIER CAMBIO (modo historia)

- Agregado a HABITIA_GUIA_DEFINITIVA.html el apartado "🛠️ 8. Cómo hacer cambios"
  con 10 tipos de cambio seleccionables (tamaño, color, mover, endpoint,
  prompt IA, regla, BD, pantalla, notificación y texto), cada uno con su
  viaje animado (paradas + reproductor) que indica ruta exacta, método y
  propagación hasta el usuario.
- Incluye mapa de seguimiento de 16 filas (todas las rutas del proyecto) y
  checklist final de 6 pasos.
- Verificado HTML íntegro y abierto en navegador. ZIP regenerado (3.38 MB).

## CORTE 44 — APARTADO 9 NIVEL CIRUJANO

- Agregado apartado 9 a la guía definitiva: caso real "cambiar color de un
  botón del Home" con Viaje animado + mapa de 15 filas de HomeScreen.kt
  (composable real, línea aproximada, método y color exacto), 4 ámbitos de
  cambio de color, método universal de 7 pasos para localizar cualquier cosa
  y tabla de composables/métodos/conexión de las 11 pantallas.
- Verificado HTML íntegro (70.5 KB), abierto en navegador, ZIP regenerado.

## CORTE 45 — FIX JAVASCRIPT GUÍA DEFINITIVA

- El HTML no permitia navegar porque el script tenia DOS definiciones de
  function parar() y una variable 'raiz' sin declarar en contar3().
- Reemplazado el bloque <script> completo por uno unificado con IIFE:
  3 reproductores (contar, contar2, contar3), un solo parar(), tabs de
  procesos/cambios/diagramas y navegacion principal. Sin var sueltas.
- Verificado: 1 script, 3 contar, 1 parar, 0 constantes duplicadas.
  Abierto en navegador y ZIP regenerado (3.39 MB).

## CORTE 46 — GALERÍA DE 12 CASOS REALES DENTRO DEL APARTADO 9

- Agregada galería "Casos reales de todos los módulos" con 12 casos
  hiper-específicos (color botón Home, tamaño isla/Haabi, categoría Tareas,
  color matriz Eisenhower, catálogo Comidas, botón Tutorial, fuente Diario,
  grid Planner, fila Recordatorio, chat Haabi, campo Perfil, Apariencia).
- Cada caso con paradas animadas + reproductor contar4() que nombra el
  archivo, composable, línea aproximada, método y endpoint exactos.
- Verificado HTML íntegro (91 KB, 1 script, 9 secciones, 4 reproductores),
  abierto en navegador, ZIP regenerado.

## CORTE 47 — MAPAS QUIRÚRGICOS DE TODOS LOS MÓDULOS

- Agregados 12 mapas quirúrgicos (mismo formato que HomeScreen) dentro del
  apartado 9: Tareas, Eisenhower, Comidas, Planner, Alertas, Diario, Haabi,
  Perfil, Apariencia, Focus, Más y Login/Register.
- Cada mapa: lo que ves → composable real + línea → método → color/ruta.
  Datos extraídos del código real con Select-String.
- Navegables por tabs (#tabsMapas). HTML 104.5 KB, 1 script, cierra bien.
  ZIP regenerado (3.39 MB, 563 archivos).

## CORTE 48 — FIX TABS APARTADO 9 (reemplazar, no sumar)

- Habia dos procesos activos por defecto en el apartado 9 (caso home_color y
  mapa task), por eso se apilaban al pasar de modulo.
- Unificada la logica en mostrarSolo(id, grupoTabs): oculta TODOS los
  .proceso del apartado 9 y muestra solo el elegido; tabs de casos y mapas
  comparten la misma funcion. Arranque fuerza un solo proceso activo.
- Verificado HTML integro, abierto en navegador, ZIP regenerado.

## CORTE 49 — UX TABS: SIN SCROLL HACIA ABAJO

- Movidos los tabs de mapas junto a los de casos (arriba del apartado 9).
- .tabs ahora position:sticky top:150px: las pestañas quedan visibles
  siempre aunque bajes por las tablas.
- mostrarSolo() hace scroll automático al inicio del contenido elegido
  (pageYOffset - 168), asi no hay que buscar abajo al cambiar de modulo.
- Verificado HTML integro, abierto en navegador, ZIP regenerado.

## CORTE 50 — TABS LIMPIOS (sin sticky ni auto-scroll)

- Quitado position:sticky de .tabs y el window.scrollTo del cambio de tabs.
- Conservado mostrarSolo(): al elegir un tab se ocultan TODOS los procesos
  del apartado 9 y se muestra solo el elegido (nunca se apila el anterior).
- El unico scrollTo restante es el del nav principal entre apartados.
- Abierto en navegador, ZIP regenerado (3.39 MB, 563 archivos).

## CORTE 51 — 2 COLUMNAS NUEVAS EN TODOS LOS MAPAS QUIRURGICOS

- Agregadas columnas "Para que sirve / Que hace" y "Como cambiar color,
  tamano, forma, tipografia..." a las 14 tablas del apartado 9 (89 filas).
- Cada fila indica archivo real donde cambiar (HomeScreen.kt, Color.kt,
  UiConfig.kt, FoodCategory.kt, Glass.kt, Typography.kt, ApiClient.kt,
  modules/<mod>/application/service ...) segun el caso.
- Verificado HTML integro, abierto en navegador, ZIP regenerado.

## CORTE 52 — MINI-COMENTARIOS // EN TODOS LOS SCREENS

- Agregados comentarios inline en 17 archivos de Presentation (569 comentarios):
  color -> Core/theme/Color.kt (token), tipografia -> fontSize/typography,
  tamano -> Modifier.size/height, forma -> RoundedCornerShape, vidrio ->
  Core/theme/Glass.kt. Estilo corto: "// Color del texto -> Core/theme/Color.kt
  (onSurface) o cambia color= aqui".
- SplashContent.kt comentado a mano: fade/zoom/delay, fondo, logo, titulo,
  subtitulo.
- Verificado compileDebugKotlinAndroid -> BUILD SUCCESSFUL. ZIP regenerado.

## CORTE 53 — POSTMAN COMPLETO + ENVIRONMENT

- Regenerada Habitia_Postman_Collection.json: 68 requests (31 GET, 14 POST,
  10 PUT, 12 DELETE, 1 PATCH) en 12 carpetas, schema v2.1, JWT automatico.
- Creado HABITIA_Postman_Environment.json con 17 variables y 4 valores de
  baseUrl (solo uno activo) para cambiar de red con un clic.
- Ambos archivos incluidos en el ZIP limpio (3.41 MB, 565 archivos).

## CORTE 54 — POSTMAN 100% OK (401 claro + /error permitido + IDs semilla)

- SecurityConfig: agregado authenticationEntryPoint (401 JSON claro en vez
  del 403 engañoso) y permitido /error para que los 404 no se disfracen de 401.
- Verificado: sin token=401 con mensaje, recurso inexistente=404, valido=200.
- Creados datos semilla (habito 7, completion 7, comida 104, ingrediente 89)
  y actualizado HABITIA_Postman_Environment.json con esos IDs.
- Todos los GET de la coleccion devuelven 200. ZIP regenerado.

## CORTE 55 — DOCUMENTACIÓN PUBLICADA EN GITHUB

- Subido index.html (guía definitiva interactiva, 131.5 KB) al repo:
  https://github.com/Reigan-code/Documentation-Habitia.git
- Commit 5de5b56 en rama main; confirmado con git ls-remote (HEAD coincide).
- Nota: el archivo definitivo se llama index.html en C:\ProyectoAppMoviles\.

## CORTE 56 — APK + QR + ZIP COMPLETO + GITHUB

- APK debug compilado y copiado a C:\ProyectoAppMoviles\Habitia-APK-debug.apk (21.22 MB).
- Subido a https://github.com/Reigan-code/Documentation-Habitia.git (commit 213004e).
  Link publico: https://raw.githubusercontent.com/Reigan-code/Documentation-Habitia/main/Habitia-APK-debug.apk
  (verificado HTTP 200, 22.25 MB).
- QR generado (Habitia-APK-QR.png) apuntando al link, subido al repo (commit 6287c14).
- ZIP completo C:\ProyectoAppMoviles\Habitia-Completo.zip: 23.84 MB, 571 archivos,
  ruta max 144 caracteres, sin artefactos. Incluye backend, frontend, guias,
  Postman, APK y QR.

## CORTE 57 — RAMAS DEV POR INTEGRANTE + DOCUMENTO DE GIT

- Leido Habitia_Backend_Division_Equipo.docx (842 lineas) para mapear RF-01..RF-14
  y responsables reales (Mauricio, Andrea, Mayler, Sergio).
- Creadas 14 ramas dev en HABITIA-Back: dev/<integrante>-rfNN-<tarea>.
  Nota: git no permite dev/mauricio y dev/mauricio/rf-01 a la vez, por eso
  se uso formato plano.
- Creado HABITIA_GIT_EQUIPO.md con: regla de oro, mapa de responsables,
  las 14 ramas, plan de commits por tiempo/tipo para cada integrante,
  comandos de trabajo y checklist de merge.
- Subido al repo GitHub (commit 7d4da18) e incluido en el ZIP limpio.

## CORTE 58 — RAMAS DEV FINALES (4) + COMMITS POR INTEGRANTE + README PROFESIONAL

- Backend: eliminadas las 14 ramas RF y recreadas SOLO 4: dev/mauricio,
  dev/andrea, dev/mayler, dev/sergio desde main (estado hexagonal real).
- Commiteado el estado real del proyecto en main (84dd722) y luego un commit
  propio por rama con autor real: f65217c (Mauricio), 7ff3614 (Andrea),
  9399177 (Mayler), 8b05598 (Sergio), cada uno con APORTES_<NOMBRE>.md.
- Creado README_HABITIA_PROFESIONAL.md (309 lineas): vision, arquitectura
  backend/frontend, esquemas de BD con relaciones, modulos, Haabi, estrategia
  Git con aportes, guia de ejecucion y estado real/pendientes.
- Publicado como README.md en GitHub (commit b1243f1) e incluido en el ZIP.
