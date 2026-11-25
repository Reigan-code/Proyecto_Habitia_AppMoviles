
package upeu.edu.pe.HABITIA_Back.modules.meal.application.dto;

// Validaciones para comprobar los datos recibidos
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO que recibe los datos para actualizar un ingrediente de una comida
public class MealIngredientUpdateDto {

    // La descripción es obligatoria.
    // No puede ser null, estar vacía ni contener solamente espacios.
    @NotBlank(message = "La descripción es obligatoria")

    // Limita la descripción a un máximo de 255 caracteres.
    @Size(max = 255, message = "El ingrediente no puede superar 255 caracteres")
    private String description;

    // Devuelve la descripción recibida
    public String getDescription() {
        return description;
    }

    // Asigna la nueva descripción
    public void setDescription(String description) {
        this.description = description;
    }
}
