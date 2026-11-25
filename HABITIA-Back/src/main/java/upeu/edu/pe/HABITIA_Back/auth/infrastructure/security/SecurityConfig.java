
package upeu.edu.pe.HABITIA_Back.auth.infrastructure.security;
//seguridad: /auth es publico, lo demas pide JWT; sin sesiones

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Permite cifrar contraseñas nuevas y comprobarlas durante el login.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Proporciona el administrador de autenticación de Spring Security.
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        // El filtro JWT se integra aquí y no como filtro general del servidor.
        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(jwtService, userDetailsService);

        // No se utilizarán sesiones HTTP para mantener la autenticación.
        http.sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // La API recibe el JWT mediante Authorization, no mediante cookies.
                .csrf(csrf -> csrf.disable())

                // Solo las rutas de autenticación son públicas por ahora.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )

                // Desactiva los mecanismos de autenticación que no utilizaremos.
                .formLogin(form -> form.disable())

                // Antes: sin token daba 403 (Spring redirigia a /error, tambien protegido).
                // Ahora: 401 con mensaje claro para Postman y el front.
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                "{\"error\":\"No autorizado\",\"message\":\"Token requerido o invalido. Corre primero POST /auth/login.\"}"
                            );
                        }
                ))
                .httpBasic(basic -> basic.disable())

                // Valida el JWT antes del filtro de autenticación estándar.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
