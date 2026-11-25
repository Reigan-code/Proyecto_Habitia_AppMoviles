package upeu.edu.pe.HABITIA_Back.modules.summary.application.dto;

import java.time.LocalDate;

public record DailySummaryResponse(
        LocalDate date,
        int tasksTotal,
        int tasksCompleted,
        int habitsTotal,
        int habitsCompleted,
        int streak,
        double progressPercent
) {}
