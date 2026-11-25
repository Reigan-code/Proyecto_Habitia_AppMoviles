
package upeu.edu.pe.HABITIA_Back.modules.habit.application.service;

import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.Habit;

import java.util.List;
import java.util.Optional;

public interface HabitService {
    // Crea un hábito asociado al usuario autenticado.
    Habit crear(Habit habit, String emailUsuario);
    // Actualiza un hábito únicamente si pertenece al usuario autenticado.
    Habit update(Long habitId, Habit habit, String emailUsuario);
    Optional<Habit> consultar(Long habitId, String emailUsuario);
    void eliminar(Long habitId, String emailUsuario);
    // Lista todos los hábitos del usuario autenticado.
    List<Habit> listaByUser(String emailUsuario);
    List<Habit> habitActivos(String emailUsuario, Boolean active);
}
