
package upeu.edu.pe.HABITIA_Back.modules.shopping.application.service;
//armar lista de compras: junta ingredientes de tus comidas, agrupa por nombre+unidad y suma cantidades; si no hay comidas pues lista vacia
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto.GenerateShoppingListDto;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.MealIngredient;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.Meals;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.service.InterpreteDeIngredientes;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealsRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealIngredientRepository;
import upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto.ShoppingItemCreateDto;
import upeu.edu.pe.HABITIA_Back.modules.shopping.application.dto.ShoppingItemUpdateDto;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.IngredientUnit;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.ShoppingItemSource;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.ShoppingListStatus;
import upeu.edu.pe.HABITIA_Back.modules.shopping.infrastructure.persistence.*;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.*;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository listRepository;
    private final ShoppingItemRepository itemRepository;
    private final MealsRepository mealsRepository;
    private final MealIngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final InterpreteDeIngredientes interpreteDeIngredientes;

    public ShoppingListServiceImpl(
            ShoppingListRepository listRepository,
            ShoppingItemRepository itemRepository,
            MealsRepository mealsRepository,
            MealIngredientRepository ingredientRepository,
            UserRepository userRepository,
            InterpreteDeIngredientes interpreteDeIngredientes) {

        this.listRepository = listRepository;
        this.itemRepository = itemRepository;
        this.mealsRepository = mealsRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.interpreteDeIngredientes = interpreteDeIngredientes;
    }

    // Obtiene al usuario a partir del correo contenido en el JWT.
    // No confiamos en identificadores enviados por el cliente.
    private User obtenerUsuarioAutenticado(String emailUsuario) {
        return userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    @Override
    public ShoppingList generar(
            GenerateShoppingListDto request,
            String emailUsuario) {

        // La identidad proviene del usuario autenticado, no del DTO.
        User user = obtenerUsuarioAutenticado(emailUsuario);
        Long userId = user.getId();

        if (request.startDate().isAfter(request.endDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha inicial no puede ser posterior a la final"
            );
        }

        // Reutilizamos la lista DRAFT del usuario para el mismo periodo,
        // si existe; de lo contrario, creamos una nueva.
        ShoppingList list = listRepository
                .findFirstByUser_IdAndStartDateAndEndDateAndStatusOrderByIdDesc(
                        userId,
                        request.startDate(),
                        request.endDate(),
                        ShoppingListStatus.DRAFT
                )
                .orElseGet(() -> {
                    ShoppingList newList = new ShoppingList();
                    newList.setUser(user);
                    newList.setStartDate(request.startDate());
                    newList.setEndDate(request.endDate());
                    newList.setStatus(ShoppingListStatus.DRAFT);
                    return newList;
                });

        // Al regenerar, sustituimos solamente los artículos del menú.
        // Los artículos agregados manualmente se conservan.
        List<ShoppingItem> oldMenuItems = list.getItems().stream()
                .filter(item -> item.getSource() == ShoppingItemSource.MENU)
                .toList();

        itemRepository.deleteAll(oldMenuItems);
        list.getItems().removeAll(oldMenuItems);

        // Solo utilizamos las comidas del usuario autenticado.
        List<Meals> meals = mealsRepository.findByUserIdAndDateBetween(
                userId,
                request.startDate(),
                request.endDate()
        );

        Map<String, IngredientAccumulator> grouped = new LinkedHashMap<>();

        for (Meals meal : meals) {
            List<MealIngredient> ingredients =
                    ingredientRepository.findByMealsId(meal.getId());

            for (MealIngredient ingredient : ingredients) {
                InterpreteDeIngredientes.ParsedIngredient parsed =
                        readIngredient(ingredient);

                addToGroup(grouped, parsed);
            }
        }

        // Creamos los artículos consolidados de las comidas.
        for (IngredientAccumulator accumulated : grouped.values()) {
            ShoppingItem item = new ShoppingItem();
            item.setShoppingList(list);
            item.setDescription(accumulated.description);
            item.setSource(ShoppingItemSource.MENU);
            item.setCompleted(false);

            setDisplayQuantity(item, accumulated);
            list.getItems().add(item);
        }

        return listRepository.save(list);
    }

    @Override
    public List<ShoppingList> listarPorUsuario(String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);

        return listRepository.findByUser_IdOrderByStartDateDesc(user.getId());
    }

    @Override
    public ShoppingList consultar(Long listId, String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);

        return obtenerLista(user.getId(), listId);
    }

    @Override
    public ShoppingList agregarManual(
            Long listId,
            ShoppingItemCreateDto request,
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);
        ShoppingList list = obtenerLista(user.getId(), listId);
        exigirBorrador(list);

        // Interpretamos el texto recibido.
        InterpreteDeIngredientes.ParsedIngredient parsed =
                interpreteDeIngredientes.parse(request.description());

        // Buscamos un ingrediente existente con nombre y unidad compatibles.
        for (ShoppingItem existing : list.getItems()) {

            if (existing.getQuantity() == null || existing.getUnit() == null) {
                continue;
            }

            String existingName =
                    interpreteDeIngredientes.keyName(existing.getDescription());

            String newName =
                    interpreteDeIngredientes.keyName(parsed.description());

            if (!existingName.equals(newName)) {
                continue;
            }

            String existingDimension = dimensionOf(existing.getUnit());
            String newDimension = dimensionOf(parsed.unit());

            if (!existingDimension.equals(newDimension)) {
                continue;
            }

            // Convertimos las cantidades a la unidad base antes de sumarlas.
            BigDecimal existingQuantity = toBaseQuantity(
                    existing.getQuantity(),
                    existing.getUnit()
            );

            BigDecimal newQuantity = toBaseQuantity(
                    parsed.quantity(),
                    parsed.unit()
            );

            BigDecimal total = existingQuantity.add(newQuantity);

            IngredientAccumulator accumulated = new IngredientAccumulator(
                    existing.getDescription(),
                    existingDimension,
                    total
            );

            setDisplayQuantity(existing, accumulated);
            existing.setCompleted(false);

            return listRepository.save(list);
        }

        // Si no existe un artículo compatible, agregamos uno manual.
        ShoppingItem item = new ShoppingItem();
        item.setShoppingList(list);
        item.setDescription(parsed.description());
        item.setQuantity(parsed.quantity());
        item.setUnit(parsed.unit());
        item.setSource(ShoppingItemSource.MANUAL);
        item.setCompleted(false);
        item.setItemDate(request.itemDate());
        item.setSourceMealType(request.sourceMealType());
        item.setSourceMealName(request.sourceMealName());

        list.getItems().add(item);

        return listRepository.save(list);
    }

    @Override
    public ShoppingList actualizarItem(
            Long listId,
            Long itemId,
            ShoppingItemUpdateDto request,
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);
        ShoppingList list = obtenerLista(user.getId(), listId);
        exigirBorrador(list);

        // El artículo debe pertenecer a la lista indicada.
        ShoppingItem item = itemRepository
                .findByIdAndShoppingList_Id(itemId, listId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Artículo no encontrado"
                ));

        InterpreteDeIngredientes.ParsedIngredient parsed =
                interpreteDeIngredientes.parse(request.description());

        item.setDescription(parsed.description());
        item.setQuantity(parsed.quantity());
        item.setUnit(parsed.unit());

        if (request.completed() != null) {
            item.setCompleted(request.completed());
        }

        return listRepository.save(list);
    }

    @Override
    public ShoppingList eliminarItem(
            Long listId,
            Long itemId,
            String emailUsuario) {

        User user = obtenerUsuarioAutenticado(emailUsuario);
        ShoppingList list = obtenerLista(user.getId(), listId);
        exigirBorrador(list);

        ShoppingItem item = itemRepository
                .findByIdAndShoppingList_Id(itemId, listId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Artículo no encontrado"
                ));

        list.getItems().remove(item);
        itemRepository.delete(item);

        return listRepository.save(list);
    }

    @Override
    public ShoppingList confirmar(Long listId, String emailUsuario) {
        User user = obtenerUsuarioAutenticado(emailUsuario);
        ShoppingList list = obtenerLista(user.getId(), listId);
        exigirBorrador(list);

        list.setStatus(ShoppingListStatus.CONFIRMED);
        return listRepository.save(list);
    }

    private InterpreteDeIngredientes.ParsedIngredient readIngredient(
            MealIngredient ingredient) {

        // Compatibilidad con registros antiguos que guardan
        // la cantidad y la unidad dentro de la descripción.
        if (ingredient.getQuantity() == null || ingredient.getUnit() == null) {
            return interpreteDeIngredientes.parse(ingredient.getDescription());
        }

        return new InterpreteDeIngredientes.ParsedIngredient(
                ingredient.getDescription(),
                ingredient.getQuantity(),
                ingredient.getUnit()
        );
    }

    private void addToGroup(
            Map<String, IngredientAccumulator> grouped,
            InterpreteDeIngredientes.ParsedIngredient ingredient) {

        String dimension = dimensionOf(ingredient.unit());
        String normalizedName =
                interpreteDeIngredientes.keyName(ingredient.description());

        String key = dimension + "|" + normalizedName;

        BigDecimal baseQuantity = toBaseQuantity(
                ingredient.quantity(),
                ingredient.unit()
        );

        IngredientAccumulator accumulator = grouped.get(key);

        if (accumulator == null) {
            accumulator = new IngredientAccumulator(
                    ingredient.description(),
                    dimension,
                    baseQuantity
            );

            grouped.put(key, accumulator);
        } else {
            accumulator.baseQuantity =
                    accumulator.baseQuantity.add(baseQuantity);
        }
    }

    private String dimensionOf(IngredientUnit unit) {
        return switch (unit) {
            case G, KG -> "MASS";
            case ML, L -> "VOLUME";
            case UNIT -> "COUNT";
        };
    }

    private BigDecimal toBaseQuantity(
            BigDecimal quantity,
            IngredientUnit unit) {

        return switch (unit) {
            case KG, L -> quantity.multiply(BigDecimal.valueOf(1000));
            case G, ML, UNIT -> quantity;
        };
    }

    private void setDisplayQuantity(
            ShoppingItem item,
            IngredientAccumulator accumulator) {

        BigDecimal quantity = accumulator.baseQuantity;

        switch (accumulator.dimension) {
            case "MASS" -> {
                if (quantity.compareTo(BigDecimal.valueOf(1000)) >= 0) {
                    item.setQuantity(
                            quantity.divide(BigDecimal.valueOf(1000))
                    );
                    item.setUnit(IngredientUnit.KG);
                } else {
                    item.setQuantity(quantity);
                    item.setUnit(IngredientUnit.G);
                }
            }

            case "VOLUME" -> {
                if (quantity.compareTo(BigDecimal.valueOf(1000)) >= 0) {
                    item.setQuantity(
                            quantity.divide(BigDecimal.valueOf(1000))
                    );
                    item.setUnit(IngredientUnit.L);
                } else {
                    item.setQuantity(quantity);
                    item.setUnit(IngredientUnit.ML);
                }
            }

            default -> {
                item.setQuantity(quantity);
                item.setUnit(IngredientUnit.UNIT);
            }
        }
    }

    // Solo recuperamos una lista que pertenezca al usuario autenticado.
    private ShoppingList obtenerLista(Long userId, Long listId) {
        return listRepository.findByIdAndUser_Id(listId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lista no encontrada"
                ));
    }

    // Las listas confirmadas ya no se pueden modificar.
    private void exigirBorrador(ShoppingList list) {
        if (list.getStatus() != ShoppingListStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La lista está confirmada y ya no se puede editar"
            );
        }
    }

    private static class IngredientAccumulator {
        private final String description;
        private final String dimension;
        private BigDecimal baseQuantity;

        private IngredientAccumulator(
                String description,
                String dimension,
                BigDecimal baseQuantity) {
            this.description = description;
            this.dimension = dimension;
            this.baseQuantity = baseQuantity;
        }
    }
}
