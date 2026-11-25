package upeu.edu.pe.HABITIA_Back.modules.meal.domain.service;
//lee "200 g de arroz" y separa cantidad, unidad y nombre; si no entiende pues 400

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import upeu.edu.pe.HABITIA_Back.shared.domain.enums.IngredientUnit;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InterpreteDeIngredientes {
    private static final Pattern INGREDIENT_PATTERN = Pattern.compile(
            "^\\s*(\\d+(?:[.,]\\d+)?)?\\s*" +
                    "(?:(kilogramos?|kilos?|kg|gramos?|grs?|gr|g|" +
                    "mililitros?|ml|litros?|l|unidades?|unidad|u)\\b\\s*)?" +
                    "(?:de\\s+)?" +
                    "(.+?)\\s*$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Set<String> COUNTABLE_INGREDIENTS = Set.of(
            "huevo", "manzana", "platano", "tomate", "papa",
            "cebolla", "limon", "naranja", "zanahoria",
            "pepino", "palta", "pera", "mandarina"
    );

    public record ParsedIngredient(
            String description,
            BigDecimal quantity,
            IngredientUnit unit
    ) {}

    public ParsedIngredient parse(String input) {
        if (input == null || input.isBlank()) {
            throw invalid("Debes ingresar un ingrediente con cantidad y unidad.");
        }

        Matcher matcher = INGREDIENT_PATTERN.matcher(input);

        if (!matcher.matches()) {
            throw invalid(
                    "Formato inválido. Ejemplos: 3 huevos, 200 g de arroz, 0.5 kg de avena."
            );
        }

        BigDecimal quantity;

        String qtyGroup = matcher.group(1);
        if (qtyGroup == null || qtyGroup.isBlank()) {
            quantity = BigDecimal.ONE;
        } else {
            try {
                quantity = new BigDecimal(
                        qtyGroup.replace(',', '.')
                );
            } catch (NumberFormatException ex) {
                throw invalid("La cantidad ingresada no es válida.");
            }
        }

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw invalid("La cantidad debe ser mayor que cero.");
        }

        if (quantity.stripTrailingZeros().scale() > 3) {
            throw invalid("La cantidad admite como máximo tres decimales.");
        }

        String rawUnit = matcher.group(2);
        String rawDescription = matcher.group(3);

        if (rawDescription == null || rawDescription.isBlank()) {
            throw invalid("Debes indicar el nombre del ingrediente.");
        }

        IngredientUnit unit;

        if (rawUnit == null) {
            if (qtyGroup == null || qtyGroup.isBlank()) {
                // Sin cantidad explícita → 1 unidad (no exigir countable).
                unit = IngredientUnit.UNIT;
            } else {
                String candidate = normalizeDescription(rawDescription);

                if (!COUNTABLE_INGREDIENTS.contains(keyName(candidate))) {
                    throw invalid(
                            "Debes indicar una unidad reconocible. " +
                                    "Ejemplos: 200 g de arroz o 2 unidades de arroz."
                    );
                }

                unit = IngredientUnit.UNIT;
            }
        } else {
            unit = normalizeUnit(rawUnit);
        }

        if (unit == IngredientUnit.UNIT &&
                quantity.stripTrailingZeros().scale() > 0) {
            throw invalid("Las unidades individuales deben ser enteras.");
        }

        String description = normalizeDescription(rawDescription);

        if (description.isBlank()) {
            throw invalid("El nombre del ingrediente no es válido.");
        }

        return new ParsedIngredient(description, quantity, unit);
    }

    private IngredientUnit normalizeUnit(String rawUnit) {
        String unit = removeAccents(rawUnit)
                .toLowerCase(Locale.ROOT);

        return switch (unit) {
            case "g", "gr", "grs", "gramo", "gramos" ->
                    IngredientUnit.G;

            case "kg", "kilo", "kilos", "kilogramo", "kilogramos" ->
                    IngredientUnit.KG;

            case "ml", "mililitro", "mililitros" ->
                    IngredientUnit.ML;

            case "l", "litro", "litros" ->
                    IngredientUnit.L;

            case "u", "unidad", "unidades" ->
                    IngredientUnit.UNIT;

            default -> throw invalid("La unidad ingresada no es válida.");
        };
    }

    private String normalizeDescription(String value) {
        String result = value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        if (result.startsWith("de ")) {
            result = result.substring(3).trim();
        }

        // Normaliza algunos plurales frecuentes sin modificar
        // indiscriminadamente todas las palabras.
        String[] words = result.split(" ");
        StringBuilder normalized = new StringBuilder();

        for (String word : words) {
            String singular = singularize(word);

            if (!normalized.isEmpty()) {
                normalized.append(' ');
            }

            normalized.append(singular);
        }

        return normalized.toString();
    }

    private String singularize(String word) {
        if (word.length() <= 3) {
            return word;
        }

        if (word.endsWith("ces") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "z";
        }

        if (word.endsWith("frijoles")) {
            return word.substring(0, word.length() - 2);
        }

        if (word.endsWith("es") &&
                (word.endsWith("iones") ||
                        word.endsWith("oles") ||
                        word.endsWith("ales"))) {
            return word.substring(0, word.length() - 2);
        }

        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }

    public String keyName(String value) {
        return removeAccents(value)
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String removeAccents(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private ResponseStatusException invalid(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
