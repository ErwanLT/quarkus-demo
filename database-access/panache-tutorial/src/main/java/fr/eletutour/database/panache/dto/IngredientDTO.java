package fr.eletutour.database.panache.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Représentation d'un ingrédient magique")
public record IngredientDTO(
    @Schema(description = "Identifiant de l'ingrédient", example = "1")
    Long id,
    @Schema(description = "Nom de l'ingrédient", example = "Queue de Phénix")
    String name,
    @Schema(description = "Unité de mesure", example = "unité")
    String unit,
    @Schema(description = "Coût en pièces d'or", example = "150.0")
    double cost
) {}
