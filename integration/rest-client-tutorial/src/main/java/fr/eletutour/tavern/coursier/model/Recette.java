package fr.eletutour.tavern.coursier.model;

import java.util.List;

/**
 * Une recette du Grimoire, consultée par le coursier avant de partir en mission.
 *
 * @param nom                nom lisible de la recette
 * @param ingredients        ce qu'il faut emporter
 * @param joursDeFermentation durée de préparation
 */
public record Recette(
        String nom,
        List<String> ingredients,
        int joursDeFermentation) {
}
