package fr.eletutour.database.panache.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.util.List;

@Schema(description = "Représentation d'une recette du grimoire")
public record RecipeDTO(
    @Schema(description = "Identifiant de la recette", example = "1")
    Long id,
    @Schema(description = "Titre de la recette", example = "Hydromel de l'Elfe")
    String title,
    @Schema(description = "Description de la recette", example = "Une boisson rafraîchissante.")
    String description,
    @Schema(description = "Liste des ingrédients nécessaires")
    List<IngredientDTO> ingredients
) {}
