
package upeu.edu.pe.HABITIA_Back.modules.task.application.service;

import upeu.edu.pe.HABITIA_Back.modules.task.application.dto.TaskCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.task.application.dto.TaskUpdateDto;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.TaskStatus;
import upeu.edu.pe.HABITIA_Back.modules.task.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    // Crea una tarea asignándola al usuario autenticado.
    Task crear(TaskCreateDto dto, String emailUsuario);
    // Consulta una tarea solo si pertenece al usuario autenticado.
    Optional<Task> consultar(Long taskId, String emailUsuario);
    // Actualiza únicamente las tareas del usuario autenticado.
    Task update(Long taskId, TaskUpdateDto dto, String emailUsuario);
    // Elimina únicamente las tareas del usuario autenticado.
    void delete(Long taskId, String emailUsuario);
    // Obtiene todas las tareas del usuario autenticado.
    List<Task> taskByUser(String emailUsuario);
    // Filtra las tareas del usuario autenticado por estado.
    List<Task> taskByEstado(String emailUsuario, TaskStatus status);
}
