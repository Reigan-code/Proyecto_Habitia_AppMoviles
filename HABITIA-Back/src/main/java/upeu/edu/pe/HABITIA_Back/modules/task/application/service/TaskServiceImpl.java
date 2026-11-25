
package upeu.edu.pe.HABITIA_Back.modules.task.application.service;
//tareas: crea/lee/edita/borra solo lo tuyo; si no es tuyo pues 404

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import upeu.edu.pe.HABITIA_Back.modules.task.application.dto.TaskCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.task.application.dto.TaskUpdateDto;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.TaskStatus;
import upeu.edu.pe.HABITIA_Back.modules.task.infrastructure.persistence.TaskRepository;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.task.domain.model.Task;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Busca al usuario mediante el correo obtenido de la autenticación.
    // Así no confiamos en un userId enviado por el cliente.
    private User obtenerUsuarioAutenticado(String emailUsuario) {
        return userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario autenticado no encontrado"));
    }

    @Override
    public Task crear(TaskCreateDto dto, String emailUsuario) {

        // El propietario se obtiene del usuario autenticado,
        // no de dto.getUserId().
        User user = obtenerUsuarioAutenticado(emailUsuario);

        Task task = new Task();
        task.setUser(user);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority());

        // Toda tarea nueva comienza pendiente.
        task.setStatus(TaskStatus.PENDING);

        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> consultar(Long taskId, String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Devuelve la tarea solo si pertenece a este usuario.
        return taskRepository.findByIdAndUserId(taskId, user.getId());
    }

    @Override
    public Task update(
            Long taskId,
            TaskUpdateDto dto,
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Si la tarea pertenece a otra persona, no se encuentra
        // mediante esta búsqueda y no puede modificarse.
        Task task = taskRepository.findByIdAndUserId(
                        taskId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tarea no encontrada"));

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        return taskRepository.save(task);
    }

    @Override
    public void delete(Long taskId, String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Solo se puede eliminar una tarea propia.
        Task task = taskRepository.findByIdAndUserId(
                        taskId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tarea no encontrada"));

        taskRepository.delete(task);
    }

    @Override
    public List<Task> taskByUser(String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // El usuario solo recibe sus propias tareas.
        return taskRepository.findByUserId(user.getId());
    }

    @Override
    public List<Task> taskByEstado(
            String emailUsuario,
            TaskStatus status) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // El filtro por estado también queda limitado al propietario.
        return taskRepository.findByUserIdAndStatus(
                user.getId(), status);
    }
}
