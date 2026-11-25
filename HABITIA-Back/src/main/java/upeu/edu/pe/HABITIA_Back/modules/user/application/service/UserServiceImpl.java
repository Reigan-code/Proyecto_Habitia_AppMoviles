package upeu.edu.pe.HABITIA_Back.modules.user.application.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.util.Optional;

//perfil: guarda nombre/cumple/telefono; la clave se cifra con BCrypt
@Service
@Transactional
//perfil: guarda nombre/cumple/telefono; la clave se cifra con BCrypt
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User crear(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> consultar(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
