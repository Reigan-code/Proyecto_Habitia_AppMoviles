
package upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto;

import upeu.edu.pe.HABITIA_Back.shared.domain.enums.RepeatType;

import java.time.LocalDateTime;

public record ReminderResponseDto(
        Long id,
        Long userId,
        String title,
        String description,
        LocalDateTime reminderDate,
        RepeatType repeat,
        Boolean completed
) {}
