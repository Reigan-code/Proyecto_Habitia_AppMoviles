package upeu.edu.pe.Presentation.food

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import upeu.edu.pe.Core.components.*
import upeu.edu.pe.Domain.model.Meal
import upeu.edu.pe.Domain.model.MealType
import upeu.edu.pe.Domain.model.ShoppingItem

import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.HabitiaGlassFab
import upeu.edu.pe.Core.theme.habitiaAmbient
import upeu.edu.pe.Core.theme.habitiaGlass
import upeu.edu.pe.Domain.model.CatalogIngredient
import upeu.edu.pe.Domain.model.FoodCategory

private fun mealTypeLabel(type: MealType): String = when (type) {
    MealType.BREAKFAST -> "Desayuno"
    MealType.LUNCH -> "Almuerzo"
    MealType.DINNER -> "Cena"
    MealType.SNACK -> "Aperitivo / Snack"
}

@Composable
fun FoodScreen(viewModel: FoodViewModel) {
    val mealsList by viewModel.meals.collectAsState()
    val shoppingList by viewModel.shoppingItems.collectAsState()
    val catalogByCategory by viewModel.catalogByCategory.collectAsState()

    var currentSubTab by remember { mutableStateOf("Menú") }
    var showAddShoppingDialog by remember { mutableStateOf(false) }
    var shoppingView by remember { mutableStateOf("Por dia") }
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("") }
    var itemCat by remember { mutableStateOf("Despensa") }

    // ---- Editor de comidas (Meal con id vacío = nueva comida) ----
    var editingMeal by remember { mutableStateOf<Meal?>(null) }
    var editName by remember { mutableStateOf("") }
    var editType by remember { mutableStateOf(MealType.BREAKFAST) }
    var editDate by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }
    var editIngredients by remember { mutableStateOf("") }
    var editImage by remember { mutableStateOf("") }
    // -- Glass --
    val hazeState = remember { HazeState() }

    fun openMealEditor(meal: Meal?) {
        // CREAR (meal == null): se pasa un Meal VACÍO con id="" — el editor
        // decide el modo por id.isNotEmpty(). Antes se asignaba null y el
        // diálogo (condición editingMeal?.let) NUNCA aparecía: ese era el
        // bug del FAB "+" en el menú semanal.
        editingMeal = meal ?: Meal(
            id = "", // el id REAL lo asigna el backend en el POST
            name = "",
            type = MealType.BREAKFAST,
            date = kotlinx.datetime.Clock.System
                .todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
                .toString(),
            description = "",
            ingredients = emptyList(),
            image = null
        )
        editName = editingMeal?.name.orEmpty()
        editType = editingMeal?.type ?: MealType.BREAKFAST
        editDate = editingMeal?.date.orEmpty()
        editDesc = editingMeal?.description.orEmpty()
        editIngredients = editingMeal?.ingredients?.joinToString(", ").orEmpty()
        editImage = editingMeal?.image.orEmpty()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ═══ CAPA FUENTE (hermana) — Haze 1.2.x excluye effects descendientes
        // del source (zIndex); como hermana el blur funciona de verdad.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        // ═══ CAPA CONTENIDO (incluidas las cards glass)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 24.dp, bottom = 176.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HabitiaSectionTitle(
                    title = "Cocina & Alimentación",
                    subtitle = "Planifica tus menús y lista de ingredientes"
                )
            }

            // Pestañas Menú / Compras con HabitiaChip
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HabitiaChip(
                        selected = currentSubTab == "Menú",
                        onClick = { currentSubTab = "Menú" },
                        label = "Menú de la Semana",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                // Tamano -> cambia 16.dp aqui
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    HabitiaChip(
                        selected = currentSubTab == "Compras",
                        onClick = { currentSubTab = "Compras" },
                        label = "Lista de Compra",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                // Tamano -> cambia 16.dp aqui
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            if (currentSubTab == "Menú") {
                if (mealsList.isEmpty()) {
                    item {
                        HabitiaEmptyState(
                            icon = Icons.Default.SoupKitchen,
                            title = "No hay comidas programadas",
                            subtitle = "Toca el botón + para diseñar tu recetario o menú semanal"
                        )
                    }
                } else {
                    val grouped = mealsList.groupBy { it.type }
                    val order = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER, MealType.SNACK)
                    order.forEach { type ->
                        val list = grouped[type] ?: return@forEach
                        if (list.isEmpty()) return@forEach
                        item {
                            Text(
                                text = when (type) {
                                    MealType.BREAKFAST -> "Desayunos"
                                    MealType.LUNCH -> "Almuerzos"
                                    MealType.DINNER -> "Cenas"
                                    MealType.SNACK -> "Snacks"
                                },
                                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(list) { meal ->
                            MealCard(meal = meal, onEdit = { openMealEditor(meal) }, hazeState = hazeState)
                        }
                    }
                }
            } else {
                // Selector de vista de la lista de compras
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = shoppingView == "Por dia", onClick = { shoppingView = "Por dia" }, label = { Text("Por dia") })
                        FilterChip(selected = shoppingView == "Por alimento", onClick = { shoppingView = "Por alimento" }, label = { Text("Por alimento") })
                    }
                }
                if (shoppingList.isEmpty()) {
                    item {
                        HabitiaEmptyState(
                            icon = Icons.Default.RemoveShoppingCart,
                            title = "Lista de compras despejada",
                            subtitle = "Toca el boton + para agregar ingredientes"
                        )
                    }
                } else if (shoppingView == "Por dia") {
                    val dias = shoppingList.groupBy { it.sourceDay ?: it.date ?: "Sin dia" }
                    dias.toSortedMap().forEach { (dia, itemsDelDia) ->
                        item {
                            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                            // Peso de letra -> FontWeight.Bold (cambialo aqui)
                            Text("Fecha: $dia", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                        }
                        val porComida = itemsDelDia.groupBy { it.sourceMealType ?: "OTROS" }
                        listOf("BREAKFAST", "LUNCH", "DINNER", "SNACK", "OTROS").forEach { tipo ->
                            val listaComida = porComida[tipo] ?: return@forEach
                            if (listaComida.isEmpty()) return@forEach
                            item {
                                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                                Text(when (tipo) { "BREAKFAST" -> "Desayuno"; "LUNCH" -> "Almuerzo"; "DINNER" -> "Cena"; "SNACK" -> "Snack"; else -> "Otros" }, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 2.dp))
                            }
                            items(listaComida.sortedBy { it.name }) { item ->
                                ShoppingItemRow(item = item, onToggle = { viewModel.toggleShoppingItem(item.id) })
                            }
                        }
                    }
                } else {
                    // Por alimento: agrupado por FoodCategory (Frutas, Verduras, ...)
                    val porCat = shoppingList.groupBy { it.foodCategory }
                    FoodCategory.ordered.forEach { cat ->
                        val listaCat = porCat[cat] ?: return@forEach
                        if (listaCat.isEmpty()) return@forEach
                        item {
                            // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                            // Peso de letra -> FontWeight.Bold (cambialo aqui)
                            Text("${cat.emoji} ${cat.label} (${listaCat.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                        }
                        items(listaCat.sortedBy { it.name }) { item ->
                            ShoppingItemRow(item = item, onToggle = { viewModel.toggleShoppingItem(item.id) })
                        }
                    }
                }
            }
        }

        // FAB: añade comidas (pestaña Menú) o ingredientes (pestaña Compras)
        // FIX VISIBILIDAD: bottom 24→104dp — el nav bar glass lo tapaba (80+12).
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 104.dp)
        ) {
            HabitiaGlassFab(
                onClick = {
                    if (currentSubTab == "Menú") openMealEditor(null) else showAddShoppingDialog =
                        true
                },
                icon = Icons.Default.Add,
                contentDescription = if (currentSubTab == "Menú") "Añadir Comida" else "Añadir Ingrediente"
            )
        }

        if (showAddShoppingDialog) {
            AlertDialog(
                onDismissRequest = { showAddShoppingDialog = false },
                // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(20.dp),
                // Diálogo translúcido (entorno glass) manteniendo legibilidad
                // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (surface)
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                title = {
                    Text(
                        text = "Añadir a la Compra",
                        style = MaterialTheme.typography.titleLarge,
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        fontWeight = FontWeight.Bold,
                        // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HabitiaTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = "Ingrediente o producto",
                            leadingIcon = Icons.Default.ShoppingBasket
                        )
                        HabitiaTextField(
                            value = itemQty,
                            onValueChange = { itemQty = it },
                            label = "Cantidad (ej. 500g, 2 unid)",
                            leadingIcon = Icons.Default.Scale
                        )
                        HabitiaTextField(
                            value = itemCat,
                            onValueChange = { itemCat = it },
                            label = "Categoría (ej. Despensa, Frutas)",
                            leadingIcon = Icons.Default.Category
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (itemName.isNotBlank()) {
                                viewModel.addShoppingItem(
                                    itemName,
                                    itemQty.ifEmpty { "1 unid" },
                                    itemCat.ifEmpty { "Despensa" }
                                )
                                itemName = ""
                                itemQty = ""
                                itemCat = "Despensa"
                                showAddShoppingDialog = false
                            }
                        }
                    ) {
                        Text(
                            "Añadir",
                            // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                            color = MaterialTheme.colorScheme.primary,
                            // Peso de letra -> FontWeight.Bold (cambialo aqui)
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddShoppingDialog = false }) {
                        // Color del texto -> Core/theme/Color.kt (outline) o cambia color= aqui
                        Text("Cancelar", color = MaterialTheme.colorScheme.outline)
                    }
                }
            )
        }

        // ---- EDITOR DE COMIDAS: se abre al hacer click en cualquier alimento ----
        editingMeal?.let { mealToEdit ->
            MealEditorDialog(
                isEditing = mealToEdit.id.isNotEmpty(),
                name = editName, onNameChange = { editName = it },
                type = editType, onTypeChange = { editType = it },
                date = editDate, onDateChange = { editDate = it },
                description = editDesc, onDescriptionChange = { editDesc = it },
                ingredients = editIngredients, onIngredientsChange = { editIngredients = it },
                image = editImage, onImageChange = { editImage = it },
                catalogByCategory = catalogByCategory,
                onAddCatalog = { name, cat -> viewModel.addCatalogIngredient(name, cat) },
                onDismiss = { editingMeal = null },
                onSave = {
                    if (editName.isNotBlank()) {
                        val updated = mealToEdit.copy(
                            name = editName.trim(),
                            type = editType,
                            date = editDate.trim(),
                            description = editDesc.trim(),
                            ingredients = editIngredients
                                .split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() },
                            image = editImage.trim().ifBlank { null }
                        )
                        if (mealToEdit.id.isEmpty()) viewModel.addMeal(updated)
                        else viewModel.updateMeal(updated)
                        editingMeal = null
                    }
                },
                onDelete = if (mealToEdit.id.isNotEmpty()) {
                    {
                        viewModel.deleteMeal(mealToEdit.id)
                        editingMeal = null
                    }
                } else null
            )
        }
        } // fin capa contenido
    } // fin raíz 2 capas
}


