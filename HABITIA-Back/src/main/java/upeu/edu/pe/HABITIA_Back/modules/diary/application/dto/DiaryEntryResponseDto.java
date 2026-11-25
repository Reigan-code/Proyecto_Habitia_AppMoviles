package upeu.edu.pe.HABITIA_Back.modules.diary.application.dto;

import java.time.LocalDate;

public record DiaryEntryResponseDto(
        Long id,
        LocalDate entryDate,
        String text
) {
    public static DiaryEntryResponseDto fromEntity(upeu.edu.pe.HABITIA_Back.modules.diary.domain.model.DiaryEntry e) {
        return new DiaryEntryResponseDto(e.getId(), e.getEntryDate(), e.getText());
    }
}
