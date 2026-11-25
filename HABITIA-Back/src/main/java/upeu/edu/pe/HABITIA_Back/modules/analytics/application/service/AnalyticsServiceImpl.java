package upeu.edu.pe.HABITIA_Back.modules.analytics.application.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import upeu.edu.pe.HABITIA_Back.modules.analytics.application.dto.*;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.task.infrastructure.persistence.TaskRepository;
import upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence.HabitRepository;
import upeu.edu.pe.HABITIA_Back.modules.habit.infrastructure.persistence.HabitCompletionRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealsRepository;
import upeu.edu.pe.HABITIA_Back.modules.reminder.infrastructure.persistence.ReminderRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.TaskStatus;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final HabitCompletionRepository completionRepository;
    private final MealsRepository mealsRepository;
    private final ReminderRepository reminderRepository;

    public AnalyticsServiceImpl(UserRepository userRepository, TaskRepository taskRepository,
                               HabitRepository habitRepository, HabitCompletionRepository completionRepository,
                               MealsRepository mealsRepository, ReminderRepository reminderRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
        this.mealsRepository = mealsRepository;
        this.reminderRepository = reminderRepository;
    }

    private User user(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Override
    public List<PodiumEntryDto> podium(String email) {
        User u = user(email);
        Long uid = u.getId();
        var habits = habitRepository.findByUserId(uid);
        // contar completions por hábito
        Map<Long, Long> counts = new HashMap<>();
        for (var h : habits) counts.put(h.getId(), 0L);
        for (var c : completionRepository.findByHabitUserId(uid)) {
            counts.merge(c.getHabit().getId(), 1L, Long::sum);
        }
        List<Map.Entry<Long, Long>> sorted = counts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(3).toList();
        List<PodiumEntryDto> result = new ArrayList<>();
        String[] rewards = {"🏆 Trofeo de Oro", "🥈 Medalla de Plata", "🥉 Insignia de Bronce"};
        int pos = 1;
        for (var e : sorted) {
            String name = habits.stream().filter(h -> h.getId().equals(e.getKey())).map(h -> h.getName()).findFirst().orElse("Hábito");
            result.add(new PodiumEntryDto(pos, name, e.getValue(), rewards[pos - 1]));
            pos++;
        }
        return result;
    }

    @Override
    public WeeklySummaryDto weekly(String email) {
        User u = user(email);
        Long uid = u.getId();
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);
        int tasksDone = (int) taskRepository.findByUserId(uid).stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED
                        && t.getDueDate() != null && !t.getDueDate().isBefore(start) && !t.getDueDate().isAfter(today))
                .count();
        int habitsDone = completionRepository.findByHabitUserIdAndDateBetween(uid, start, today).size();
        int mealsLogged = mealsRepository.findByUserIdAndDateBetween(uid, start, today).size();
        int remindersDone = (int) reminderRepository.findByUser_IdOrderByReminderDateAsc(uid).stream()
                .filter(r -> Boolean.TRUE.equals(r.getCompleted()) && r.getReminderDate() != null
                        && !r.getReminderDate().toLocalDate().isBefore(start) && !r.getReminderDate().toLocalDate().isAfter(today))
                .count();
        return new WeeklySummaryDto(tasksDone, habitsDone, mealsLogged, remindersDone);
    }

    @Override
    public BalanceDto balance(String email) {
        User u = user(email);
        Long uid = u.getId();
        var tasks = taskRepository.findByUserId(uid);
        int totalTasks = tasks.size();
        int doneTasks = (int) tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        double productivity = totalTasks == 0 ? 0.0 : (doneTasks * 100.0) / totalTasks;
        var habits = habitRepository.findByUserId(uid);
        int totalHabits = habits.size();
        long totalCompletions = completionRepository.findByHabitUserId(uid).size();
        double wellness = (totalHabits == 0) ? 0.0 : Math.min(100.0, (totalCompletions * 100.0) / (totalHabits * 7));
        double balance = (productivity + wellness) / 2.0;
        return new BalanceDto(productivity, wellness, balance);
    }

    @Override
    public List<RewardDto> rewards(String email) {
        User u = user(email);
        Long uid = u.getId();
        int streak = computeStreak(uid, LocalDate.now());
        long totalCompletions = completionRepository.findByHabitUserId(uid).size();
        int tasksDone = (int) taskRepository.findByUserId(uid).stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        List<RewardDto> list = new ArrayList<>();
        list.add(new RewardDto("R1", "Primera semana", "Completa 7 días seguidos", "🏅", streak >= 7));
        list.add(new RewardDto("R2", "Hábito de hierro", "Alcanza 30 completions", "🛡️", totalCompletions >= 30));
        list.add(new RewardDto("R3", "Productivo", "Completa 10 tareas", "⚡", tasksDone >= 10));
        list.add(new RewardDto("R4", "Bienestar", "Registra 5 comidas", "🥗", mealsRepository.findByUserId(uid).size() >= 5));
        list.add(new RewardDto("R5", "Maestro del hogar", "Balance >= 80%", "🏆", (balance(email).balance() >= 80)));
        return list;
    }

    private int computeStreak(Long userId, LocalDate date) {
        int streak = 0;
        LocalDate cursor = date;
        while (true) {
            var c = completionRepository.findByHabitUserIdAndDateBetween(userId, cursor, cursor);
            if (c == null || c.isEmpty()) break;
            streak++;
            cursor = cursor.minusDays(1);
            if (streak > 365) break;
        }
        return streak;
    }
}
