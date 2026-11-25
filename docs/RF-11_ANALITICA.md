# RF-11 — Analítica de datos

**Responsable:** Sergio · **Rama:** `dev/sergio` · **Estado:** diseño documentado, pendiente de implementar.

## Endpoints propuestos

```
GET /analytics/trends?userId={id}&from={date}&to={date}
GET /analytics/weekly?userId={id}
GET /analytics/balance?userId={id}
```

## Servicio y consultas JPQL

```java
@Service
public class AnalyticsService {
    // SELECT COUNT(t) FROM Task t WHERE t.user.id = :id AND t.status = :status
    // SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.habit.user.id = :id AND hc.date BETWEEN :from AND :to
    // SELECT m.type, COUNT(m) FROM Meals m WHERE m.user.id = :id GROUP BY m.type
}
```

## DTOs propuestos
```java
TrendPointDto     { LocalDate date; int completed; int created; }
WeeklySummaryDto  { int tasksDone; int habitsDone; int mealsLogged; }
BalanceDto        { double productivity; double wellness; double balance; }
```

## Criterios de aceptación
- [ ] Las agregaciones se hacen en SQL (JPQL), no en memoria.
- [ ] Fechas nulas o rango invertido devuelven 400 con mensaje claro.
- [ ] Sin datos devuelve listas vacías, nunca null.
