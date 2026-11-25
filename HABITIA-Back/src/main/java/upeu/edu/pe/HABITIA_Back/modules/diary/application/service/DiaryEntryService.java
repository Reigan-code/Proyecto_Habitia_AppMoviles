package upeu.edu.pe.HABITIA_Back.modules.diary.application.service;

import upeu.edu.pe.HABITIA_Back.modules.diary.domain.model.DiaryEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryEntryService {
    DiaryEntry crear(DiaryEntry entry, String emailUsuario);
    Optional<DiaryEntry> consultar(Long id, String emailUsuario);
    void eliminar(Long id, String emailUsuario);
    List<DiaryEntry> listaByUser(String emailUsuario);
    List<DiaryEntry> listaByDate(String emailUsuario, LocalDate date);
    DiaryEntry update(Long id, String text, String emailUsuario);
}
