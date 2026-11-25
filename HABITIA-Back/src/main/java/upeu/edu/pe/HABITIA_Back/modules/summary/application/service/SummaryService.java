package upeu.edu.pe.HABITIA_Back.modules.summary.application.service;

import upeu.edu.pe.HABITIA_Back.modules.summary.application.dto.DailySummaryResponse;
import java.time.LocalDate;
import java.util.List;

public interface SummaryService {
    DailySummaryResponse daily(String emailUsuario, LocalDate date);
    List<String> pending(String emailUsuario);
    double habitsProgress(String emailUsuario, LocalDate date);
}
