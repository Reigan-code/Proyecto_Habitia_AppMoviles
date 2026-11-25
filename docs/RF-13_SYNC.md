# RF-13 — Sincronización entre dispositivos

**Responsable:** Sergio · **Rama:** `dev/sergio` · **Estado:** diseño documentado, pendiente de implementar.

## Base existente
La persistencia local ya funciona con DataStore (`SessionManager` y
`UserPreferencesRepository`). Es la base sobre la que se construirá la sincronización.

## Contrato propuesto

```java
public class SyncPushRequest {
    private String deviceId;
    private List<SyncEntityDto> tasks;
    private List<SyncEntityDto> habits;
    private List<SyncEntityDto> meals;
    private Instant lastSync;
}

public class SyncPullRequest {
    private String deviceId;
    private Instant lastSync;
}

public enum SyncStatus { OK, CONFLICT, PARTIAL }
```

## Endpoints
```
POST /sync/push   → SyncStatus
POST /sync/pull   → SyncPushRequest (delta desde lastSync)
```

## Estrategia de conflictos
1. Comparar `updatedAt` del servidor vs. `lastSync` del dispositivo.
2. Si el servidor es más reciente, gana el servidor y se reporta `CONFLICT`.
3. Si el dispositivo es más reciente, se aplica el push y se reporta `PARTIAL`.

## Criterios de aceptación
- [ ] Idempotente: repetir un push con el mismo `lastSync` no duplica datos.
- [ ] El delta nunca incluye entidades borradas antes de `lastSync`.
