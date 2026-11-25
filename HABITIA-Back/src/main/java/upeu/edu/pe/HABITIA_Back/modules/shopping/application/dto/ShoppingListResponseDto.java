package upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto;

import upeu.edu.pe.HABITIA_Back.shared.domain.enums.ShoppingListStatus;

import java.time.LocalDate;
import java.util.List;

// DTO que define la estructura de respuesta
// de una lista de compras.
public record ShoppingListResponseDto(

        // Identificador único de la lista de compras.
        Long id,

        // Fecha de inicio del periodo al que corresponde la lista.
        LocalDate startDate,

        // Fecha de finalización del periodo de la lista.
        LocalDate endDate,

        // Estado actual de la lista.
        // Sus valores dependen del enum ShoppingListStatus.
        // Por ejemplo: PENDING o CONFIRMED, si existen en el enum.
        ShoppingListStatus status,

        // Lista de artículos de compra asociados.
        // Cada elemento contiene los datos definidos
        // en ShoppingItemResponseDto.
        List<ShoppingItemResponseDto> items

) {}