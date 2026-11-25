package upeu.edu.pe.HABITIA_Back.modules.calendar.application.service;
//agenda: guarda eventos y los trae por rango de fechas; si no hay pues vacio

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.HABITIA_Back.modules.calendar.infrastructure.persistence.CalendarEventRepository;
import upeu.edu.pe.HABITIA_Back.modules.calendar.domain.model.CalendarEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//agenda: guarda/eventos por rango de fechas; si no hay pues vacio
@Service
@Transactional
//agenda: guarda/eventos por rango de fechas; si no hay pues vacio
public class CalendarEventServiceImpl implements CalendarEventService{
    private final CalendarEventRepository calendarEventRepository;

    public CalendarEventServiceImpl(CalendarEventRepository calendarEventRepository) {
        this.calendarEventRepository = calendarEventRepository;
    }

    @Override
    public CalendarEvent crear(CalendarEvent calendarEvent) {
        return calendarEventRepository.save(calendarEvent);
    }

    @Override
    public Optional<CalendarEvent> consultar(Long eventId) {
        return calendarEventRepository.findById(eventId);
    }

    @Override
    public CalendarEvent update(CalendarEvent calendarEvent) {
        return calendarEventRepository.save(calendarEvent);
    }

    @Override
    public void eliminar(Long eventId) {
        calendarEventRepository.deleteById(eventId);
    }

    @Override
    public List<CalendarEvent> listaByUserId(Long userId) {
        return calendarEventRepository.findByUserId(userId);
    }

    @Override
    public List<CalendarEvent> listaByFechaEntre(Long userId, LocalDateTime inicio, LocalDateTime fin) {
        return calendarEventRepository.findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, inicio, fin);
    }
}
