package upeu.edu.pe.HABITIA_Back.modules.diary.infrastructure.Controller;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.diary.application.dto.DiaryEntryCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.diary.application.dto.DiaryEntryResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.diary.application.service.DiaryEntryService;
import upeu.edu.pe.HABITIA_Back.modules.diary.domain.model.DiaryEntry;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/DiaryEntries")
public class DiaryEntryController {

    private final DiaryEntryService diaryEntryService;
    private final Logger log = LoggerFactory.getLogger(DiaryEntryController.class);

    public DiaryEntryController(DiaryEntryService diaryEntryService) {
        this.diaryEntryService = diaryEntryService;
    }

    //trae tu diario, funciona con JWT; sino pues vacio
    @GetMapping
    public ResponseEntity<List<DiaryEntryResponseDto>> list(Authentication authentication) {
        log.info("GET: diary entries for authenticated user");
        List<DiaryEntryResponseDto> entries = diaryEntryService
                .listaByUser(authentication.getName())
                .stream()
                .map(DiaryEntryResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(entries);
    }

    //entradas de un dia; sino pues vacio
    @GetMapping("/date/{date}")
    public ResponseEntity<List<DiaryEntryResponseDto>> byDate(
            @PathVariable LocalDate date,
            Authentication authentication) {
        log.info("GET: diary entries by date");
        List<DiaryEntryResponseDto> entries = diaryEntryService
                .listaByDate(authentication.getName(), date)
                .stream()
                .map(DiaryEntryResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(entries);
    }

    //guarda entrada del diario; sino pues 401
    @PostMapping
    public ResponseEntity<DiaryEntryResponseDto> post(
            @Valid @RequestBody DiaryEntryCreateDto dto,
            Authentication authentication) {
        log.info("POST: creating diary entry");
        DiaryEntry entry = new DiaryEntry();
        entry.setEntryDate(dto.entryDate());
        entry.setText(dto.text());
        DiaryEntry created = diaryEntryService.crear(entry, authentication.getName());
        return ResponseEntity
                .created(URI.create("/habitia-backend/DiaryEntries/" + created.getId()))
                .body(DiaryEntryResponseDto.fromEntity(created));
    }

    //edita tu entrada; sino pues 404
    @PutMapping("/{id}")
    public ResponseEntity<DiaryEntryResponseDto> put(
            @PathVariable Long id,
            @Valid @RequestBody DiaryEntryCreateDto dto,
            Authentication authentication) {
        log.info("PUT: updating diary entry");
        DiaryEntry updated = diaryEntryService.update(id, dto.text(), authentication.getName());
        return ResponseEntity.ok(DiaryEntryResponseDto.fromEntity(updated));
    }

    //borra tu entrada; sino pues 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("DELETE: deleting diary entry");
        diaryEntryService.eliminar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
