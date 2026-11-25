
package upeu.edu.pe.HABITIA_Back.modules.user.infrastructure.Controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import upeu.edu.pe.HABITIA_Back.modules.user.application.dto.UserCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.user.application.dto.UserResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.user.application.dto.UserUpdateDto;
import upeu.edu.pe.HABITIA_Back.modules.user.application.service.UserService;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.net.URI;

@RestController
@RequestMapping(path = "Users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    //ver tu perfil, funciona si eres tu; sino pues 404
    @GetMapping(path = "{id}")
    public ResponseEntity<UserResponseDto> get(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("GET: user profile requested");
        return userService.consultar(id)
                .filter(user -> user.getEmail().equals(authentication.getName()))
                .map(user -> ResponseEntity.ok(toResponseDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //perfil por correo (login); sino pues 404
    @GetMapping(path = "email/{email}")
    public ResponseEntity<UserResponseDto> getByEmail(
            @PathVariable String email,
            Authentication authentication) {log.info("GET: profile lookup by email requested");

        if (!email.equalsIgnoreCase(authentication.getName())) {
            return ResponseEntity.notFound().build();}

        return userService.findByEmail(authentication.getName())
                .map(user -> ResponseEntity.ok(toResponseDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    //guarda perfil (nombre/cumple/telefono); sino pues 404
    @PutMapping(path = "{id}")
    public ResponseEntity<UserResponseDto> put(
            @Valid @RequestBody UserUpdateDto userDTO,
            @PathVariable Long id, Authentication authentication) {
        log.info("PUT: user profile update requested");
        return userService.consultar(id)
                .filter(user -> user.getEmail().equals(authentication.getName()))
                .map(existingUser -> {
                    existingUser.setName(userDTO.getName());
                    existingUser.setBirthDate(userDTO.getBirthDate());
                    existingUser.setPhone(userDTO.getPhone());
                    User updatedUser = userService.update(existingUser);
                    return ResponseEntity.ok(toResponseDTO(updatedUser));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private UserResponseDto toResponseDTO(User user) {
        return new UserResponseDto(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirthDate(),
                user.getPhone());
    }



//

/*
@DeleteMapping(path = "{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {

    log.info("DELETE: user with ID {}", id);

    if (userService.consultar(id).isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    userService.delete(id);

    return ResponseEntity.noContent().build();
}
*/

}
