package upeu.edu.pe.HABITIA_Back.modules.catalog.infrastructure.Controller;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.HABITIA_Back.modules.catalog.infrastructure.persistence.CatalogIngredientRepository;
import upeu.edu.pe.HABITIA_Back.modules.task.infrastructure.persistence.TaskCategoryRepository;
import upeu.edu.pe.HABITIA_Back.shared.infrastructure.persistence.UserRepository;
import upeu.edu.pe.HABITIA_Back.modules.catalog.domain.model.CatalogIngredientEntity;
import upeu.edu.pe.HABITIA_Back.modules.task.domain.model.TaskCategory;
import upeu.edu.pe.HABITIA_Back.shared.domain.model.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Catalogs")
public class CatalogController {

    private final TaskCategoryRepository categoryRepo;
    private final CatalogIngredientRepository ingredientRepo;
    private final UserRepository userRepo;

    public CatalogController(TaskCategoryRepository categoryRepo,
                             CatalogIngredientRepository ingredientRepo,
                             UserRepository userRepo) {
        this.categoryRepo = categoryRepo;
        this.ingredientRepo = ingredientRepo;
        this.userRepo = userRepo;
    }

    private User user(Authentication auth) {
        return userRepo.findByEmail(auth.getName()).orElseThrow();
    }

    private static final List<String> SEED_CATEGORIES = List.of("Hogar", "Compras", "Alimentación");
    private static final Map<String, List<String>> SEED_INGREDIENTS = Map.ofEntries(
        Map.entry("Frutas", List.of("manzana","plátano","fresa","naranja","aguacate","piña","uva")),
        Map.entry("Verduras", List.of("tomate","cebolla","zanahoria","lechuga","espinaca","brócoli","pimiento")),
        Map.entry("Carnes", List.of("pollo","res","cerdo","pavo")),
        Map.entry("Pescados", List.of("atún","salmón","tilapia")),
        Map.entry("Lácteos", List.of("leche","queso","yogur","mantequilla","huevo")),
        Map.entry("Granos", List.of("arroz","avena","pan","pasta","quinoa","harina")),
        Map.entry("Legumbres", List.of("lenteja","garbanzo","frijol")),
        Map.entry("Especias", List.of("sal","pimienta","orégano","comino","canela")),
        Map.entry("Aceites", List.of("aceite de oliva","aceite vegetal")),
        Map.entry("Bebidas", List.of("agua","café","té","jugo de naranja")),
        Map.entry("Otros", List.of("azúcar","miel"))
    );

    private void ensureSeed(User u) {
        Long uid = u.getId();
        if (categoryRepo.findByUserIdOrderById(uid).isEmpty()) {
            for (String name : SEED_CATEGORIES) {
                TaskCategory c = new TaskCategory();
                c.setUser(u); c.setName(name);
                categoryRepo.save(c);
            }
        }
        if (ingredientRepo.findByUserIdOrderByCategoryAscNameAsc(uid).isEmpty()) {
            SEED_INGREDIENTS.forEach((cat, names) -> {
                for (String name : names) {
                    CatalogIngredientEntity e = new CatalogIngredientEntity();
                    e.setUser(u); e.setName(name); e.setCategory(cat); e.setUnit("unidades");
                    ingredientRepo.save(e);
                }
            });
        }
    }

    //categorias de tareas, si no hay pues siembra las 3
    @GetMapping("/categories")
    @Transactional
    public ResponseEntity<List<String>> getCategories(Authentication auth) {
        User u = user(auth);
        ensureSeed(u);
        return ResponseEntity.ok(
            categoryRepo.findByUserIdOrderById(u.getId()).stream().map(TaskCategory::getName).toList()
        );
    }

    //agrega categoria nueva; sino pues la deja igual
    @PostMapping("/categories")
    public ResponseEntity<List<String>> addCategory(@RequestBody Map<String, String> body,
                                                    Authentication auth) {
        User u = user(auth);
        String name = body.get("name");
        if (name != null && !name.isBlank()
                && categoryRepo.findByUserIdOrderById(u.getId()).stream()
                     .noneMatch(c -> c.getName().equalsIgnoreCase(name.trim()))) {
            TaskCategory c = new TaskCategory();
            c.setUser(u); c.setName(name.trim());
            categoryRepo.save(c);
        }
        return ResponseEntity.ok(categoryRepo.findByUserIdOrderById(u.getId()).stream().map(TaskCategory::getName).toList());
    }

    //borra categoria por nombre; sino pues la deja igual
    @DeleteMapping("/categories/by-name/{name}")
    @Transactional
    public ResponseEntity<List<String>> deleteCategory(@PathVariable String name, Authentication auth) {
        User u = user(auth);
        categoryRepo.findByUserIdOrderById(u.getId()).stream()
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .forEach(c -> categoryRepo.deleteByIdAndUserId(c.getId(), u.getId()));
        return ResponseEntity.ok(categoryRepo.findByUserIdOrderById(u.getId()).stream().map(TaskCategory::getName).toList());
    }

    //catalogo de ingredientes, si no hay pues siembra 48
    @GetMapping("/ingredients")
    @Transactional
    public ResponseEntity<List<Map<String, Object>>> getIngredients(Authentication auth) {
        User u = user(auth);
        ensureSeed(u);
        return ResponseEntity.ok(ingredientRepo.findByUserIdOrderByCategoryAscNameAsc(u.getId())
            .stream().map(e -> Map.<String, Object>of(
                "id", e.getId(), "name", e.getName(), "category", e.getCategory(), "unit", e.getUnit())).toList());
    }

    //agrega ingrediente al catalogo; sino pues igual
    @PostMapping("/ingredients")
    public ResponseEntity<List<Map<String, Object>>> addIngredient(@RequestBody Map<String, String> body,
                                                                   Authentication auth) {
        User u = user(auth);
        String name = body.get("name"), cat = body.get("category"), unit = body.get("unit");
        if (name != null && !name.isBlank()) {
            CatalogIngredientEntity e = new CatalogIngredientEntity();
            e.setUser(u); e.setName(name.trim());
            e.setCategory(cat != null ? cat : "Otros");
            e.setUnit(unit != null ? unit : "unidades");
            ingredientRepo.save(e);
        }
        return ResponseEntity.ok(ingredientRepo.findByUserIdOrderByCategoryAscNameAsc(u.getId())
            .stream().map(e -> Map.<String, Object>of(
                "id", e.getId(), "name", e.getName(), "category", e.getCategory(), "unit", e.getUnit())).toList());
    }

    //borra ingrediente del catalogo; sino pues igual
    @DeleteMapping("/ingredients/{id}")
    @Transactional
    public ResponseEntity<List<Map<String, Object>>> deleteIngredient(@PathVariable Long id, Authentication auth) {
        User u = user(auth);
        ingredientRepo.deleteByIdAndUserId(id, u.getId());
        return ResponseEntity.ok(ingredientRepo.findByUserIdOrderByCategoryAscNameAsc(u.getId())
            .stream().map(e -> Map.<String, Object>of(
                "id", e.getId(), "name", e.getName(), "category", e.getCategory(), "unit", e.getUnit())).toList());
    }
}
