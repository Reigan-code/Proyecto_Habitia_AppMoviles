
package upeu.edu.pe.HABITIA_Back.modules.meal.application.dto;

// Enum que representa el tipo de comida
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.MealType;

import java.time.LocalDate; // Fecha de la comida
import java.time.LocalTime; // Hora de la comida

// DTO que define los datos que el backend devuelve sobre una comida
public class MealsResponseDto {

    // Identificador único de la comida
    private Long id;

    // Identificador del usuario propietario de la comida
    private Long userId;

    // Nombre de la comida
    private String name;

    // Tipo de comida: depende de los valores de MealType
    private MealType type;

    // Fecha en la que está programada
    private LocalDate date;

    // Hora programada; puede ser null si no se especificó
    private LocalTime time;

    // Descripción adicional de la comida
    private String description;

    // Indica si la comida está completada
    private Boolean completed;

    // Constructor: permite crear una respuesta con todos los datos
    public MealsResponseDto(
            Long id,
            Long userId,
            String name,
            MealType type,
            LocalDate date,
            LocalTime time,
            String description,
            Boolean completed) {

        // Copia cada parámetro recibido al atributo correspondiente
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.date = date;
        this.time = time;
        this.description = description;
        this.completed = completed;
    }

    // Devuelve el ID de la comida
    public Long getId() {
        return id;
    }

    // Devuelve el ID del usuario propietario
    public Long getUserId() {
        return userId;
    }

    // Devuelve el nombre
    public String getName() {
        return name;
    }

    // Devuelve el tipo de comida
    public MealType getType() {
        return type;
    }

    // Devuelve la fecha
    public LocalDate getDate() {
        return date;
    }

    // Devuelve la hora
    public LocalTime getTime() {
        return time;
    }

    // Devuelve la descripción
    public String getDescription() {
        return description;
    }

    // Devuelve si está completada
    public Boolean getCompleted() {
        return completed;
    }
}
