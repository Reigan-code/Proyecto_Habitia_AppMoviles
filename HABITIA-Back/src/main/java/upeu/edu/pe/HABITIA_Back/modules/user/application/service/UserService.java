package upeu.edu.pe.HABITIA_Back.modules.user.application.service;

import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User>findByEmail(String email);
    User crear(User user);
    Optional<User>consultar(Long userId);
    User update(User user);
    void delete(Long userId);
}
