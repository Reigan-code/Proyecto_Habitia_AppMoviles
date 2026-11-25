# 🤝 Contribuir a Habitia

## Ramas

- `main` → código integrado y compilado.
- `dev/<nombre>` → rama de trabajo de cada integrante (`mauricio`, `andrea`, `mayler`, `sergio`).

## Commits

Formato:

```
RF-NN <tipo>: <descripción corta en imperativo>
```

Tipos: `feat` (nueva funcionalidad), `fix` (corrección), `test`, `docs`, `refactor`.

Ejemplos reales:

```
RF-03 feat: TaskController con CRUD y filtros por usuario
RF-12 fix: EventBus refresca la UI tras crear una tarea desde Haabi
RF-04 docs: árbol de trabajo de hábitos y rachas
```

**Regla:** un commit = un cambio lógico. Si tocás dos módulos, hacé dos commits.

## Identidad

Cada integrante configura su identidad **en su propia máquina**:

```powershell
git config user.name "Andrea"
git config user.email "andrea@habitia.pe"
```

O usa el helper (raíz del repo):

```powershell
.\scripts\commit_integrante.ps1 -Nombre "Andrea" -Email "andrea@habitia.pe" `
  -Mensaje "RF-03 feat: filtros por estado en TaskController" `
  -Archivos @("HABITIA-Back/src/main/java/upeu/edu/pe/HABITIA_Back/modules/task")
```

> No compartas contraseñas ni tokens de GitHub. Tu identidad de autor es tuya.

## Pull Request

1. Push de tu rama `dev/<nombre>`.
2. Abrí PR a `main` usando la plantilla `.github/PULL_REQUEST_TEMPLATE.md`.
3. El responsable del módulo (`CODEOWNERS`) revisa y aprueba.
4. Merge a `main`.

## Antes de commitear

- Backend compila: `.\mvnw.cmd -q compile`
- Frontend compila: `.\gradlew assembleDebug`
- Sin claves API en el código (ver sección 9 del README).
- Sin artefactos: `.gradle/`, `build/`, `target/`, `*.log`, `.opencode/`.
