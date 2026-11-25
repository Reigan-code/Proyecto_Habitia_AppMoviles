package upeu.edu.pe.HABITIA_Back.modules.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.HABITIA_Back.modules.catalog.domain.model.CatalogIngredientEntity;

import java.util.List;

public interface CatalogIngredientRepository extends JpaRepository<CatalogIngredientEntity, Long> {
    List<CatalogIngredientEntity> findByUserIdOrderByCategoryAscNameAsc(Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
}
