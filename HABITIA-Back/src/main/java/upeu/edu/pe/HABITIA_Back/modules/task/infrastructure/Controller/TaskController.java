
package upeu.edu.pe.HABITIA_Back.modules.task.infrastructure.Controller;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import upeu.edu.pe.HABITIA_Back.modules.task.application.dto.TaskCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.task.application.dto.TaskResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.task.application.dto.TaskUpdateDto;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.TaskStatus;
import upeu.edu.pe.HABITIA_Back.modules.task.application.service.TaskService;
import upeu.edu.pe.HABITIA_Back.modules.task.domain.model.Task;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "Tasks")
public class TaskController {

    private final TaskService taskService;
    private final Logger log =
            LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //ver una tarea, funciona si es tuya; sino pues 404
    @GetMapping(path = "{id}")
    public ResponseEntity<TaskResponseDto> get(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("GET: task with ID {}", id);

        String emailUsuario = authentication.getName();

        return taskService.consultar(id, emailUsuario)
                .map(task -> ResponseEntity.ok(toResponseDTO(task)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //lista tus tareas del JWT; sino pues vacio
    @GetMapping(path = "user/{userId}")
    public ResponseEntity<List<TaskResponseDto>> getByUser(
            @PathVariable Long userId,
            Authentication authentication) {

        log.info("GET: tasks requested");

        String emailUsuario = authentication.getName();

        List<TaskResponseDto> tasks =
                taskService.taskByUser(emailUsuario)
                        .stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping(path = "user/{userId}/status/{status}")
    public ResponseEntity<List<TaskResponseDto>> getByEstado(
            @PathVariable Long userId,
            @PathVariable TaskStatus status,
            Authentication authentication) {

        log.info("GET: tasks filtered by status {}", status);

        String emailUsuario = authentication.getName();

        List<TaskResponseDto> tasks =
                taskService.taskByEstado(emailUsuario, status)
                        .stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(tasks);
    }


    //creacion de tarea, funciona si hay JWT; sino pues 401
    @PostMapping
    public ResponseEntity<TaskResponseDto> post(
            @Valid @RequestBody TaskCreateDto dto,
            Authentication authentication) {

        log.info("POST: creating task");

        String emailUsuario = authentication.getName();

        Task createdTask = taskService.crear(dto, emailUsuario);

        return ResponseEntity.created(
                URI.create("/habitia-backend/Tasks/" + createdTask.getId())
        ).body(toResponseDTO(createdTask));
    }


    //edita tu tarea, funciona si es tuya; sino pues 404
    @PutMapping(path = "{id}")
    public ResponseEntity<TaskResponseDto> put(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateDto dto,
            Authentication authentication) {

        log.info("PUT: task with ID {}", id);

        String emailUsuario = authentication.getName();

        Task updatedTask = taskService.update(id, dto, emailUsuario);

        return ResponseEntity.ok(toResponseDTO(updatedTask));
    }


    //borra tu tarea, funciona si es tuya; sino pues 404
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("DELETE: task with ID {}", id);

        String emailUsuario = authentication.getName();

        taskService.delete(id, emailUsuario);

        return ResponseEntity.noContent().build();
    }

    private TaskResponseDto toResponseDTO(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getUser().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }
}
