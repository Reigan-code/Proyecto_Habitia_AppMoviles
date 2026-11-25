
package upeu.edu.pe.HABITIA_Back.modules.habit.application.service;
//habitos: crea/lee/edita/borra solo lo tuyo; si no es tuyo pues 404

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence.HabitRepository;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.Habit;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitServiceImpl(
            HabitRepository habitRepository,
            UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    // Obtiene al usuario autenticado a partir del correo de su JWT.
    // Si no existe en la base de datos, la operación no puede continuar.
    private User obtenerUsuarioAutenticado(String emailUsuario) {
        return userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario autenticado no encontrado"));
    }

    @Override
    public Habit crear(Habit habit, String emailUsuario) {
        // El propietario se obtiene del usuario autenticado,
        // no de un ID enviado por el cliente.
        User user = obtenerUsuarioAutenticado(emailUsuario);
        habit.setUser(user);

        return habitRepository.save(habit);
    }

    @Override
    public Optional<Habit> consultar(Long habitId, String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Solo devuelve el hábito si existe y pertenece a este usuario.
        return habitRepository.findByIdAndUserId(
                habitId, user.getId());
    }

    @Override
    public Habit update(
            Long habitId, Habit datosActualizados, String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Verifica la propiedad antes de modificar cualquier dato.
        Habit habit = habitRepository
                .findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Hábito no encontrado"));

        // Actualiza únicamente los campos permitidos.
        habit.setName(datosActualizados.getName());
        habit.setDescription(datosActualizados.getDescription());
        habit.setFrequency(datosActualizados.getFrequency());
        habit.setStartDate(datosActualizados.getStartDate());
        habit.setEndDate(datosActualizados.getEndDate());
        // Si no se envía el estado, se conserva el que ya tenía el hábito.
        if (datosActualizados.getActive() != null) {
            habit.setActive(datosActualizados.getActive());
        }
        return habitRepository.save(habit);
    }

    @Override
    public void eliminar(Long habitId, String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Impide eliminar hábitos que pertenecen a otros usuarios.
        Habit habit = habitRepository
                .findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Hábito no encontrado"));

        habitRepository.delete(habit);
    }

    @Override
    public List<Habit> listaByUser(String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);

        return habitRepository.findByUserId(user.getId());
    }

    @Override
    public List<Habit> habitActivos(
            String emailUsuario, Boolean active) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        return habitRepository.findByUserIdAndActive(
                user.getId(), active);
    }
}
