package upeu.edu.pe.HABITIA_Back.modules.reminder.domain.model;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.persistence.*;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.RepeatType;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id", nullable = false)
    private User user;

    private String title;

    private String description;

    private LocalDateTime reminderDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type")
    private RepeatType repeat;


    private Boolean completed;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }

    public RepeatType getRepeat() {
        return repeat;
    }

    public void setRepeat(RepeatType repeat) {
        this.repeat = repeat;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "ReminderEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", reminderDate=" + reminderDate +
                ", repeat=" + repeat +
                ", completed=" + completed +
                '}';
    }
}