
package upeu.edu.pe.HABITIA_Back.modules.reminder.application.service;
//recordatorios: crea/lee/marca hecho; el proximo sale ordenado por fecha+hora

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderUpdateDto;
import upeu.edu.pe.HABITIA_Back.modules.reminder.infrastructure.persistence.ReminderRepository;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.reminder.domain.model.Reminder;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;

    // Spring inyecta los repositorios necesarios para trabajar con los datos.
    public ReminderServiceImpl(
            ReminderRepository reminderRepository,
            UserRepository userRepository) {

        this.reminderRepository = reminderRepository;
        this.userRepository = userRepository;
    }

    // Busca al usuario a partir del correo obtenido de la autenticación JWT.
    private User obtenerUsuarioAutenticado(String emailUsuario) {
        return userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    // Busca un recordatorio verificando su ID y el ID de su propietario.
    private Reminder obtenerReminderPropio(
            Long reminderId,
            Long userId) {

        return reminderRepository.findByIdAndUser_Id(
                        reminderId,
                        userId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recordatorio no encontrado"
                ));
    }

    // CREA UN RECORDATORIO
    @Override
    public ReminderResponseDto crear(
            ReminderCreateDto request,
            String emailUsuario) {
        // El propietario se obtiene de la identidad autenticada,
        // no del userId que pueda enviar el cliente.
        User user = obtenerUsuarioAutenticado(emailUsuario);
        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setTitle(request.title().trim());
        reminder.setDescription(request.description());
        reminder.setReminderDate(request.reminderDate());
        reminder.setRepeat(request.repeat());
        reminder.setCompleted(false);
        return convertir(reminderRepository.save(reminder));
    }

    // CONSULTA UN RECORDATORIO PROPIO
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ReminderResponseDto consultar(
            Long reminderId,
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Solo se devuelve el recordatorio si pertenece al usuario autenticado.
        Reminder reminder = obtenerReminderPropio(
                reminderId,
                user.getId()
        );

        return convertir(reminder);
    }

    // ACTUALIZA UN RECORDATORIO PROPIO
    @Override
    public ReminderResponseDto actualizar(
            Long reminderId,
            ReminderUpdateDto request,
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Comprueba la propiedad antes de modificar los datos.
        Reminder reminder = obtenerReminderPropio(
                reminderId,
                user.getId()
        );

        reminder.setTitle(request.title().trim());
        reminder.setDescription(request.description());
        reminder.setReminderDate(request.reminderDate());
        reminder.setRepeat(request.repeat());
        reminder.setCompleted(request.completed());

        return convertir(reminderRepository.save(reminder));
    }

    // ELIMINA UN RECORDATORIO PROPIO
    @Override
    public void eliminar(
            Long reminderId,
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        // Impide eliminar recordatorios pertenecientes a otros usuarios.
        Reminder reminder = obtenerReminderPropio(
                reminderId,
                user.getId()
        );

        reminderRepository.delete(reminder);
    }

    // LISTA LOS RECORDATORIOS DEL USUARIO AUTENTICADO
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ReminderResponseDto> listarPorUsuario(
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        return reminderRepository
                .findByUser_IdOrderByReminderDateAsc(user.getId())
                .stream()
                .map(this::convertir)
                .toList();
    }

    // FILTRA LOS RECORDATORIOS PROPIOS POR ESTADO
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ReminderResponseDto> listarPorEstado(
            Boolean completed,
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);

        return reminderRepository
                .findByUser_IdAndCompletedOrderByReminderDateAsc(
                        user.getId(),
                        completed
                )
                .stream()
                .map(this::convertir)
                .toList();
    }

    // LISTA LOS RECORDATORIOS PROPIOS DENTRO DE UN RANGO DE FECHAS
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ReminderResponseDto> listarEntreFechas(
            LocalDateTime inicio,
            LocalDateTime fin,
            String emailUsuario) {

        // Valida el rango antes de ejecutar la consulta.
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El rango de fechas no es válido"
            );
        }

        User user = obtenerUsuarioAutenticado(emailUsuario);

        return reminderRepository
                .findByUser_IdAndReminderDateBetweenOrderByReminderDateAsc(
                        user.getId(),
                        inicio,
                        fin
                )
                .stream()
                .map(this::convertir)
                .toList();
    }

    // CONVIERTE LA ENTIDAD EN UN DTO DE RESPUESTA
    private ReminderResponseDto convertir(Reminder reminder) {
        return new ReminderResponseDto(
                reminder.getId(),
                reminder.getUser().getId(),
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getReminderDate(),
                reminder.getRepeat(),
                reminder.getCompleted()
        );
    }
}
