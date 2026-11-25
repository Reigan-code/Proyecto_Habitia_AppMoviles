
package upeu.edu.pe.HABITIA_Back.modules.habit.domain.model;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.persistence.*;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.Frequency;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Habits")
public class Habit {

    // Identificador único generado automáticamente por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchos hábitos pueden pertenecer a un mismo usuario.
    // La columna id_user guarda la clave foránea hacia User.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    // Nombre obligatorio del hábito.
    @Column(nullable = false, length = 100)
    private String name;

    // Descripción opcional del hábito.
    @Column(length = 500)
    private String description;

    // Guarda el nombre del enum, por ejemplo DAILY o WEEKLY,
    // según los valores que existan realmente en Frequency.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Frequency frequency;

    // Fecha desde la que el usuario empieza a realizar el hábito.
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // Fecha final opcional. Null significa que no se ha definido
    // una fecha de finalización.
    @Column(name = "end_date")
    private LocalDate endDate;

    // Permite activar o desactivar el hábito sin eliminarlo.
    @Column(nullable = false)
    private Boolean active = true;

    // Un hábito puede tener muchos registros de cumplimiento.
    // mappedBy indica que la relación se define en HabitCompletion.
    // No se cargan todos los cumplimientos automáticamente.
    @OneToMany(mappedBy = "habit", fetch = FetchType.LAZY)
    private List<HabitCompletion> completions = new ArrayList<>();

    // Constructor vacío requerido por JPA.
    public Habit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<HabitCompletion> getCompletions() {
        return completions;
    }

    public void setCompletions(List<HabitCompletion> completions) {
        this.completions = completions;
    }

    // No incluimos User ni completions para evitar recursividad
    // y problemas al registrar la entidad en los logs.
    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", frequency=" + frequency +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                '}';
    }
}
