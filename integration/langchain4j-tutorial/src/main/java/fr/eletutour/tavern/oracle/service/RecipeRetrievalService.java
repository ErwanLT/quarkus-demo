package fr.eletutour.tavern.oracle.service;

import fr.eletutour.tavern.oracle.model.Recipe;
import fr.eletutour.tavern.oracle.repository.RecipeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.stream.Collectors;

@ApplicationScoped
public class RecipeRetrievalService {
    private final RecipeRepository recipes;

    public RecipeRetrievalService(RecipeRepository recipes) {
        this.recipes = recipes;
    }

    /** Retrieval lexical minimal pour illustrer l'injection de contexte avant génération. */
    @Transactional
    public String retrieve(String query) {
        String passages = recipes.search(query).stream()
                .map(Recipe::toGrimoireLine)
                .collect(Collectors.joining("\n"));
        return passages.isEmpty() ? "Aucun passage pertinent trouvé dans le grimoire." : passages;
    }
}
