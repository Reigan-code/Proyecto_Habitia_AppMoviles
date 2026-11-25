
package upeu.edu.pe.HABITIA_Back.modules.meal.application.service;
//ingredientes de un plato: convierte texto a cantidad+unidad; si no entiende pues 400

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealIngredientRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealsRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.service.InterpreteDeIngredientes;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.MealIngredient;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.IngredientUnit;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MealIngredientImpl implements MealIngredientService {

    private final MealIngredientRepository mealIngredientRepository;
    private final MealsRepository mealsRepository;
    private final InterpreteDeIngredientes InterpreteDeIngredientes;

    public MealIngredientImpl(MealIngredientRepository mealIngredientRepository, MealsRepository mealsRepository, InterpreteDeIngredientes interpreteDeIngredientes) {
        this.mealIngredientRepository = mealIngredientRepository;
        this.mealsRepository = mealsRepository;
        InterpreteDeIngredientes = interpreteDeIngredientes;
    }

    @Override
    public MealIngredient crear(MealIngredient ingredient) {
        interpretar(ingredient);
        return mealIngredientRepository.save(ingredient);
    }

    @Override
    public Optional<MealIngredient> consultar(Long ingredientId) {
        return mealIngredientRepository.findById(ingredientId);
    }

    @Override
    public MealIngredient update(MealIngredient ingredient) {
        interpretar(ingredient);
        return mealIngredientRepository.save(ingredient);
    }

    @Override
    public void delete(Long ingredientId) {
        mealIngredientRepository.deleteById(ingredientId);
    }

    @Override
    public List<MealIngredient> findByMeal(Long mealId) {
        return mealIngredientRepository.findByMealsId(mealId);
    }

    public boolean existeComida(Long mealId) {
        return mealsRepository.existsById(mealId);
    }

    private void interpretar(MealIngredient ingredient) {

        // Interpreta la descripción del ingrediente y separa
        // su nombre, cantidad y unidad de medida.
        InterpreteDeIngredientes.ParsedIngredient parsed =
                InterpreteDeIngredientes.parse(ingredient.getDescription());

        // Guarda el nombre limpio del ingrediente.
        ingredient.setDescription(parsed.description());

        // Guarda la cantidad identificada.
        ingredient.setQuantity(parsed.quantity());

        // Guarda la unidad de medida identificada.
        ingredient.setUnit(parsed.unit());
    }
}
