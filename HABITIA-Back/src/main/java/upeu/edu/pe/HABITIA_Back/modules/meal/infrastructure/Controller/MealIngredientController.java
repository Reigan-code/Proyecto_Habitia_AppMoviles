
package upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.Controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.meal.application.dto.MealIngredientCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.meal.application.dto.MealIngredientResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.meal.application.dto.MealIngredientUpdateDto;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealsRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.application.service.MealIngredientService;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.MealIngredient;

import java.net.URI;
import java.util.List;

@RestController

@RequestMapping("/MealIngredients")
public class MealIngredientController {

    private final MealIngredientService mealIngredientService;

    private final MealsRepository mealsRepository;

    private final Logger log =
            LoggerFactory.getLogger(MealIngredientController.class);

    public MealIngredientController(
            MealIngredientService mealIngredientService,
            MealsRepository mealsRepository) {
        this.mealIngredientService = mealIngredientService;
        this.mealsRepository = mealsRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealIngredientResponseDto> get(
            @PathVariable Long id) {

        return mealIngredientService.consultar(id)
                .map(ingredient ->
                        ResponseEntity.ok(toResponseDTO(ingredient)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //ingredientes de un plato; sino pues vacio
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<MealIngredientResponseDto>> getByMeal(
            @PathVariable Long mealId) {

        if (!mealsRepository.existsById(mealId)) {
            return ResponseEntity.notFound().build();
        }

        List<MealIngredientResponseDto> response =
                mealIngredientService.findByMeal(mealId)
                        .stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }

    //agrega ingrediente al plato, funciona si es tuyo; sino pues 404
    @PostMapping
    public ResponseEntity<?> post(
            @Valid @RequestBody MealIngredientCreateDto dto) {

        log.info("POST: creating meal ingredient");

        var meal = mealsRepository.findById(dto.getMealId());

        if (meal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MealIngredient ingredient = new MealIngredient();
        ingredient.setDescription(dto.getDescription());
        ingredient.setMeals(meal.get());

        MealIngredient created = mealIngredientService.crear(ingredient);

        return ResponseEntity.created(
                URI.create("/habitia-backend/MealIngredients/"
                        + created.getId())
        ).body(toResponseDTO(created));
    }

    //edita el ingrediente; sino pues 404
    @PutMapping("/{id}")
    public ResponseEntity<MealIngredientResponseDto> put(
            @PathVariable Long id,
            @Valid @RequestBody MealIngredientUpdateDto dto) {

        return mealIngredientService.consultar(id)
                .map(existing -> {

                    existing.setDescription(dto.getDescription());

                    MealIngredient updated =
                            mealIngredientService.update(existing);

                    return ResponseEntity.ok(toResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //quita el ingrediente; sino pues 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (mealIngredientService.consultar(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        mealIngredientService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private MealIngredientResponseDto toResponseDTO(
            MealIngredient ingredient) {

        return new MealIngredientResponseDto(
                ingredient.getId(),
                ingredient.getDescription(),
                ingredient.getMeals().getId(),
                ingredient.getQuantity(),
                ingredient.getUnit()
        );
    }
}
