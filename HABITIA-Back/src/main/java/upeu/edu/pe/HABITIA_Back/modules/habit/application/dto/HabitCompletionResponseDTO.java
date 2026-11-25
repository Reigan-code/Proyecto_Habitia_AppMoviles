
package upeu.edu.pe.HABITIA_Back.modules.habit.application.dto;

import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.HabitCompletion;
import java.time.LocalDate;

public class HabitCompletionResponseDTO {

    private Long id;
    private Long habitId;
    private LocalDate date;
    private Boolean completed;

    public HabitCompletionResponseDTO() {
    }

    // Convierte la entidad en una respuesta segura y sencilla.
    public static HabitCompletionResponseDTO fromEntity(
            HabitCompletion completion) {

        HabitCompletionResponseDTO dto =
                new HabitCompletionResponseDTO();

        dto.setId(completion.getId());
        dto.setHabitId(completion.getHabit().getId());
        dto.setDate(completion.getDate());
        dto.setCompleted(completion.getCompleted());

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
