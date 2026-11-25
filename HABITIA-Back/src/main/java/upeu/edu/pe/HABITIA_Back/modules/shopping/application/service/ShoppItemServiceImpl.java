package upeu.edu.pe.HABITIA_Back.modules.shopping.application.service;
//items de compra: alta/baja/cambio de comprado
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealIngredientRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.infrastructure.persistence.MealsRepository;
import upeu.edu.pe.HABITIA_Back.modules.shopping.infrastructure.persistence.ShoppingItemRepository;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.MealIngredient;
import upeu.edu.pe.HABITIA_Back.modules.meal.domain.model.Meals;
import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.ShoppingItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Indica que esta clase pertenece a la capa de servicios.
// Aquí se implementa la lógica de negocio de los artículos de compra.
@Service

// Gestiona las operaciones dentro de una transacción.
// Si ocurre un error en una operación transaccional,
// los cambios realizados pueden revertirse.
@Transactional
public class ShoppItemServiceImpl implements ShoppingItemService {

    // Repositorio para guardar, actualizar, eliminar
    // y consultar artículos de compra.
    private final ShoppingItemRepository shoppingItemRepository;

    // Repositorio para consultar las comidas
    // registradas por los usuarios.
    private final MealsRepository mealsRepository;

    // Repositorio para consultar los ingredientes
    // asociados a cada comida.
    private final MealIngredientRepository mealIngredientRepository;

    // Inyección de dependencias por constructor.
    // Spring proporciona las implementaciones de los repositorios.
    public ShoppItemServiceImpl(
            ShoppingItemRepository shoppingItemRepository,
            MealsRepository mealsRepository,
            MealIngredientRepository mealIngredientRepository) {

        this.shoppingItemRepository = shoppingItemRepository;
        this.mealsRepository = mealsRepository;
        this.mealIngredientRepository = mealIngredientRepository;
    }

    // CREA UN ARTÍCULO DE COMPRA
    @Override
    public ShoppingItem crear(ShoppingItem shoppingItem) {

        // Guarda el artículo recibido en la base de datos
        // y devuelve la entidad guardada.
        return shoppingItemRepository.save(shoppingItem);
    }

    // ACTUALIZA UN ARTÍCULO DE COMPRA
    @Override
    public ShoppingItem update(ShoppingItem shoppingItem) {

        // Guarda la entidad con sus datos actualizados.
        // Si corresponde a un registro existente, normalmente
        // persistirá sus cambios.
        return shoppingItemRepository.save(shoppingItem);
    }

    // ELIMINA UN ARTÍCULO DE COMPRA
    @Override
    public void delete(Long shoppingItemId) {

        // Elimina el artículo utilizando su identificador.
        shoppingItemRepository.deleteById(shoppingItemId);
    }

    // CONSULTA LOS ARTÍCULOS DE COMPRA DE UN USUARIO
    @Override
    public List<ShoppingItem> findByUser(Long userId) {

        // Busca los artículos cuya lista de compras
        // pertenece al usuario indicado.
        // La ruta de propiedades es:
        // ShoppingItem -> ShoppingList -> User -> Id.
        return shoppingItemRepository.findByShoppingList_User_Id(userId);
    }

    // GENERA ARTÍCULOS A PARTIR DE LAS COMIDAS
    // REGISTRADAS ENTRE DOS FECHAS
    @Override
    public List<ShoppingItem> generarListCompras(
            Long userId,
            LocalDate inicio,
            LocalDate fin) {

        // Obtiene las comidas del usuario cuya fecha
        // se encuentra dentro del intervalo indicado.
        List<Meals> meals = mealsRepository.findByUserIdAndDateBetween(
                userId,
                inicio,
                fin
        );

        // Crea una lista temporal para reunir
        // los artículos derivados de los ingredientes.
        List<ShoppingItem> shoppingItems = new ArrayList<>();

        // Recorre todas las comidas encontradas.
        for (Meals meal : meals) {

            // Obtiene los ingredientes asociados a la comida actual.
            List<MealIngredient> ingredientes =
                    mealIngredientRepository.findByMealsId(meal.getId());

            // Recorre los ingredientes de esa comida.
            for (MealIngredient ingrediente : ingredientes) {

                // Crea un nuevo artículo de compra.
                ShoppingItem shoppingItem = new ShoppingItem();

                // Copia la descripción del ingrediente
                // para utilizarla como descripción del artículo.
                shoppingItem.setDescription(ingrediente.getDescription());

                // Marca el artículo como pendiente de compra.
                shoppingItem.setCompleted(false);

                // Añade el artículo a la lista temporal.
                shoppingItems.add(shoppingItem);
            }
        }

        // Devuelve todos los artículos generados en memoria.
        return shoppingItems;
    }
}