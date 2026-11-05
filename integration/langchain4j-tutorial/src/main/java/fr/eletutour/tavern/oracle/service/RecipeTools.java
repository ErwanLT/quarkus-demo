package fr.eletutour.tavern.oracle.service;

import dev.langchain4j.agent.tool.Tool;
import fr.eletutour.tavern.oracle.model.Recipe;
import fr.eletutour.tavern.oracle.repository.RecipeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.stream.Collectors;

@ApplicationScoped
public class RecipeTools {
    private final RecipeRepository recipes;

    public RecipeTools(RecipeRepository recipes) {
        this.recipes = recipes;
    }

    @Tool("Cherche dans le grimoire une recette correspondant aux mots-clés fournis")
    @Transactional
    public String findRecipe(String keywords) {
        String results = recipes.search(keywords).stream()
                .map(Recipe::toGrimoireLine)
                .collect(Collectors.joining("\n"));
        return results.isEmpty() ? "Aucune recette trouvée pour : " + keywords : results;
    }
}
