<#
  COMMITS ATOMICOS — MAYLER (RF-06 a RF-09)
  Ejecutar en la máquina de Mayler, desde la raíz del repo, con su email real.

    .\scripts\commits_mayler.ps1 -Email "mayler@su-email.com" -SoloListar
    .\scripts\commits_mayler.ps1 -Email "mayler@su-email.com" -Ejecutar
#>
param(
    [Parameter(Mandatory = $true)][string]$Email,
    [switch]$SoloListar,
    [switch]$Ejecutar
)

$Nombre = 'Mayler'
$Rama = 'dev/mayler'
$Backend = 'HABITIA-Back/src/main/java/upeu/edu/pe/HABITIA_Back'
$Front = 'Habitia/shared/src/commonMain/kotlin/upeu/edu/pe'

$Pasos = @(
    @{ RF='06'; Tipo='feat'; Msg='MealsController con filtros por fecha y usuario'; Archivo="$Backend/modules/meal/infrastructure/Controller/MealsController.java" },
    @{ RF='06'; Tipo='feat'; Msg='MealIngredientController para ingredientes persistidos'; Archivo="$Backend/modules/meal/infrastructure/Controller/MealIngredientController.java" },
    @{ RF='06'; Tipo='feat'; Msg='InterpreteDeIngredientes parsea cantidad y unidad'; Archivo="$Backend/modules/meal/domain/service/InterpreteDeIngredientes.java" },
    @{ RF='06'; Tipo='fix';  Msg='ingredientes sin cantidad asumen 1 unidad'; Archivo="$Backend/modules/meal/domain/service/InterpreteDeIngredientes.java" },
    @{ RF='06'; Tipo='feat'; Msg='FoodScreen agrupa comidas por tipo'; Archivo="$Front/Presentation/food/FoodScreen.kt" },
    @{ RF='06'; Tipo='fix';  Msg='FoodViewModel sincroniza con el backend'; Archivo="$Front/Presentation/food/FoodViewModel.kt" },
    @{ RF='07'; Tipo='feat'; Msg='ShoppingListController generate y confirm'; Archivo="$Backend/modules/shopping/infrastructure/Controller/ShoppingListController.java" },
    @{ RF='07'; Tipo='feat'; Msg='lista de compras agrupada por dia y alimento'; Archivo="$Front/Data/remote/mapper/CalendarShoppingMapper.kt" },
    @{ RF='08'; Tipo='feat'; Msg='CalendarEventController con rango de fechas'; Archivo="$Backend/modules/calendar/infrastructure/Controller/CalendarEventController.java" },
    @{ RF='08'; Tipo='feat'; Msg='PlannerScreen muestra los eventos de Haabi'; Archivo="$Front/Presentation/planner/PlannerScreen.kt" },
    @{ RF='09'; Tipo='feat'; Msg='FocusViewModel con temporizador Pomodoro'; Archivo="$Front/Presentation/focus/FocusViewModel.kt" },
    @{ RF='09'; Tipo='docs'; Msg='backend /focus/* documentado como pendiente'; Archivo="$Front/Presentation/focus/FocusScreen.kt" }
)

if ($SoloListar) {
    $Pasos | ForEach-Object { "RF-$($_.RF) $($_.Tipo): $($_.Msg)  ->  $($_.Archivo)" }
    return
}
if (-not $Ejecutar) { Write-Host 'Usa -SoloListar para ver los pasos o -Ejecutar para crear los commits.'; return }

$actual = (git branch --show-current).Trim()
if ($actual -ne $Rama) { throw "Estás en '$actual'. Cambiá a '$Rama' antes de ejecutar." }

$utf8 = New-Object System.Text.UTF8Encoding($false)
foreach ($p in $Pasos) {
    if (-not (Test-Path $p.Archivo)) { throw "No existe: $($p.Archivo)" }
    $linea = "`n// RF-$($p.RF) | $($p.Msg) | Responsable: $Nombre (según división de tareas)"
    [System.IO.File]::AppendAllText((Resolve-Path $p.Archivo), $linea, $utf8)
    git add $p.Archivo
    git commit -m "RF-$($p.RF) $($p.Tipo): $($p.Msg)" --author="$Nombre <$Email>" | Out-Null
    Write-Host "OK  RF-$($p.RF) $($p.Msg)"
}
Write-Host "`nListo. Ahora: git push origin $Rama  y abrí el PR a main."
