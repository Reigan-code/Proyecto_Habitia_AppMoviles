package upeu.edu.pe.HABITIA_Back.modules.sync.application.dto;

import java.time.Instant;
import java.util.List;

public record SyncPushRequest(String deviceId, List<SyncEntityDto> entities, Instant lastSync) {}
