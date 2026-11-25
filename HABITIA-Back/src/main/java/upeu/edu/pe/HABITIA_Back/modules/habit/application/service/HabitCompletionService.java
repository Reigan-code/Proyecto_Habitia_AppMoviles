
package upeu.edu.pe.HABITIA_Back.modules.habit.application.service;

import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitCompletionRequestDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitCompletionUpdateDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.HabitCompletion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCompletionService {
        HabitCompletion crear(HabitCompletionRequestDTO request, String emailUsuario);
        HabitCompletion update(Long completionId, HabitCompletionUpdateDTO request, String emailUsuario);
        Optional<HabitCompletion> consultar(Long completionId, String emailUsuario);
        void eliminar(Long completionId, String emailUsuario);
        List<HabitCompletion> listaByHabitId(Long habitId, String emailUsuario);
        Optional<HabitCompletion> consultarPorFecha(Long habitId, LocalDate date, String emailUsuario);
        List<HabitCompletion> listaByFechaEntre(Long habitId, LocalDate inicio, LocalDate fin, String emailUsuario);
}
