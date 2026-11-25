package upeu.edu.pe.HABITIA_Back.modules.task.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.modules.task.domain.model.TaskCategory;

import java.util.List;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {
    List<TaskCategory> findByUserIdOrderById(Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
}
