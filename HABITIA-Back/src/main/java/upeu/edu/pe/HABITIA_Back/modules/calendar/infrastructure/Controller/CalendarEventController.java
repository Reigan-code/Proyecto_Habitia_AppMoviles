
package upeu.edu.pe.HABITIA_Back.modules.calendar.infrastructure.Controller;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.calendar.application.service.CalendarEventService;
import upeu.edu.pe.HABITIA_Back.modules.calendar.domain.model.CalendarEvent;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController

@RequestMapping(path = "CalendarEvents")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    private final Logger log =
            LoggerFactory.getLogger(CalendarEventController.class);

    public CalendarEventController(
            CalendarEventService calendarEventService) {
        this.calendarEventService = calendarEventService;
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<CalendarEvent> get(
            @PathVariable Long id) {

        log.info("GET: calendar event with ID {}", id);

        return this.calendarEventService.consultar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //eventos para la agenda; sino pues vacio
    @GetMapping(path = "user/{userId}")
    public ResponseEntity<List<CalendarEvent>> getByUser(
            @PathVariable Long userId) {

        log.info("GET: calendar events with user ID {}", userId);

        return ResponseEntity.ok(
                this.calendarEventService.listaByUserId(userId)
        );
    }

    //eventos entre fechas; sino pues vacio
    @GetMapping(path = "user/{userId}/between")
    public ResponseEntity<List<CalendarEvent>> getByDateBetween(
            @PathVariable Long userId,
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {

        log.info(
                "GET: calendar events with user ID {} between {} and {}",
                userId, inicio, fin
        );

        return ResponseEntity.ok(
                this.calendarEventService.listaByFechaEntre(
                        userId, inicio, fin
                )
        );
    }

    //agenda un evento (lo que crea Haabi); sino pues 401
    @PostMapping
    public ResponseEntity<CalendarEvent> post(
            @RequestBody CalendarEvent calendarEvent) {

        log.info("POST: creating calendar event");

        CalendarEvent createdEvent =
                this.calendarEventService.crear(calendarEvent);

        return ResponseEntity.created(
                URI.create("/CalendarEvents/" + createdEvent.getId())
        ).body(createdEvent);
    }

    //mueve el evento de dia; sino pues 404
    @PutMapping(path = "{id}")
    public ResponseEntity<CalendarEvent> put(
            @RequestBody CalendarEvent calendarEvent,
            @PathVariable Long id) {

        log.info("PUT: calendar event with ID {}", id);

        calendarEvent.setId(id);

        return ResponseEntity.ok(
                this.calendarEventService.update(calendarEvent)
        );
    }

    //borra el evento; sino pues 404
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        log.info("DELETE: calendar event with ID {}", id);

        this.calendarEventService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
