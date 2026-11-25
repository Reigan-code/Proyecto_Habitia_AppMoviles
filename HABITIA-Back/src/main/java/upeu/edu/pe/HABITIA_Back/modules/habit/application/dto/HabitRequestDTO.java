
package upeu.edu.pe.HABITIA_Back.modules.habit.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.Frequency;

import java.time.LocalDate;

public class HabitRequestDTO {

    // Nombre obligatorio del hábito, con un máximo de 100 caracteres.
    @NotBlank(message = "El nombre del hábito es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    // Descripción opcional del hábito.
    private String description;

    // Frecuencia obligatoria: por ejemplo, diaria o semanal,
    // según los valores definidos en el enum Frequency.
    @NotNull(message = "La frecuencia es obligatoria")
    private Frequency frequency;

    // Fecha obligatoria desde la que comienza el hábito.
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    // Fecha final opcional.
    private LocalDate endDate;

    // Al crear, si no se envía este valor, el servicio
    // podrá asignar true por defecto.
    private Boolean active;

    public HabitRequestDTO() {
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
}
