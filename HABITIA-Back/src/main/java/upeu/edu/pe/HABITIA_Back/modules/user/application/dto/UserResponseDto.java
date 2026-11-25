package upeu.edu.pe.HABITIA_Back.modules.user.application.dto;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;
import java.time.LocalDate;


public class UserResponseDto {
    // Datos que el backend puede devolver al cliente.
    // No incluimos password ni relaciones con otras entidades
    // para evitar exponer información sensible o innecesaria.
    private Long id;
    private String name;
    private String email;
    private LocalDate birthDate;
    private String phone;

    public UserResponseDto() {
    }
    // Permite construir el DTO con los datos seleccionados
    // de la entidad User.
    public UserResponseDto(
            Long id,
            String name,
            String email,
            LocalDate birthDate,
            String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
