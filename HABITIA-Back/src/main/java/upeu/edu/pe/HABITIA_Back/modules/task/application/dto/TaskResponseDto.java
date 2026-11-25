
package upeu.edu.pe.HABITIA_Back.modules.task.application.dto;

import upeu.edu.pe.HABITIA_Back.shared.domain.enums.Priority;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.TaskStatus;

import java.time.LocalDate;

public class TaskResponseDto {

    // Se devuelve el ID de usuario, no la entidad relacionada.
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private TaskStatus status;

    public TaskResponseDto() {
    }

    public TaskResponseDto(
            Long id,
            Long userId,
            String title,
            String description,
            LocalDate dueDate,
            Priority priority,
            TaskStatus status) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
