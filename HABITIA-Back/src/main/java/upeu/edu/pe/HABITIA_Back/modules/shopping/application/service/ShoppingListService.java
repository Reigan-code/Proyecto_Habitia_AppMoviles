
package upeu.edu.pe.HABITIA_Back.modules.shopping.application.service;

import upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto.GenerateShoppingListDto;
import upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto.ShoppingItemCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto.ShoppingItemUpdateDto;
import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.ShoppingList;

import java.util.List;

public interface ShoppingListService {
    ShoppingList generar(GenerateShoppingListDto request, String emailUsuario);
    List<ShoppingList> listarPorUsuario(String emailUsuario);
    ShoppingList consultar(Long listId, String emailUsuario);
    // Agrega un artículo manualmente a una lista propia.
    ShoppingList agregarManual(Long listId, ShoppingItemCreateDto request, String emailUsuario);
    // Actualiza un artículo de una lista propia.
    ShoppingList actualizarItem(Long listId, Long itemId, ShoppingItemUpdateDto request, String emailUsuario);
    ShoppingList eliminarItem(Long listId, Long itemId, String emailUsuario);
    ShoppingList confirmar(Long listId, String emailUsuario);
}
