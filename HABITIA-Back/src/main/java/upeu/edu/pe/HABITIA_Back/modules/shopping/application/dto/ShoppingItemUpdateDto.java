package upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO que define los datos permitidos al actualizar
// un artículo de la lista de compras.
public record ShoppingItemUpdateDto(

        // La descripción es obligatoria:
        // no puede ser null, estar vacía ni contener
        // únicamente espacios en blanco.
        @NotBlank

        // Limita la descripción a un máximo de 255 caracteres.
        @Size(max = 255)
        String description,

        // Indica si el artículo ya fue comprado.
        // true = completado; false = pendiente.
        // Al no tener una restricción, puede ser null.
        Boolean completed

) {}