/** Diálogo con TODOS los campos editables de un alimento. */
@Composable
fun MealEditorDialog(
    isEditing: Boolean,
    name: String, onNameChange: (String) -> Unit,
    type: MealType, onTypeChange: (MealType) -> Unit,
    date: String, onDateChange: (String) -> Unit,
    description: String, onDescriptionChange: (String) -> Unit,
    ingredients: String, onIngredientsChange: (String) -> Unit,
    image: String, onImageChange: (String) -> Unit,
    catalogByCategory: Map<FoodCategory, List<CatalogIngredient>> = emptyMap(),
    onAddCatalog: (String, FoodCategory) -> Unit = { _, _ -> },
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onDelete: (() -> Unit)?
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        // Forma -> cambia radio 20.dp (mas alto=mas redondo)
        shape = RoundedCornerShape(20.dp),
        // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
        // Color del contenedor -> Core/theme/Color.kt (surface)
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        title = {
            Text(
                text = if (isEditing) "Editar Comida" else "Añadir Comida",
                style = MaterialTheme.typography.titleLarge,
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                fontWeight = FontWeight.Bold,
                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                HabitiaTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = "Nombre del alimento",
                    leadingIcon = Icons.Default.RestaurantMenu
                )

                Text(
                    text = "Tipo de comida:",
                    style = MaterialTheme.typography.labelLarge,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MealType.entries.forEach { mealType ->
                        FilterChip(
                            selected = mealType == type,
                            onClick = { onTypeChange(mealType) },
                            label = { Text(mealTypeLabel(mealType)) }
                        )
                    }
                }

                HabitiaTextField(
                    value = date,
                    onValueChange = onDateChange,
                    label = "Fecha (ej. 2026-09-30)",
                    leadingIcon = Icons.Default.CalendarMonth
                )
                HabitiaTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = "Descripción",
                    leadingIcon = Icons.Default.Notes
                )
                HabitiaTextField(
                    value = ingredients,
                    onValueChange = onIngredientsChange,
                    label = "Ingredientes (separados por comas)",
                    leadingIcon = Icons.Default.List
                )
                // Catálogo de ingredientes seleccionables
                // Catalogo de ingredientes CATEGORIZADO (secciones colapsables por FoodCategory)
                val selected = ingredients.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                var expandedCat by remember { mutableStateOf<FoodCategory?>(FoodCategory.FRUTAS) }
                var showAddIngredient by remember { mutableStateOf(false) }
                var newIngName by remember { mutableStateOf("") }
                var newIngCat by remember { mutableStateOf(FoodCategory.FRUTAS) }
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                Text("Catalogo de ingredientes:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FoodCategory.ordered.forEach { cat ->
                    val items = catalogByCategory[cat].orEmpty()
                    if (items.isEmpty()) return@forEach
                    Surface(
                        // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                        shape = RoundedCornerShape(12.dp),
                        // Color del texto -> Core/theme/Color.kt (surfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { expandedCat = if (expandedCat == cat) null else cat },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                                Text("${cat.emoji} ${cat.label} (${items.size})", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                Icon(if (expandedCat == cat) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                            }
                            if (expandedCat == cat) {
                                Spacer(Modifier.height(6.dp))
                                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items.forEach { ing ->
                                        FilterChip(
                                            selected = ing.name in selected,
                                            onClick = {
                                                val newSet = if (ing.name in selected) selected - ing.name else selected + ing.name
                                                onIngredientsChange(newSet.joinToString(", "))
                                            },
                                            label = { Text(ing.name, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                    FilterChip(
                                        selected = false,
                                        onClick = { newIngCat = cat; newIngName = ""; showAddIngredient = true },
                                        label = { Text("+ nuevo", style = MaterialTheme.typography.labelSmall) }
                                    )
                                }
                            }
                        }
                    }
                }
                if (showAddIngredient) {
                    AlertDialog(
                        onDismissRequest = { showAddIngredient = false },
                        title = { Text("Nuevo ingrediente en ${newIngCat.label}") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                HabitiaTextField(value = newIngName, onValueChange = { newIngName = it }, label = "Nombre")
                                Text("Categoria:", style = MaterialTheme.typography.labelSmall)
                                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                    FoodCategory.ordered.forEach { c ->
                                        FilterChip(selected = c == newIngCat, onClick = { newIngCat = c }, label = { Text(c.label, style = MaterialTheme.typography.labelSmall) })
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (newIngName.isNotBlank()) { onAddCatalog(newIngName.trim(), newIngCat); val newSet = selected + newIngName.trim(); onIngredientsChange(newSet.joinToString(", ")) }
                                showAddIngredient = false
                            }) { Text("Agregar") }
                        },
                        dismissButton = { TextButton(onClick = { showAddIngredient = false }) { Text("Cancelar") } }
                    )
                }
                HabitiaTextField(
                    value = image,
                    onValueChange = onImageChange,
                    label = "Imagen (URL opcional)",
                    leadingIcon = Icons.Default.Image
                )

                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(
                            // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        // Tamano -> cambia 18.dp aqui
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar esta comida")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                Text("Guardar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                // Color del texto -> Core/theme/Color.kt (outline) o cambia color= aqui
                Text("Cancelar", color = MaterialTheme.colorScheme.outline)
            }
        }
    )
}
@Composable
fun MealCard (meal: Meal, onEdit: () -> Unit, hazeState: HazeState? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
            .habitiaGlass(
                hazeState = hazeState,
                // Forma -> cambia radio 16.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(16.dp),
                onClick = onEdit
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mealTypeLabel(meal.type),
                    style = MaterialTheme.typography.labelMedium.copy(
                        // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                        color = MaterialTheme.colorScheme.primary,
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    // Tamano -> cambia 18.dp aqui
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = meal.name,
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurface
            )
            if (meal.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val tutorialUrl = meal.youtubeUrl ?: meal.youtubeQuery?.let { "https://www.youtube.com/results?search_query=${it.replace(" ", "+")}" }
            if (tutorialUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                Button(
                    onClick = { uriHandler.openUri(tutorialUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (primary)
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                ) {
                    // Tamano -> cambia 18.dp aqui
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("▶ Tutorial", style = MaterialTheme.typography.labelMedium)
                }
            }
            if (meal.ingredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Ingredientes:",
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (onSurface) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.ingredients.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ShoppingItemRow(item: ShoppingItem, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        // Forma -> cambia radio 12.dp (mas alto=mas redondo)
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.completed) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = item.completed,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                        checkedColor = MaterialTheme.colorScheme.primary,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (item.completed) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (item.completed) MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.6f
                            ) else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${item.quantity} · ${item.category}",
                        style = MaterialTheme.typography.bodyMedium,
                        // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ShoppingCartCheckout,
                contentDescription = null,
                // Color del icono -> Core/theme/Color.kt (onSurfaceVariant) o cambia tint= aqui
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                // Tamano -> cambia 20.dp aqui
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

