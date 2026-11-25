package upeu.edu.pe.HABITIA_Back.modules.sync.infrastructure.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.sync.application.dto.*;
import upeu.edu.pe.HABITIA_Back.modules.sync.application.service.SyncService;

import java.time.Instant;

@RestController
@RequestMapping("/Sync")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/push")
    public ResponseEntity<SyncStatus> push(
            @RequestBody SyncPushRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(syncService.push(authentication.getName(), request));
    }

    @PostMapping("/pull")
    public ResponseEntity<SyncPullResponse> pull(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String lastSync,
            Authentication authentication) {
        Instant ls = (lastSync == null || lastSync.isBlank()) ? null : Instant.parse(lastSync);
        return ResponseEntity.ok(syncService.pull(authentication.getName(), deviceId, ls));
    }
}
