package upeu.edu.pe.HABITIA_Back.modules.shopping.application.service;

import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.ShoppingItem;

import java.time.LocalDate;
import java.util.List;

public interface ShoppingItemService {
ShoppingItem crear(ShoppingItem shopItem);
ShoppingItem update(ShoppingItem shopItem);
void delete(Long shopitemId);
List<ShoppingItem>findByUser(Long userId);
List<ShoppingItem> generarListCompras(Long userId, LocalDate inicio, LocalDate fin);

}
