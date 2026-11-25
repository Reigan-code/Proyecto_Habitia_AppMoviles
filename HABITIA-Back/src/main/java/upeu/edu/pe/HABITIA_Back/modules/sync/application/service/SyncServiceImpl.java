package upeu.edu.pe.HABITIA_Back.modules.sync.application.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.HABITIA_Back.modules.sync.application.dto.*;
import java.time.Instant;
import java.util.Collections;

@Service
@Transactional
public class SyncServiceImpl implements SyncService {
    @Override
    public SyncStatus push(String emailUsuario, SyncPushRequest request) {
        // Idempotente por lastSync; aquí se persistirían los deltas.
        return SyncStatus.OK;
    }

    @Override
    public SyncPullResponse pull(String emailUsuario, String deviceId, Instant lastSync) {
        return new SyncPullResponse(deviceId, Collections.emptyList(), Instant.now(), SyncStatus.OK);
    }
}
