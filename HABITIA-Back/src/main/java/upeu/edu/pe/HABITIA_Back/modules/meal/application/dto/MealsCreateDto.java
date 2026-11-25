package upeu.edu.pe.HABITIA_Back.modules.meal.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.MealType;

import java.time.LocalDate;
import java.time.LocalTime;

public class MealsCreateDto {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotBlank(message = "El nombre de la comida es obligatorio")
    private String name;

    @NotNull(message = "El tipo de comida es obligatorio")
    private MealType type;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    private LocalTime time;
    private String description;
    private Boolean completed;

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

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}