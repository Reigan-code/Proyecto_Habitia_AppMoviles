# RF-10 — Resumen diario

**Responsable:** Sergio · **Rama:** `dev/sergio` · **Estado:** implementado en frontend, backend pendiente.

## Estado actual
`HomeViewModel.updateProgress()` combina las tareas de hoy (`GetTodayTasksUseCase`)
con los hábitos (`GetHabitsUseCase`) y calcula el progreso mostrado en `HomeScreen`.

## Diseño del endpoint pendiente

```
GET /summary/daily?userId={id}&date={yyyy-MM-dd}
GET /summary/pending?userId={id}
GET /summary/habits-progress?userId={id}&date={yyyy-MM-dd}
```

### DTO propuesto
```java
public class DailySummaryResponse {
    private LocalDate date;
    private int tasksTotal;
    private int tasksCompleted;
    private int habitsTotal;
    private int habitsCompleted;
    private int streak;
    private double progressPercent;
}
```

## Criterios de aceptación
- [ ] El progreso coincide con el calculado hoy en el front.
- [ ] La racha usa `Habit_complet` (mismo criterio que `computeStreak()`).
- [ ] Sin tareas ni hábitos devuelve `progressPercent = 0`, nunca null.
