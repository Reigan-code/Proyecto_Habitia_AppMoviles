package upeu.edu.pe.HABITIA_Back.modules.reminder.infrastructure.Controller;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.core.Authentication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderUpdateDto;
import upeu.edu.pe.HABITIA_Back.modules.reminder.application.service.ReminderService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/Reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReminderResponseDto>> listar(
            @PathVariable Long userId,
            Authentication authentication) {

        return ResponseEntity.ok(
                reminderService.listarPorUsuario(authentication.getName())
        );
    }

    @GetMapping("/user/{userId}/{reminderId}")
    public ResponseEntity<ReminderResponseDto> consultar(
            @PathVariable Long userId,
            @PathVariable Long reminderId,
            Authentication authentication) {

        return ResponseEntity.ok(
                reminderService.consultar(
                        reminderId, authentication.getName())
        );
    }

    @GetMapping("/user/{userId}/completed/{completed}")
    public ResponseEntity<List<ReminderResponseDto>> listarPorEstado(
            @PathVariable Long userId,
            @PathVariable Boolean completed,
            Authentication authentication) {

        return ResponseEntity.ok(
                reminderService.listarPorEstado(
                        completed, authentication.getName())
        );
    }

    @GetMapping("/user/{userId}/between")
    public ResponseEntity<List<ReminderResponseDto>> listarEntreFechas(
            @PathVariable Long userId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin,

            Authentication authentication) {

        return ResponseEntity.ok(
                reminderService.listarEntreFechas(
                        inicio, fin, authentication.getName())
        );
    }

    @PostMapping
    public ResponseEntity<ReminderResponseDto> crear(
            @Valid @RequestBody ReminderCreateDto request,
            Authentication authentication) {

        ReminderResponseDto creado = reminderService.crear(
                request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/user/{userId}/{reminderId}")
    public ResponseEntity<ReminderResponseDto> actualizar(
            @PathVariable Long userId,
            @PathVariable Long reminderId,
            @Valid @RequestBody ReminderUpdateDto request,
            Authentication authentication) {

        return ResponseEntity.ok(
                reminderService.actualizar(
                        reminderId, request, authentication.getName())
        );
    }

    @DeleteMapping("/user/{userId}/{reminderId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long userId,
            @PathVariable Long reminderId,
            Authentication authentication) {

        reminderService.eliminar(
                reminderId, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
