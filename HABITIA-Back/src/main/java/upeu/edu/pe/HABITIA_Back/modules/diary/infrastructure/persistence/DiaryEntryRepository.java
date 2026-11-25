package upeu.edu.pe.HABITIA_Back.modules.diary.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.modules.diary.domain.model.DiaryEntry;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {
    List<DiaryEntry> findByUserOrderByEntryDateDesc(User user);
    List<DiaryEntry> findByUserAndEntryDate(User user, LocalDate date);
    Optional<DiaryEntry> findByIdAndUser(Long id, User user);
}
