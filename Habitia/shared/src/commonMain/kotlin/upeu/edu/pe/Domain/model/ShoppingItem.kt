package upeu.edu.pe.Domain.model

data class ShoppingItem(
    val id: String,
    val name: String,
    val quantity: String,
    val category: String,
    val foodCategory: FoodCategory = FoodCategory.OTROS,
    val completed: Boolean = false,
    val date: String? = null,   // ISO yyyy-MM-dd para subdividir por día
    val sourceDay: String? = null,
    val sourceMealType: String? = null,
    val sourceMealName: String? = null
)