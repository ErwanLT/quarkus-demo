package fr.eletutour.database.panache.service;

import fr.eletutour.database.panache.dto.IngredientDTO;
import fr.eletutour.database.panache.dto.RecipeDTO;
import fr.eletutour.database.panache.model.Ingredient;
import fr.eletutour.database.panache.model.Recipe;
import fr.eletutour.database.panache.repository.IngredientRepository;
import fr.eletutour.database.panache.repository.RecipeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service orchestrant le Grimoire de la Taverne.
 * Responsable de la logique métier, de la validation et des transactions.
 */
@ApplicationScoped
public class GrimoireService {

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    IngredientRepository ingredientRepository;

    public List<IngredientDTO> consulterLaReserve() {
        return Ingredient.<Ingredient>listAll().stream()
                .map(this::mapToIngredientDTO)
                .collect(Collectors.toList());
    }

    public List<IngredientDTO> feuilleterLaReserve(int page, int size) {
        return Ingredient.<Ingredient>findAll().page(page, size).list().stream()
                .map(this::mapToIngredientDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientDTO acquerirNouvelIngredient(IngredientDTO dto) {
        validerIngredient(dto);
        
        Ingredient ingredient = new Ingredient(dto.name(), dto.unit(), dto.cost());
        ingredientRepository.persist(ingredient);
        
        return mapToIngredientDTO(ingredient);
    }

    public List<RecipeDTO> consulterLeGrimoire() {
        return recipeRepository.listAllWithIngredients().stream()
                .map(this::mapToRecipeDTO)
                .collect(Collectors.toList());
    }

    public List<RecipeDTO> chercherDansLeGrimoire(String titre) {
        return recipeRepository.findByTitle(titre).stream()
                .map(this::mapToRecipeDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecipeDTO inscrireNouvelleRecette(RecipeDTO dto) {
        validerRecette(dto);
        
        Recipe recipe = new Recipe(dto.title(), dto.description());
        recipeRepository.persist(recipe);
        
        return mapToRecipeDTO(recipe);
    }

    // --- Validations métier ---

    private void validerIngredient(IngredientDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException("L'ingrédient doit avoir un nom.");
        }
        if (dto.cost() < 0) {
            throw new IllegalArgumentException("Le coût d'un ingrédient ne peut être négatif, même pour de la magie noire.");
        }
    }

    private void validerRecette(RecipeDTO dto) {
        if (dto.title() == null || dto.title().isBlank()) {
            throw new IllegalArgumentException("La recette doit avoir un titre.");
        }
    }

    // --- Mappers (internes au service) ---

    private IngredientDTO mapToIngredientDTO(Ingredient ingredient) {
        return new IngredientDTO(ingredient.id, ingredient.name, ingredient.unit, ingredient.cost);
    }

    private RecipeDTO mapToRecipeDTO(Recipe recipe) {
        List<IngredientDTO> ingredients = recipe.ingredients.stream()
                .map(this::mapToIngredientDTO)
                .collect(Collectors.toList());
        return new RecipeDTO(recipe.id, recipe.title, recipe.description, ingredients);
    }
}
