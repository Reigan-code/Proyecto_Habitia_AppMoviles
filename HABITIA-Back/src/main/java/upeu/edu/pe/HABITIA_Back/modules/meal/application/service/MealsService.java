package upeu.edu.pe.HABITIA_Back.modules.meal.application.service;

import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.Meals;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealsService {
    Meals crear(Meals meals, Long userId);
    Optional<Meals> consultar(Long mealsId);
    Meals update(Meals meals);
    void delete(Long mealsId);
    List<Meals> findByUserId(Long userId);
    List<Meals> findByDate(Long userId, LocalDate date);
    List<Meals> findByDateBetween(
            Long userId,
            LocalDate inicio,
            LocalDate fin
    );
}