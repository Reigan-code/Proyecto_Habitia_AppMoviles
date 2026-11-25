
package upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.Controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitCompletionRequestDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitCompletionResponseDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitCompletionUpdateDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.application.service.HabitCompletionService;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.HabitCompletion;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/HabitCompletions")
public class HabitCompletionController {

    private final HabitCompletionService habitCompletionService;

    public HabitCompletionController(
            HabitCompletionService habitCompletionService) {
        this.habitCompletionService = habitCompletionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitCompletionResponseDTO> consultar(
            @PathVariable Long id,
            Authentication authentication) {

        HabitCompletion completion = habitCompletionService
                .consultar(id, authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El cumplimiento no existe"
                ));

        return ResponseEntity.ok(
                HabitCompletionResponseDTO.fromEntity(completion)
        );
    }

    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<HabitCompletionResponseDTO>> listarPorHabit(
            @PathVariable Long habitId,
            Authentication authentication) {

        List<HabitCompletionResponseDTO> response =
                habitCompletionService.listaByHabitId(
                                habitId, authentication.getName()
                        )
                        .stream()
                        .map(HabitCompletionResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/habit/{habitId}/date/{date}")
    public ResponseEntity<HabitCompletionResponseDTO> consultarPorFecha(
            @PathVariable Long habitId,
            @PathVariable LocalDate date,
            Authentication authentication) {

        HabitCompletion completion = habitCompletionService
                .consultarPorFecha(
                        habitId, date, authentication.getName()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un cumplimiento para esa fecha"
                ));

        return ResponseEntity.ok(
                HabitCompletionResponseDTO.fromEntity(completion)
        );
    }

    @GetMapping("/habit/{habitId}/between")
    public ResponseEntity<List<HabitCompletionResponseDTO>> listarEntreFechas(
            @PathVariable Long habitId,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin,
            Authentication authentication) {

        List<HabitCompletionResponseDTO> response =
                habitCompletionService.listaByFechaEntre(
                                habitId,
                                inicio,
                                fin,
                                authentication.getName()
                        )
                        .stream()
                        .map(HabitCompletionResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<HabitCompletionResponseDTO> crear(
            @Valid @RequestBody HabitCompletionRequestDTO request,
            Authentication authentication) {
        HabitCompletion completion = habitCompletionService.crear(
                request, authentication.getName()
        );
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(completion.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(HabitCompletionResponseDTO.fromEntity(completion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitCompletionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody HabitCompletionUpdateDTO request,
            Authentication authentication) {

        HabitCompletion completion = habitCompletionService.update(
                id, request, authentication.getName()
        );

        return ResponseEntity.ok(
                HabitCompletionResponseDTO.fromEntity(completion)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {

        habitCompletionService.eliminar(
                id, authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
