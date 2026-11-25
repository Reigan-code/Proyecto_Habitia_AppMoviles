
package upeu.edu.pe.HABITIA_Back.modules.reminder.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.modules.reminder.domain.model.Reminder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUser_IdOrderByReminderDateAsc(
            Long userId
    );
    List<Reminder> findByUser_IdAndCompletedOrderByReminderDateAsc(
            Long userId,
            Boolean completed
    );
    List<Reminder> findByUser_IdAndReminderDateBetweenOrderByReminderDateAsc(
            Long userId,
            LocalDateTime inicio,
            LocalDateTime fin
    );
    Optional<Reminder> findByIdAndUser_Id(
            Long id,
            Long userId
    );
}
