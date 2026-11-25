package upeu.edu.pe.HABITIA_Back.modules.summary.application.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import upeu.edu.pe.HABITIA_Back.modules.summary.application.dto.DailySummaryResponse;
import upeu.edu.pe.HABITIA_Back.modules.task.infrastructure.persistence.TaskRepository;
import upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence.HabitRepository;
import upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence.HabitCompletionRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class SummaryServiceImpl implements SummaryService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final HabitCompletionRepository completionRepository;

    public SummaryServiceImpl(UserRepository userRepository, TaskRepository taskRepository,
                             HabitRepository habitRepository, HabitCompletionRepository completionRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
    }

    private User user(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Override
    public DailySummaryResponse daily(String email, LocalDate date) {
        final LocalDate d = (date == null) ? LocalDate.now() : date;
        User u = user(email);
        Long uid = u.getId();
        var tasks = taskRepository.findByUserId(uid);
        int tasksTotal = (int) tasks.stream().filter(t -> t.getDueDate() != null && t.getDueDate().equals(d)).count();
        int tasksDone = (int) tasks.stream().filter(t -> t.getDueDate() != null && t.getDueDate().equals(d)
                && t.getStatus() == TaskStatus.COMPLETED).count();
        var habits = habitRepository.findByUserIdAndActive(uid, true);
        int habitsTotal = habits.size();
        int habitsDone = 0;
        for (var h : habits) {
            if (completionRepository.existsByHabitIdAndDate(h.getId(), d)) habitsDone++;
        }
        int streak = computeStreak(uid, d);
        double progress = (tasksTotal + habitsTotal) == 0 ? 0.0
                : ((tasksDone + habitsDone) * 100.0) / (tasksTotal + habitsTotal);
        return new DailySummaryResponse(d, tasksTotal, tasksDone, habitsTotal, habitsDone, streak, progress);
    }

    private int computeStreak(Long userId, LocalDate date) {
        int streak = 0;
        LocalDate cursor = date;
        while (true) {
            var completions = completionRepository.findByHabitUserIdAndDateBetween(userId, cursor, cursor);
            if (completions == null || completions.isEmpty()) break;
            streak++;
            cursor = cursor.minusDays(1);
            if (streak > 365) break;
        }
        return streak;
    }

    @Override
    public List<String> pending(String email) {
        User u = user(email);
        return taskRepository.findByUserId(u.getId()).stream()
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                .map(t -> t.getTitle())
                .toList();
    }

    @Override
    public double habitsProgress(String email, LocalDate date) {
        final LocalDate d = (date == null) ? LocalDate.now() : date;
        User u = user(email);
        var habits = habitRepository.findByUserIdAndActive(u.getId(), true);
        if (habits.isEmpty()) return 0.0;
        int done = 0;
        for (var h : habits) {
            if (completionRepository.existsByHabitIdAndDate(h.getId(), d)) done++;
        }
        return (done * 100.0) / habits.size();
    }
}
