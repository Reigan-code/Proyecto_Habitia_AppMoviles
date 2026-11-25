
package upeu.edu.pe.HABITIA_Back.modules.habit.application.dto;

import upeu.edu.pe.HABITIA_Back.shared.domain.enums.Frequency;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.Habit;

import java.time.LocalDate;

public class HabitResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Frequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;

    public HabitResponseDTO() {
    }

    // Convierte la entidad JPA en el DTO que recibe el cliente.
    public static HabitResponseDTO fromEntity(Habit habit) {
        HabitResponseDTO dto = new HabitResponseDTO();

        dto.setId(habit.getId());
        dto.setUserId(habit.getUser().getId());
        dto.setName(habit.getName());
        dto.setDescription(habit.getDescription());
        dto.setFrequency(habit.getFrequency());
        dto.setStartDate(habit.getStartDate());
        dto.setEndDate(habit.getEndDate());
        dto.setActive(habit.getActive());

        return dto;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
