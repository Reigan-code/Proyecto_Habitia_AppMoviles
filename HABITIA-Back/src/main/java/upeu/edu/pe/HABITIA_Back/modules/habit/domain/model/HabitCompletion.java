
package upeu.edu.pe.HABITIA_Back.modules.habit.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "Habit_complet",
        uniqueConstraints = {
                // No puede repetirse el mismo hábito en la misma fecha.
                @UniqueConstraint(
                        name = "unico_habito_fecha",
                        columnNames = {"id_habit", "date"}
                )
        }
)
public class HabitCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchos registros de cumplimiento pueden pertenecer a un hábito.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_habit", nullable = false)
    private Habit habit;

    // Día al que corresponde este registro.
    @Column(name = "date", nullable = false)
    private LocalDate date;

    // true = cumplido; false = no cumplido.
    @Column(nullable = false)
    private Boolean completed;

    public HabitCompletion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "HabitCompletion{" +
                "id=" + id +
                ", date=" + date +
                ", completed=" + completed +
                '}';
    }
}
