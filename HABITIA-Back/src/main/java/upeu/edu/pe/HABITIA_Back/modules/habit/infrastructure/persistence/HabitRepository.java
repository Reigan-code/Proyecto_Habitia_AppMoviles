package upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.Habit;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserId(Long userId);
    List<Habit> findByUserIdAndActive(Long userId, Boolean active);
    // Busca un hábito por su ID y comprueba que pertenezca al usuario indicado.
    // Si el hábito no existe o pertenece a otra persona, devuelve Optional.empty().
    Optional<Habit> findByIdAndUserId(Long habitId, Long userId);
}