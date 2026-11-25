
package upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GenerateShoppingListDto(
        @NotNull Long userId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {

}
