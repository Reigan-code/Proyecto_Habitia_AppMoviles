
package upeu.edu.pe.HABITIA_Back.modules.meal.domain.model;

import jakarta.persistence.*;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.IngredientUnit;

import java.math.BigDecimal;

@Entity
@Table(name = "Meal_Ingredients")
public class MealIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "id_meal",
            referencedColumnName = "id",
            nullable = false
    )
    private Meals meals;

    @Column(length = 255)
    private String description;

    @Column(precision = 10, scale = 3)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private IngredientUnit unit;

    public MealIngredient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Meals getMeals() {
        return meals;
    }

    public void setMeals(Meals meals) {
        this.meals = meals;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public IngredientUnit getUnit() {
        return unit;
    }

    public void setUnit(IngredientUnit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "MealIngredient{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unit=" + unit +
                '}';
    }
}
