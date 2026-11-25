
package upeu.edu.pe.HABITIA_Back.modules.meal.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MealIngredientCreateDto {

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "El ingrediente no puede superar 255 caracteres")
    private String description;

    @NotNull(message = "El ID de la comida es obligatorio")
    @Positive(message = "El ID de la comida debe ser positivo")
    private Long mealId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMealId() {
        return mealId;
    }

    public void setMealId(Long mealId) {
        this.mealId = mealId;
    }
}
