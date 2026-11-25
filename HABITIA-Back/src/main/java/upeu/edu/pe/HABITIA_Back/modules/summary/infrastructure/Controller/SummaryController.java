package upeu.edu.pe.HABITIA_Back.modules.summary.infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.summary.application.dto.DailySummaryResponse;
import upeu.edu.pe.HABITIA_Back.modules.summary.application.service.SummaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/Summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/daily")
    public ResponseEntity<DailySummaryResponse> daily(
            @RequestParam(required = false) String date,
            Authentication authentication) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        return ResponseEntity.ok(summaryService.daily(authentication.getName(), d));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<String>> pending(Authentication authentication) {
        return ResponseEntity.ok(summaryService.pending(authentication.getName()));
    }

    @GetMapping("/habits-progress")
    public ResponseEntity<Double> habitsProgress(
            @RequestParam(required = false) String date,
            Authentication authentication) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        return ResponseEntity.ok(summaryService.habitsProgress(authentication.getName(), d));
    }
}
