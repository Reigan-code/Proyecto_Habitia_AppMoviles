package upeu.edu.pe.HABITIA_Back.modules.task.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.TaskStatus;
import upeu.edu.pe.HABITIA_Back.modules.task.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task>findByUserId(Long userId);
    List<Task>findByUserIdAndStatus(Long userId, TaskStatus status);
    void deleteById(Long taskId);
    // Busca una tarea únicamente si pertenece al usuario indicado.
    // Si la tarea existe, pero pertenece a otra persona,
    // este metodo no la devuelve.
    Optional<Task> findByIdAndUserId(Long taskId, Long userId);
}
