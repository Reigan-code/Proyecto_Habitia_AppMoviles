package upeu.edu.pe.HABITIA_Back.modules.meal.application.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealsRepository;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.Meals;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//platos: crea/lee/edita/borra solo lo tuyo; si no es tuyo pues 404
@Service
@Transactional
//platos: crea/lee/edita/borra solo lo tuyo; si no es tuyo pues 404
public class MealsServiceImpl implements MealsService {

    private final MealsRepository mealsRepository;
    private final UserRepository userRepository;

    public MealsServiceImpl(MealsRepository mealsRepository, UserRepository userRepository) {
        this.mealsRepository = mealsRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Meals crear(Meals meals, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe el usuario con ID: " + userId
                        )
                );

        meals.setUser(user);

        if (meals.getCompleted() == null) {
            meals.setCompleted(false);
        }

        return mealsRepository.save(meals);
    }
    @Override
    public Optional<Meals> consultar(Long mealsId) {
        return mealsRepository.findById(mealsId);
    }

    @Override
    public Meals update(Meals meals) {
        return mealsRepository.save(meals);
    }

    @Override
    public void delete(Long mealsId) {
        mealsRepository.deleteById(mealsId);
    }

    @Override
    public List<Meals> findByUserId(Long userId) {
        return mealsRepository.findByUserId(userId);
    }

    @Override
    public List<Meals> findByDate(Long userId, LocalDate date) {
        return mealsRepository.findByUserIdAndDate(userId, date);
    }

    @Override
    public List<Meals> findByDateBetween(
            Long userId,
            LocalDate inicio,
            LocalDate fin) {

        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser posterior a la fecha de fin"
            );
        }

        return mealsRepository.findByUserIdAndDateBetween(
                userId, inicio, fin
        );
    }
}