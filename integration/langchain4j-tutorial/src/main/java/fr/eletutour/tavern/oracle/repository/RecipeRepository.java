package fr.eletutour.tavern.oracle.repository;

import fr.eletutour.tavern.oracle.model.Recipe;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class RecipeRepository implements PanacheRepository<Recipe> {
    /** \W est ASCII par défaut en Java : « mûres » serait coupé en « m » et « res ». On découpe sur ce qui n'est pas une lettre Unicode. */
    private static final Pattern NON_LETTERS = Pattern.compile("[^\\p{L}]+");
    private static final int MIN_TOKEN_LENGTH = 3;
    private static final Set<String> STOP_WORDS = Set.of(
            "les", "des", "aux", "une", "que", "qui", "quoi", "avec", "sans", "dans", "pour", "par", "sur", "est", "sont");

    /** Une seule requête pour tous les mots-clés, puis tri par nombre de mots-clés retrouvés. */
    public List<Recipe> search(String query) {
        List<String> tokens = tokens(query);
        if (tokens.isEmpty()) {
            return List.of();
        }
        String where = IntStream.rangeClosed(1, tokens.size())
                .mapToObj(i -> "lower(concat(name, ' ', description, ' ', ingredients)) like ?" + i)
                .collect(Collectors.joining(" or "));
        Object[] patterns = tokens.stream().map(token -> "%" + token + "%").toArray();
        return find(where, patterns).<Recipe>list().stream()
                .sorted(Comparator.comparingInt((Recipe recipe) -> matches(recipe, tokens)).reversed())
                .toList();
    }

    /** Mots-clés significatifs d'une question : lettres uniquement, en minuscules, sans mots vides ni doublons. */
    public static List<String> tokens(String query) {
        if (query == null) {
            return List.of();
        }
        return Arrays.stream(NON_LETTERS.split(query.toLowerCase(Locale.ROOT)))
                .filter(token -> token.length() >= MIN_TOKEN_LENGTH)
                .filter(token -> !STOP_WORDS.contains(token))
                .distinct()
                .toList();
    }

    private static int matches(Recipe recipe, List<String> tokens) {
        String text = (recipe.name + " " + recipe.description + " " + recipe.ingredients).toLowerCase(Locale.ROOT);
        return (int) tokens.stream().filter(text::contains).count();
    }
}
