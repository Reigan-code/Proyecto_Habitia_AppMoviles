package upeu.edu.pe.HABITIA_Back.modules.shopping.infrastructure.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.ShoppingListStatus;
import upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model.ShoppingList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Hereda las operaciones CRUD de JpaRepository para ShoppingList.
// No es necesario implementar manualmente los métodos básicos:
// Spring Data JPA genera sus implementaciones automáticamente.
public interface ShoppingListRepository
        extends JpaRepository<ShoppingList, Long> {

    // LISTA LAS LISTAS DE COMPRAS DE UN USUARIO
    // Recupera las listas del usuario indicado y las ordena
    // por fecha de inicio, desde la más reciente hasta la más antigua.
    //
    // @EntityGraph(attributePaths = "items") solicita que también
    // se cargue la relación "items" de cada lista en esta consulta.
    @EntityGraph(attributePaths = "items")
    List<ShoppingList> findByUser_IdOrderByStartDateDesc(Long userId);

    // BUSCA UNA LISTA POR SU ID Y SU PROPIETARIO
    // Solo devuelve la lista si coinciden ambos identificadores:
    // el ID de la lista y el ID del usuario propietario.
    //
    // Optional permite representar que la lista puede no existir
    // o no pertenecer al usuario indicado.
    //
    // EntityGraph también solicita cargar sus artículos.
    @EntityGraph(attributePaths = "items")
    Optional<ShoppingList> findByIdAndUser_Id(
            Long id, Long userId
    );

    // BUSCA LA LISTA MÁS RECIENTE QUE COINCIDA CON LOS CRITERIOS
    // Busca por usuario, fecha inicial, fecha final y estado.
    // Si hay varias coincidencias, devuelve la de mayor ID.
    //
    // Puede utilizarse, por ejemplo, para localizar una lista
    // pendiente de un periodo determinado antes de regenerarla.
    //
    // EntityGraph permite cargar también los artículos asociados.
    @EntityGraph(attributePaths = "items")
    Optional<ShoppingList>
    findFirstByUser_IdAndStartDateAndEndDateAndStatusOrderByIdDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            ShoppingListStatus status
    );
}