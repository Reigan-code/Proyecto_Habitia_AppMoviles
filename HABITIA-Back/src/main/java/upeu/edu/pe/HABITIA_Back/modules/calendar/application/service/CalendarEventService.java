package upeu.edu.pe.HABITIA_Back.modules.calendar.application.service;

import upeu.edu.pe.HABITIA_Back.modules.calendar.domain.model.CalendarEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CalendarEventService {
    CalendarEvent crear(CalendarEvent calendarEvent);
    Optional<CalendarEvent> consultar(Long eventId);
    CalendarEvent update(CalendarEvent calendarEvent);
    void eliminar(Long eventId);
    List<CalendarEvent> listaByUserId(Long userId);
    List<CalendarEvent> listaByFechaEntre(
            Long userId,
            LocalDateTime inicio,
            LocalDateTime fin
    );

    boolean existeSolapamiento(Long userId, LocalDateTime inicio, LocalDateTime fin, Long excluirId);
}
