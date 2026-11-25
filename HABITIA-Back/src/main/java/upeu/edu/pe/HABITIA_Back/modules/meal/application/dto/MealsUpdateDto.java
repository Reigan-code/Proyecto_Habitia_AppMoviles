
package upeu.edu.pe.HABITIA_Back.modules.meal.application.dto;

// Validaciones para comprobar los datos recibidos
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Enum que define los tipos de comida permitidos
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.MealType;

import java.time.LocalDate; // Representa una fecha sin hora
import java.time.LocalTime; // Representa una hora sin fecha

// DTO que recibe los datos para actualizar una comida
public class MealsUpdateDto {

    // El nombre es obligatorio y no puede estar vacío ni contener solo espacios
    @NotBlank(message = "El nombre de la comida es obligatorio")
    private String name;

    // El tipo de comida es obligatorio
    @NotNull(message = "El tipo de comida es obligatorio")
    private MealType type;

    // La fecha de la comida es obligatoria
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    // Hora de la comida. Puede ser null si no se especifica
    private LocalTime time;

    // Descripción adicional de la comida. Es opcional
    private String description;

    // Indica si la comida está completada.
    // Boolean permite true, false o null
    private Boolean completed;

    // Devuelve el nombre de la comida
    public String getName() {
        return name;
    }

    // Asigna el nombre recibido
    public void setName(String name) {
        this.name = name;
    }

    // Devuelve el tipo de comida
    public MealType getType() {
        return type;
    }

    // Asigna el tipo de comida
    public void setType(MealType type) {
        this.type = type;
    }

    // Devuelve la fecha de la comida
    public LocalDate getDate() {
        return date;
    }

    // Asigna la fecha de la comida
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Devuelve la hora de la comida
    public LocalTime getTime() {
        return time;
    }

    // Asigna la hora de la comida
    public void setTime(LocalTime time) {
        this.time = time;
    }

    // Devuelve la descripción
    public String getDescription() {
        return description;
    }

    // Asigna la descripción
    public void setDescription(String description) {
        this.description = description;
    }

    // Devuelve si la comida está completada
    public Boolean getCompleted() {
        return completed;
    }

    // Asigna el estado de completado
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
