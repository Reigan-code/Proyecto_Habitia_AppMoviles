package upeu.edu.pe.HABITIA_Back.modules.sync.application.service;

import upeu.edu.pe.HABITIA_Back.modules.sync.application.dto.*;

public interface SyncService {
    SyncStatus push(String emailUsuario, SyncPushRequest request);
    SyncPullResponse pull(String emailUsuario, String deviceId, java.time.Instant lastSync);
}
