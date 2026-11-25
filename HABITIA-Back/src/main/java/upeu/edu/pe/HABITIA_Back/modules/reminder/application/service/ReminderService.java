
package upeu.edu.pe.HABITIA_Back.modules.reminder.application.service;

import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.reminder.application.dto.ReminderUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderService {

    ReminderResponseDto crear(ReminderCreateDto request, String emailUsuario);
    ReminderResponseDto consultar(Long reminderId, String emailUsuario);
    ReminderResponseDto actualizar(
            Long reminderId,
            ReminderUpdateDto request,
            String emailUsuario
    );
    void eliminar(Long reminderId, String emailUsuario);
    List<ReminderResponseDto> listarPorUsuario(String emailUsuario);
    List<ReminderResponseDto> listarPorEstado(
            Boolean completed,
            String emailUsuario
    );
    List<ReminderResponseDto> listarEntreFechas(
            LocalDateTime inicio,
            LocalDateTime fin,
            String emailUsuario
    );
}
