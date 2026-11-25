
package upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.Controller;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitRequestDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitResponseDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.application.service.HabitService;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.Habit;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/Habits")
public class HabitController {

    private final HabitService habitService;

    private final Logger log =
            LoggerFactory.getLogger(HabitController.class);

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitResponseDTO> get(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("GET: habit requested");

        return habitService.consultar(id, authentication.getName())
                .map(HabitResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //lista tus habitos; sino pues vacio
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HabitResponseDTO>> getByUser(
            @PathVariable Long userId,
            Authentication authentication) {

        log.info("GET: habits for authenticated user");

        List<HabitResponseDTO> habits =
                habitService.listaByUser(authentication.getName())
                        .stream()
                        .map(HabitResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(habits);
    }

    @GetMapping("/user/{userId}/active/{active}")
    public ResponseEntity<List<HabitResponseDTO>> getByActive(
            @PathVariable Long userId,
            @PathVariable Boolean active,
            Authentication authentication) {

        log.info("GET: active habits for authenticated user");

        List<HabitResponseDTO> habits =
                habitService.habitActivos(
                                authentication.getName(), active)
                        .stream()
                        .map(HabitResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(habits);
    }

    //creacion de habito, funciona con JWT; sino pues 401
    @PostMapping
    public ResponseEntity<HabitResponseDTO> post(
            @Valid @RequestBody HabitRequestDTO dto,
            Authentication authentication) {

        log.info("POST: creating habit");

        Habit habit = new Habit();
        habit.setName(dto.getName());
        habit.setDescription(dto.getDescription());
        habit.setFrequency(dto.getFrequency());
        habit.setStartDate(dto.getStartDate());
        habit.setEndDate(dto.getEndDate());

        habit.setActive(
                dto.getActive() != null ? dto.getActive() : true);

        Habit createdHabit =
                habitService.crear(habit, authentication.getName());

        HabitResponseDTO response =
                HabitResponseDTO.fromEntity(createdHabit);

        return ResponseEntity
                .created(URI.create("/habitia-backend/Habits/"
                        + createdHabit.getId()))
                .body(response);
    }

    //edita tu habito; sino pues 404
    @PutMapping("/{id}")
    public ResponseEntity<HabitResponseDTO> put(
            @PathVariable Long id,
            @Valid @RequestBody HabitRequestDTO dto,
            Authentication authentication) {

        log.info("PUT: updating habit");

        Habit datosActualizados = new Habit();
        datosActualizados.setName(dto.getName());
        datosActualizados.setDescription(dto.getDescription());
        datosActualizados.setFrequency(dto.getFrequency());
        datosActualizados.setStartDate(dto.getStartDate());
        datosActualizados.setEndDate(dto.getEndDate());

        datosActualizados.setActive(dto.getActive());

        Habit updatedHabit = habitService.update(
                id, datosActualizados, authentication.getName());

        return ResponseEntity.ok(
                HabitResponseDTO.fromEntity(updatedHabit));
    }

    //borra tu habito; sino pues 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("DELETE: deleting habit");

        habitService.eliminar(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
