package upeu.edu.pe.HABITIA_Back.modules.sync.application.dto;

import java.time.Instant;

public record SyncEntityDto(String type, String payload, Instant updatedAt) {}
