
package upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.RepeatType;

import java.time.LocalDateTime;

public record ReminderUpdateDto(
        @NotBlank
        @Size(max = 150)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull
        LocalDateTime reminderDate,

        RepeatType repeat,

        @NotNull
        Boolean completed
) {}
