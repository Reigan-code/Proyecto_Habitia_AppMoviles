package upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.MealType;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.Meals;

import java.time.LocalDate;
import java.util.List;

public interface MealsRepository extends JpaRepository<Meals, Long> {
    List<Meals> findByUserId(Long userId);

    List<Meals>findByUserIdAndDate(Long userId, LocalDate date);

    List<Meals>findByUserIdAndDateBetween(
            Long userId,
            LocalDate inicio,
            LocalDate fin
    );
    List<Meals>findByUserIdAndType(
            Long userId,
            MealType type
    );
}
