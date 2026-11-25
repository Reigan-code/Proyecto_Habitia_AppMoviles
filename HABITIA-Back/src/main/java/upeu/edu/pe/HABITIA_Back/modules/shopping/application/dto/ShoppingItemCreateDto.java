
package upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShoppingItemCreateDto(
        @NotBlank
        @Size(max = 255)
        String description,

        java.time.LocalDate itemDate,

        String sourceMealType,

        String sourceMealName
) {}
