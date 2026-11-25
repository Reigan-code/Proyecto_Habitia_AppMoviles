
package upeu.edu.pe.HABITIA_Back.modules.habit.application.service;
//marca habito por dia: si ya habia completion la cambia, sino crea; sirve para el streak

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitCompletionRequestDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.application.dto.HabitCompletionUpdateDTO;
import upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence.HabitCompletionRepository;
import upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence.HabitRepository;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.Habit;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.HabitCompletion;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HabitCompletionServiceImpl implements HabitCompletionService {

    private final HabitCompletionRepository habitCompletionRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitCompletionServiceImpl(
            HabitCompletionRepository habitCompletionRepository,
            HabitRepository habitRepository,
            UserRepository userRepository) {

        this.habitCompletionRepository = habitCompletionRepository;
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    // Busca al usuario autenticado utilizando el correo obtenido del JWT.
    private User obtenerUsuarioAutenticado(String emailUsuario) {
        return userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));
    }

    // Busca un hábito únicamente si pertenece al usuario indicado.
    private Habit obtenerHabitPropio(Long habitId, Long userId) {
        return habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El hábito no existe"));
    }

    // Busca un cumplimiento únicamente si su hábito pertenece al usuario.
    private HabitCompletion obtenerCumplimientoPropio(Long completionId, Long userId) {
        return habitCompletionRepository
                .findByIdAndHabitUserId(completionId, userId).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El cumplimiento no existe"));
    }

    // Registra un cumplimiento para un hábito del usuario autenticado.
    @Override
    public HabitCompletion crear(HabitCompletionRequestDTO request, String emailUsuario) {
        User usuario = obtenerUsuarioAutenticado(emailUsuario);
        // El cliente no puede elegir un hábito perteneciente a otra persona.
        Habit habit = obtenerHabitPropio(request.getHabitId(), usuario.getId());

        // No se crean nuevos cumplimientos para hábitos inactivos.
        if (!Boolean.TRUE.equals(habit.getActive())) {throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No se pueden registrar cumplimientos para un hábito inactivo");
        }LocalDate date = request.getDate();
        // El cumplimiento no puede ser anterior al inicio del hábito.
        if (date.isBefore(habit.getStartDate())) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha es anterior al inicio del hábito");
        }

        // Si existe fecha final, no se permiten registros posteriores.
        if (habit.getEndDate() != null && date.isAfter(habit.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha supera la fecha final del hábito");
        }
        // Impide registrar dos veces el mismo hábito en el mismo día.
        if (habitCompletionRepository.existsByHabitIdAndDate(habit.getId(), date)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cumplimiento para este hábito y fecha");
        }
        // Se construye la entidad utilizando el hábito verificado.
        HabitCompletion completion = new HabitCompletion();
        completion.setHabit(habit);
        completion.setDate(date);
        completion.setCompleted(request.getCompleted());
        return habitCompletionRepository.save(completion);
    }

    // Modifica el estado de un cumplimiento propio.
    @Override
    public HabitCompletion update(Long completionId, HabitCompletionUpdateDTO request, String emailUsuario) {
        User usuario = obtenerUsuarioAutenticado(emailUsuario);
        // Verifica la propiedad antes de permitir cualquier modificación.
        HabitCompletion completion = obtenerCumplimientoPropio(completionId, usuario.getId());
        // Solo cambia el estado; conserva el hábito y la fecha originales.
        completion.setCompleted(request.getCompleted());
        return completion;
    }

    // Consulta un cumplimiento verificando la propiedad del usuario.
    @Override
    public Optional<HabitCompletion> consultar(Long completionId, String emailUsuario) {
        User usuario = obtenerUsuarioAutenticado(emailUsuario);
        return Optional.of(obtenerCumplimientoPropio(completionId, usuario.getId()));
    }

    // Elimina un cumplimiento únicamente si pertenece al usuario.
    @Override
    public void eliminar(Long completionId, String emailUsuario) {
        User usuario = obtenerUsuarioAutenticado(emailUsuario);
        HabitCompletion completion = obtenerCumplimientoPropio(completionId, usuario.getId());
        habitCompletionRepository.delete(completion);
    }

    // Lista los cumplimientos de un hábito propio.
    @Override
    public List<HabitCompletion> listaByHabitId(Long habitId, String emailUsuario) {
        User usuario = obtenerUsuarioAutenticado(emailUsuario);
        // Verifica la propiedad antes de consultar el historial.
        obtenerHabitPropio(habitId, usuario.getId());
        return habitCompletionRepository.findByHabitId(habitId);
    }

    // Busca el cumplimiento de un hábito propio en una fecha.
    @Override
    public Optional<HabitCompletion> consultarPorFecha(Long habitId, LocalDate date, String emailUsuario) {
        User usuario = obtenerUsuarioAutenticado(emailUsuario);
        obtenerHabitPropio(habitId, usuario.getId());
        return habitCompletionRepository.findByHabitIdAndDate(habitId, date);
    }

    // Lista los cumplimientos de un hábito propio entre dos fechas.
    @Override
    public List<HabitCompletion> listaByFechaEntre(Long habitId, LocalDate inicio, LocalDate fin, String emailUsuario) {
        User usuario = obtenerUsuarioAutenticado(emailUsuario);
        obtenerHabitPropio(habitId, usuario.getId());
        // Valida que ambas fechas existan y que el rango sea correcto.
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rango de fechas no es válido");
        }
        return habitCompletionRepository.findByHabitIdAndDateBetween(habitId, inicio, fin);
    }
}
