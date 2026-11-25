
package upeu.edu.pe.HABITIA_Back.modules.task.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.Priority;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.TaskStatus;

import java.time.LocalDate;

public class TaskUpdateDto {

    // El título debe tener contenido y no superar 150 caracteres.
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar 150 caracteres")
    private String title;

    private String description;

    // La fecha límite es opcional.
    private LocalDate dueDate;

    // La prioridad es obligatoria al actualizar.
    @NotNull(message = "La prioridad es obligatoria")
    private Priority priority;

    // El estado también debe estar presente.
    @NotNull(message = "El estado es obligatorio")
    private TaskStatus status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
