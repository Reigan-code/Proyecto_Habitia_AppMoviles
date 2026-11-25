
package upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto;

import upeu.edu.pe.HABITIA_Back.shared.domain.enums.IngredientUnit;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.ShoppingItemSource;

import java.math.BigDecimal;

public record ShoppingItemResponseDto(
        Long id,
        String description,
        BigDecimal quantity,
        IngredientUnit unit,
        ShoppingItemSource source,
        Boolean completed,
        java.time.LocalDate itemDate,
        String sourceMealType,
        String sourceMealName
) {}
