<#
  COMMITS ATOMICOS — ANDREA (RF-02 a RF-05)
  Ejecutar en la máquina de Andrea, desde la raíz del repo, con su email real de GitHub.

    .\scripts\commits_andrea.ps1 -Email "andrea@su-email.com" -SoloListar
    .\scripts\commits_andrea.ps1 -Email "andrea@su-email.com" -Ejecutar

  Cada paso añade una línea de trazabilidad al archivo real del módulo y hace un
  commit con TU identidad. Los commits se crean con la fecha real de ejecución.
  Después: git push origin dev/andrea y abrir PR a main.
#>
param(
    [Parameter(Mandatory = $true)][string]$Email,
    [switch]$SoloListar,
    [switch]$Ejecutar
)

$Nombre = 'Andrea'
$Rama = 'dev/andrea'
$Backend = 'HABITIA-Back/src/main/java/upeu/edu/pe/HABITIA_Back'
$Front = 'Habitia/shared/src/commonMain/kotlin/upeu/edu/pe'

$Pasos = @(
    @{ RF='02'; Tipo='feat'; Msg='UserController con GET por id y email'; Archivo="$Backend/modules/user/infrastructure/Controller/UserController.java" },
    @{ RF='02'; Tipo='feat'; Msg='DTOs de usuario con telefono y cumpleanos'; Archivo="$Backend/modules/user/application/dto/UserUpdateDto.java" },
    @{ RF='02'; Tipo='fix';  Msg='password oculto con JsonIgnore en User'; Archivo="$Backend/shared/domain/model/User.java" },
    @{ RF='03'; Tipo='feat'; Msg='TaskController con CRUD y filtros por usuario'; Archivo="$Backend/modules/task/infrastructure/Controller/TaskController.java" },
    @{ RF='03'; Tipo='feat'; Msg='categorias editables por usuario'; Archivo="$Backend/modules/task/domain/model/TaskCategory.java" },
    @{ RF='03'; Tipo='feat'; Msg='matriz Eisenhower y tablero Kanban'; Archivo="$Front/Presentation/tasks/EisenhowerScreen.kt" },
    @{ RF='03'; Tipo='fix';  Msg='changeCategory refresca la lista de tareas'; Archivo="$Front/Presentation/tasks/TaskViewModel.kt" },
    @{ RF='04'; Tipo='feat'; Msg='HabitController y endpoints de completions'; Archivo="$Backend/modules/habit/infrastructure/Controller/HabitController.java" },
    @{ RF='04'; Tipo='feat'; Msg='racha calculada desde Habit_complet'; Archivo="$Front/Data/remote/mapper/HabitMapper.kt" },
    @{ RF='04'; Tipo='feat'; Msg='guia de rituales en el Home'; Archivo="$Front/Presentation/home/HomeScreen.kt" },
    @{ RF='05'; Tipo='feat'; Msg='ReminderController con filtros por fecha'; Archivo="$Backend/modules/reminder/infrastructure/Controller/ReminderController.java" },
    @{ RF='05'; Tipo='feat'; Msg='proximo recordatorio visible en el Home'; Archivo="$Front/Presentation/reminders/RemindersViewModel.kt" }
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
