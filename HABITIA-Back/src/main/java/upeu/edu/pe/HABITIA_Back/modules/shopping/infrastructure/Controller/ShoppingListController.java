
package upeu.edu.pe.HABITIA_Back.modules.shopping.infrastructure.Controller;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto.*;
import upeu.edu.pe.HABITIA_Back.modules.shopping.application.service.ShoppingListService;
import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.ShoppingItem;
import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.ShoppingList;

import java.util.List;

@RestController
@RequestMapping("/ShoppingLists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(
            ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    //creacion de la lista de compras desde tus comidas; sino pues vacia
    @PostMapping("/generate")
    public ResponseEntity<ShoppingListResponseDto> generar(
            @Valid @RequestBody GenerateShoppingListDto request,
            Authentication authentication) {

        String emailUsuario = authentication.getName();

        ShoppingList list = shoppingListService.generar(
                request,
                emailUsuario
        );

        return ResponseEntity.ok(toResponseDTO(list));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ShoppingListResponseDto>> listar(
            @PathVariable Long userId,
            Authentication authentication) {

        String emailUsuario = authentication.getName();

        List<ShoppingListResponseDto> lists =
                shoppingListService.listarPorUsuario(emailUsuario)
                        .stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(lists);
    }

    @GetMapping("/{listId}")
    public ResponseEntity<ShoppingListResponseDto> consultar(
            @PathVariable Long listId,
            @RequestParam(required = false) Long userId,
            Authentication authentication) {

        String emailUsuario = authentication.getName();

        ShoppingList list = shoppingListService.consultar(
                listId,
                emailUsuario
        );

        return ResponseEntity.ok(toResponseDTO(list));
    }

    //agrega ingrediente a la lista, funciona si hay lista; sino pues la crea
    @PostMapping("/{listId}/items")
    public ResponseEntity<ShoppingListResponseDto> agregarManual(
            @PathVariable Long listId,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody ShoppingItemCreateDto request,
            Authentication authentication) {

        String emailUsuario = authentication.getName();

        ShoppingList list = shoppingListService.agregarManual(
                listId,
                request,
                emailUsuario
        );

        return ResponseEntity.ok(toResponseDTO(list));
    }

    //marca comprado el ingrediente; sino pues 404
    @PutMapping("/{listId}/items/{itemId}")
    public ResponseEntity<ShoppingListResponseDto> actualizarItem(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @RequestParam(required = false) Long userId,
            @Valid @RequestBody ShoppingItemUpdateDto request,
            Authentication authentication) {

        String emailUsuario = authentication.getName();

        ShoppingList list = shoppingListService.actualizarItem(
                listId,
                itemId,
                request,
                emailUsuario
        );

        return ResponseEntity.ok(toResponseDTO(list));
    }

    //saca ingrediente de la lista; sino pues 404
    @DeleteMapping("/{listId}/items/{itemId}")
    public ResponseEntity<ShoppingListResponseDto> eliminarItem(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @RequestParam(required = false) Long userId,
            Authentication authentication) {

        String emailUsuario = authentication.getName();

        ShoppingList list = shoppingListService.eliminarItem(
                listId,
                itemId,
                emailUsuario
        );

        return ResponseEntity.ok(toResponseDTO(list));
    }

    @PatchMapping("/{listId}/confirm")
    public ResponseEntity<ShoppingListResponseDto> confirmar(
            @PathVariable Long listId,
            @RequestParam(required = false) Long userId,
            Authentication authentication) {

        String emailUsuario = authentication.getName();

        ShoppingList list = shoppingListService.confirmar(
                listId,
                emailUsuario
        );

        return ResponseEntity.ok(toResponseDTO(list));
    }

    private ShoppingListResponseDto toResponseDTO(ShoppingList list) {

        List<ShoppingItemResponseDto> items = list.getItems()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return new ShoppingListResponseDto(
                list.getId(),
                list.getStartDate(),
                list.getEndDate(),
                list.getStatus(),
                items
        );
    }

    private ShoppingItemResponseDto toResponseDTO(ShoppingItem item) {

        return new ShoppingItemResponseDto(
                item.getId(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnit(),
                item.getSource(),
                item.getCompleted(),
                item.getItemDate(),
                item.getSourceMealType(),
                item.getSourceMealName()
        );
    }
}
