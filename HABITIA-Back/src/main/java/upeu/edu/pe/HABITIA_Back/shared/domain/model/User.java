package upeu.edu.pe.HABITIA_Back.shared.domain.model;

import upeu.edu.pe.HABITIA_Back.modules.calendar.domain.model.CalendarEvent;
import upeu.edu.pe.HABITIA_Back.modules.habit.domain.model.Habit;
import upeu.edu.pe.HABITIA_Back.modules.reminder.domain.model.Reminder;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.Meals;
import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.ShoppingList;
import upeu.edu.pe.HABITIA_Back.modules.task.domain.model.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private LocalDate birthDate;
    private String phone;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Task> tasks;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Habit> habits;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Reminder> reminders;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Meals> meals;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ShoppingList> shoppingLists;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<CalendarEvent> calendarEvents;
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
    @JsonIgnore
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public List<Task> getTasks() {
        return tasks;
    }
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    public List<Habit> getHabits() {
        return habits;
    }
    public void setHabits(List<Habit> habits) {
        this.habits = habits;
    }
    public List<Reminder> getReminders() {
        return reminders;
    }
    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
    public List<Meals> getMeals() {
        return meals;
    }
    public void setMeals(List<Meals> meals) {
        this.meals = meals;
    }
    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }
    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }
    public List<CalendarEvent> getCalendarEvents() {
        return calendarEvents;
    }
    public void setCalendarEvents(List<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }
}