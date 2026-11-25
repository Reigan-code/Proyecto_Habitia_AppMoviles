
package upeu.edu.pe.HABITIA_Back.modules.habit.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class HabitCompletionRequestDTO {

    @NotNull(message = "El ID del hábito es obligatorio")
    private Long habitId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "El estado de cumplimiento es obligatorio")
    private Boolean completed;

    public HabitCompletionRequestDTO() {
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
