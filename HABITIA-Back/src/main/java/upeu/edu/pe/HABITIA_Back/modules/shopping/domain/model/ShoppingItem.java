
package upeu.edu.pe.HABITIA_Back.modules.shopping.domain.model;

import jakarta.persistence.*;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.IngredientUnit;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.ShoppingItemSource;

import java.math.BigDecimal;

@Entity
@Table(name = "Shopping_Items")
public class ShoppingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_shopping_list", nullable = false)
    private ShoppingList shoppingList;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(precision = 10, scale = 3)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private IngredientUnit unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShoppingItemSource source;

    @Column(nullable = false)
    private Boolean completed = false;

    // Día/fecha para la que se comprará el ítem (permite subdividir la
    // lista de compras por día de la semana). Nullable por compatibilidad.
    @Column(name = "item_date")
    private java.time.LocalDate itemDate;

    @Column(name = "source_meal_type")
    private String sourceMealType;

    @Column(name = "source_meal_name")
    private String sourceMealName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public IngredientUnit getUnit() {
        return unit;
    }

    public void setUnit(IngredientUnit unit) {
        this.unit = unit;
    }

    public ShoppingItemSource getSource() {
        return source;
    }

    public void setSource(ShoppingItemSource source) {
        this.source = source;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public java.time.LocalDate getItemDate() {
        return itemDate;
    }

    public void setItemDate(java.time.LocalDate itemDate) {
        this.itemDate = itemDate;
    }

    public String getSourceMealType() {
        return sourceMealType;
    }

    public void setSourceMealType(String sourceMealType) {
        this.sourceMealType = sourceMealType;
    }

    public String getSourceMealName() {
        return sourceMealName;
    }

    public void setSourceMealName(String sourceMealName) {
        this.sourceMealName = sourceMealName;
    }

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", source=" + source +
                ", completed=" + completed +
                '}';
    }
}
