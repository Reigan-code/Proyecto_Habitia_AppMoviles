package upeu.edu.pe.HABITIA_Back.modules.analytics.infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.analytics.application.dto.*;
import upeu.edu.pe.HABITIA_Back.modules.analytics.application.service.AnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/Analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/podium")
    public ResponseEntity<List<PodiumEntryDto>> podium(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.podium(authentication.getName()));
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklySummaryDto> weekly(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.weekly(authentication.getName()));
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceDto> balance(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.balance(authentication.getName()));
    }

    @GetMapping("/rewards")
    public ResponseEntity<List<RewardDto>> rewards(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.rewards(authentication.getName()));
    }
}
