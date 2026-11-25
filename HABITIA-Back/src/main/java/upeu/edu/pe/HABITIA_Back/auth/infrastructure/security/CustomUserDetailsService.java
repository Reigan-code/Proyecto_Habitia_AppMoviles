
package upeu.edu.pe.HABITIA_Back.auth.infrastructure.security;
//login: busca usuario por correo para el JWT; si no existe pues 401

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Permite buscar usuarios en la base de datos mediante el repositorio existente.
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Busca al usuario por correo sin modificar datos de la base de datos.
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                // Si el correo no existe, informa que no se encontró la cuenta.
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "No se encontró una cuenta con ese correo."
                        )
                );

        // Adapta el usuario de HABITIA al formato que Spring Security necesita
        // para autenticarlo. La contraseña debe estar almacenada como hash BCrypt.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                // Asigna al usuario la autoridad básica ROLE_USER.
                .authorities("ROLE_USER")
                .build();
    }
}
