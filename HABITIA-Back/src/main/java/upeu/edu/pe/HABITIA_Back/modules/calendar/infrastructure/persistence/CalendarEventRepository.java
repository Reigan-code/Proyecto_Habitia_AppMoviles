package upeu.edu.pe.HABITIA_Back.modules.calendar.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.modules.calendar.domain.model.CalendarEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    List<CalendarEvent> findByUserId(Long userId);
    List<CalendarEvent> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long userId,
            LocalDateTime fin,
            LocalDateTime inicio
    );
}