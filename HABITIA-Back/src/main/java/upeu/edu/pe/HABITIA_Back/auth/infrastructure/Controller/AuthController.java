package upeu.edu.pe.HABITIA_Back.auth.infrastructure.Controller;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import upeu.edu.pe.HABITIA_Back.modules.user.application.dto.UserCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.user.application.dto.UserResponseDto;
import upeu.edu.pe.HABITIA_Back.auth.infrastructure.security.JwtService;
import upeu.edu.pe.HABITIA_Back.auth.infrastructure.web.dto.LoginRequestDto;
import upeu.edu.pe.HABITIA_Back.auth.infrastructure.web.dto.LoginResponseDto;
import upeu.edu.pe.HABITIA_Back.modules.user.application.service.UserService;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    //crea cuenta con clave cifrada; si el correo ya esta pues 409
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserCreateDto userDTO) {

        if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El correo electrónico ya está registrado.");}

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setBirthDate(userDTO.getBirthDate());
        User createdUser = userService.crear(user);
        UserResponseDto response = new UserResponseDto(
                createdUser.getId(),
                createdUser.getName(),
                createdUser.getEmail(),
                createdUser.getBirthDate(),
                createdUser.getPhone());
        return ResponseEntity
                .created(URI.create("/habitia-backend/Users/" + createdUser.getId())).body(response);
    }



    //devuelve JWT, funciona si la clave esta bien; sino pues 401
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
        String email = authentication.getName();
        String token = jwtService.generateToken(email);
        LoginResponseDto response = new LoginResponseDto(token, "Bearer");
        return ResponseEntity.ok(response);
    }

}
