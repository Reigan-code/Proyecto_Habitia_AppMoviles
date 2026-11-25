
package upeu.edu.pe.HABITIA_Back.modules.meal.application.dto;

import upeu.edu.pe.HABITIA_Back.shared.domain.enums.IngredientUnit;

import java.math.BigDecimal;

public class MealIngredientResponseDto {

    private Long id;
    private String description;
    private Long mealId;
    private BigDecimal quantity;
    private IngredientUnit unit;

    public MealIngredientResponseDto(
            Long id,
            String description,
            Long mealId,
            BigDecimal quantity,
            IngredientUnit unit) {
        this.id = id;
        this.description = description;
        this.mealId = mealId;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getMealId() {
        return mealId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public IngredientUnit getUnit() {
        return unit;
    }
}
