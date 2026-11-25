
package upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.Controller;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;


import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upeu.edu.pe.HABITIA_Back.modules.meal.application.dto.MealsCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.meal.application.dto.MealsResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.meal.application.dto.MealsUpdateDto;
import upeu.edu.pe.HABITIA_Back.modules.meal.application.service.MealsService;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.Meals;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController

@RequestMapping(path = "Meals")
public class MealsController {

    private final MealsService mealsService;

    private final Logger log =
            LoggerFactory.getLogger(MealsController.class);

    public MealsController(MealsService mealsService) {
        this.mealsService = mealsService;
    }

    //ver un plato, funciona si es tuyo; sino pues 404
    @GetMapping(path = "{id}")
    public ResponseEntity<MealsResponseDto> get(
            @PathVariable Long id) {

        log.info("GET: meal with ID {}", id);

        return mealsService.consultar(id)
                .map(meal -> ResponseEntity.ok(toResponseDTO(meal)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //lista tus platos; sino pues vacio
    @GetMapping(path = "user/{userId}")
    public ResponseEntity<List<MealsResponseDto>> getByUser(
            @PathVariable Long userId) {

        log.info("GET: meals with user ID {}", userId);

        List<MealsResponseDto> response =
                mealsService.findByUserId(userId)
                        .stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "user/{userId}/date/{date}")
    public ResponseEntity<List<MealsResponseDto>> getByDate(
            @PathVariable Long userId,
            @PathVariable LocalDate date) {

        log.info("GET: meals with user ID {} and date {}", userId, date);

        List<MealsResponseDto> response =
                mealsService.findByDate(userId, date)
                        .stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "user/{userId}/between")
    public ResponseEntity<List<MealsResponseDto>> getByDateBetween(
            @PathVariable Long userId,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {

        log.info("GET: meals for user {} between {} and {}",
                userId, inicio, fin);

        List<MealsResponseDto> response =
                mealsService.findByDateBetween(userId, inicio, fin)
                        .stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }

    //creacion de comida, funciona con JWT; sino pues 401
    @PostMapping
    public ResponseEntity<MealsResponseDto> post(
            @Valid @RequestBody MealsCreateDto dto) {

        log.info("POST: creating meal");

        Meals meal = new Meals();
        meal.setName(dto.getName());
        meal.setType(dto.getType());
        meal.setDate(dto.getDate());
        meal.setTime(dto.getTime());
        meal.setDescription(dto.getDescription());
        meal.setCompleted(dto.getCompleted());

        Meals createdMeal = mealsService.crear(meal, dto.getUserId());

        return ResponseEntity.created(
                URI.create("/habitia-backend/Meals/"
                        + createdMeal.getId())
        ).body(toResponseDTO(createdMeal));
    }

    //edita tu plato, funciona si es tuyo; sino pues 404
    @PutMapping(path = "{id}")
    public ResponseEntity<MealsResponseDto> put(
            @PathVariable Long id,
            @Valid @RequestBody MealsUpdateDto dto) {

        log.info("PUT: meal with ID {}", id);

        return mealsService.consultar(id)
                .map(existingMeal -> {

                    existingMeal.setName(dto.getName());
                    existingMeal.setType(dto.getType());
                    existingMeal.setDate(dto.getDate());
                    existingMeal.setTime(dto.getTime());
                    existingMeal.setDescription(dto.getDescription());

                    if (dto.getCompleted() != null) {
                        existingMeal.setCompleted(dto.getCompleted());
                    }

                    Meals updatedMeal =
                            mealsService.update(existingMeal);

                    return ResponseEntity.ok(toResponseDTO(updatedMeal));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //borra tu plato, funciona si es tuyo; sino pues 404
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        log.info("DELETE: meal with ID {}", id);

        if (mealsService.consultar(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        mealsService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private MealsResponseDto toResponseDTO(Meals meal) {
        return new MealsResponseDto(
                meal.getId(),
                meal.getUser().getId(),
                meal.getName(),
                meal.getType(),
                meal.getDate(),
                meal.getTime(),
                meal.getDescription(),
                meal.getCompleted()
        );
    }
}
