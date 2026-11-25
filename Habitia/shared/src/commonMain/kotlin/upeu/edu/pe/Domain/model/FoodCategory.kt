package upeu.edu.pe.Domain.model

enum class FoodCategory(val label: String, val emoji: String, val order: Int) {
    FRUTAS("Frutas", "🍎", 1),
    VERDURAS("Verduras", "🥬", 2),
    CARNES("Carnes", "🥩", 3),
    PESCADOS("Pescados", "🐟", 4),
    LACTEOS("Lácteos", "🥛", 5),
    GRANOS("Granos", "🌾", 6),
    LEGUMBRES("Legumbres", "🫘", 7),
    ESPECIAS("Especias", "🧂", 8),
    ACEITES("Aceites", "🫒", 9),
    BEBIDAS("Bebidas", "🧃", 10),
    OTROS("Otros", "🛒", 99);

    companion object {
        fun fromLabel(raw: String?): FoodCategory =
            entries.firstOrNull { it.label.equals(raw, ignoreCase = true) } ?: OTROS

        val ordered = entries.sortedBy { it.order }
    }
}
