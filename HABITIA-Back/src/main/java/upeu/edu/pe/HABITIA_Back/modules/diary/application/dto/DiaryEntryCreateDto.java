package upeu.edu.pe.HABITIA_Back.modules.diary.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DiaryEntryCreateDto(
        @NotNull
        LocalDate entryDate,

        @NotBlank
        @Size(max = 4000)
        String text
) {}
