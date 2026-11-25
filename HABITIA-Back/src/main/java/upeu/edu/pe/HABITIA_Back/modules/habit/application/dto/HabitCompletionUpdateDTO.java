package upeu.edu.pe.HABITIA_Back.modules.habit.application.dto;

import jakarta.validation.constraints.NotNull;

public class HabitCompletionUpdateDTO {

    @NotNull(message = "El estado de cumplimiento es obligatorio")
    private Boolean completed;

    public HabitCompletionUpdateDTO() {
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}