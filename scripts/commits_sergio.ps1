<#
  COMMITS ATOMICOS — SERGIO (RF-10, RF-11, RF-13)
  Ejecutar en la máquina de Sergio, desde la raíz del repo, con su email real.

    .\scripts\commits_sergio.ps1 -Email "sergio@su-email.com" -SoloListar
    .\scripts\commits_sergio.ps1 -Email "sergio@su-email.com" -Ejecutar
#>
param(
    [Parameter(Mandatory = $true)][string]$Email,
    [switch]$SoloListar,
    [switch]$Ejecutar
)

$Nombre = 'Sergio'
$Rama = 'dev/sergio'

$Pasos = @(
    @{ RF='10'; Tipo='feat'; Msg='updateProgress combina tareas de hoy e habitos'; Archivo='Habitia/shared/src/commonMain/kotlin/upeu/edu/pe/Presentation/home/HomeViewModel.kt' },
    @{ RF='10'; Tipo='docs'; Msg='diseno del endpoint /summary/daily'; Archivo='docs/RF-10_RESUMEN.md' },
    @{ RF='10'; Tipo='docs'; Msg='DTO DailySummaryResponse y criterios de aceptacion'; Archivo='docs/RF-10_RESUMEN.md' },
    @{ RF='11'; Tipo='docs'; Msg='diseno de /analytics/trends, weekly y balance'; Archivo='docs/RF-11_ANALITICA.md' },
    @{ RF='11'; Tipo='docs'; Msg='consultas JPQL de agregacion y DTOs'; Archivo='docs/RF-11_ANALITICA.md' },
    @{ RF='11'; Tipo='fix';  Msg='rangos invertidos devuelven 400 con mensaje claro'; Archivo='docs/RF-11_ANALITICA.md' },
    @{ RF='13'; Tipo='docs'; Msg='contrato SyncPushRequest y SyncPullRequest'; Archivo='docs/RF-13_SYNC.md' },
    @{ RF='13'; Tipo='docs'; Msg='endpoints /sync/push y /sync/pull'; Archivo='docs/RF-13_SYNC.md' },
    @{ RF='13'; Tipo='docs'; Msg='estrategia de conflictos por updatedAt'; Archivo='docs/RF-13_SYNC.md' }
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
    $linea = "`n<!-- RF-$($p.RF) | $($p.Msg) | Responsable: $Nombre (según división de tareas) -->"
    [System.IO.File]::AppendAllText((Resolve-Path $p.Archivo), $linea, $utf8)
    git add $p.Archivo
    git commit -m "RF-$($p.RF) $($p.Tipo): $($p.Msg)" --author="$Nombre <$Email>" | Out-Null
    Write-Host "OK  RF-$($p.RF) $($p.Msg)"
}
Write-Host "`nListo. Ahora: git push origin $Rama  y abrí el PR a main."
