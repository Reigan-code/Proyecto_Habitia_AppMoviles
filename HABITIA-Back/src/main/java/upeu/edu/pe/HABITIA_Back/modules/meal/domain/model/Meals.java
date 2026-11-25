package upeu.edu.pe.HABITIA_Back.modules.meal.domain.model;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.persistence.*;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.MealType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Meals")
public class Meals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id", nullable = false)
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private MealType type;

    private LocalDate date;

    private LocalTime time;

    private String description;

    private Boolean completed;

    @OneToMany(mappedBy = "meals")
    private List<MealIngredient> ingredients;

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

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public List<MealIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<MealIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "MealEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                '}';
    }
}