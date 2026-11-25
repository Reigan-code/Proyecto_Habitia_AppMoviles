
package upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence;

import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.HabitCompletion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    List<HabitCompletion> findByHabitId(Long habitId);
    Optional<HabitCompletion> findByHabitIdAndDate(Long habitId, LocalDate date);
    List<HabitCompletion> findByHabitIdAndDateBetween(
            Long habitId,
            LocalDate inicio,
            LocalDate fin
    );
    // Comprueba si ya existe un registro para ese hábito y esa fecha.
    boolean existsByHabitIdAndDate(Long habitId, LocalDate date);

    // Busca un cumplimiento por su ID y verifica que el hábito asociado
    // pertenezca al usuario indicado.
    Optional<HabitCompletion> findByIdAndHabitUserId(Long completionId, Long userId);
}
