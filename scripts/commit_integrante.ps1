<#
  Helper de commit para cada integrante de Habitia.
  Se ejecuta en la máquina del integrante, con SU identidad real.
  No guarda contraseñas ni tokens.

  Uso:
    .\scripts\commit_integrante.ps1 -Nombre "Andrea" -Email "andrea@habitia.pe" `
      -Mensaje "RF-03 feat: filtros por estado en TaskController" `
      -Archivos @("HABITIA-Back/src/main/java/upeu/edu/pe/HABITIA_Back/modules/task")
#>
param(
    [Parameter(Mandatory = $true)][string]$Nombre,
    [Parameter(Mandatory = $true)][string]$Email,
    [Parameter(Mandatory = $true)][string]$Mensaje,
    [Parameter(Mandatory = $true)][string[]]$Archivos
)

$ErrorActionPreference = 'Stop'
git add $Archivos
if ($LASTEXITCODE -ne 0) { throw 'git add falló' }

git commit -m $Mensaje --author="$Nombre <$Email>"
if ($LASTEXITCODE -ne 0) { throw 'git commit falló' }

Write-Host "Commit creado por $Nombre <$Email>"
Write-Host "Recordatorio: haz push a tu rama dev/<nombre> y abre un PR a main."
