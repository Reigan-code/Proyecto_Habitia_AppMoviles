package upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.MealIngredient;

import java.util.List;

public interface MealIngredientRepository extends JpaRepository<MealIngredient, Long> {
    List<MealIngredient> findByMealsId(Long mealId);
    
}
