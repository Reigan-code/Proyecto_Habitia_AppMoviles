package upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
