
package upeu.edu.pe.HABITIA_Back.modules.meal.application.service;

import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.MealIngredient;

import java.util.List;
import java.util.Optional;

public interface MealIngredientService {
    MealIngredient crear(MealIngredient ingredient);
    Optional<MealIngredient> consultar(Long ingredientId);
    MealIngredient update(MealIngredient ingredient);
    void delete(Long ingredientId);
    List<MealIngredient> findByMeal(Long mealId);
}
