package upeu.edu.pe.HABITIA_Back.modules.diary.application.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import upeu.edu.pe.HABITIA_Back.modules.diary.infrastructure.persistence.DiaryEntryRepository;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.diary.domain.model.DiaryEntry;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//diario: guarda por fecha, edita y borra solo lo tuyo
@Service
@Transactional
//diario: guarda por fecha, edita y borra solo lo tuyo
public class DiaryEntryServiceImpl implements DiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserRepository userRepository;

    public DiaryEntryServiceImpl(DiaryEntryRepository diaryEntryRepository,
                                 UserRepository userRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.userRepository = userRepository;
    }

    private User obtenerUsuarioAutenticado(String emailUsuario) {
        return userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario autenticado no encontrado"));
    }

    @Override
    public DiaryEntry crear(DiaryEntry entry, String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);
        entry.setUser(user);
        if (entry.getEntryDate() == null) {
            entry.setEntryDate(LocalDate.now());
        }
        return diaryEntryRepository.save(entry);
    }

    @Override
    public Optional<DiaryEntry> consultar(Long id, String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);
        return diaryEntryRepository.findByIdAndUser(id, user);
    }

    @Override
    public void eliminar(Long id, String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);
        DiaryEntry entry = diaryEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Entrada de diario no encontrada"));
        diaryEntryRepository.delete(entry);
    }

    @Override
    public List<DiaryEntry> listaByUser(String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);
        return diaryEntryRepository.findByUserOrderByEntryDateDesc(user);
    }

    @Override
    public List<DiaryEntry> listaByDate(String emailUsuario, LocalDate date) {
        User user = obtenerUsuarioAutenticado(emailUsuario);
        return diaryEntryRepository.findByUserAndEntryDate(user, date);
    }

    @Override
    public DiaryEntry update(Long id, String text, String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);
        DiaryEntry entry = diaryEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Entrada de diario no encontrada"));
        entry.setText(text);
        return diaryEntryRepository.save(entry);
    }
}
