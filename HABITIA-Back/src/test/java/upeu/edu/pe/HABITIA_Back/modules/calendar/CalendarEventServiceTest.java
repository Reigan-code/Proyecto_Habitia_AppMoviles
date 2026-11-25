package upeu.edu.pe.HABITIA_Back.modules.calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upeu.edu.pe.HABITIA_Back.modules.calendar.application.service.CalendarEventServiceImpl;
import upeu.edu.pe.HABITIA_Back.modules.calendar.domain.model.CalendarEvent;
import upeu.edu.pe.HABITIA_Back.modules.calendar.infrastructure.persistence.CalendarEventRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private CalendarEventRepository repository;

    @InjectMocks
    private CalendarEventServiceImpl service;

    @Test
    void existeSolapamiento_devuelveTrue_siHayEventoSolapado() {
        Long userId = 3L;
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 25, 15, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 25, 16, 0);

        CalendarEvent existente = new CalendarEvent();
        existente.setId(1L);
        existente.setStartDate(LocalDateTime.of(2026, 11, 25, 15, 30));
        existente.setEndDate(LocalDateTime.of(2026, 11, 25, 16, 30));
        User u = new User(); u.setId(userId);
        existente.setUser(u);

        when(repository.findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, fin, inicio))
                .thenReturn(List.of(existente));

        assertTrue(service.existeSolapamiento(userId, inicio, fin, null));
    }

    @Test
    void existeSolapamiento_devuelveFalse_siNoHaySolapamiento() {
        Long userId = 3L;
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 25, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 25, 11, 0);

        when(repository.findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, fin, inicio))
                .thenReturn(List.of());

        assertFalse(service.existeSolapamiento(userId, inicio, fin, null));
    }

    @Test
    void existeSolapamiento_excluyeElMismoEvento() {
        Long userId = 3L;
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 25, 15, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 11, 25, 16, 0);

        CalendarEvent mismo = new CalendarEvent();
        mismo.setId(5L);
        mismo.setStartDate(inicio);
        mismo.setEndDate(fin);
        User u = new User(); u.setId(userId);
        mismo.setUser(u);

        when(repository.findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(userId, fin, inicio))
                .thenReturn(List.of(mismo));

        assertFalse(service.existeSolapamiento(userId, inicio, fin, 5L));
    }
}
