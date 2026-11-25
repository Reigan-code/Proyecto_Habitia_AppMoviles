
package upeu.edu.pe.HABITIA_Back.modules.shopping.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.ShoppingItem;

import java.util.List;
import java.util.Optional;

public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {
    List<ShoppingItem> findByShoppingList_User_IdAndCompleted(
            Long userId, Boolean completed);
    Optional<ShoppingItem> findByIdAndShoppingList_Id(
            Long itemId, Long shoppingListId);
    List<ShoppingItem> findByShoppingList_User_Id(Long userId);
}
