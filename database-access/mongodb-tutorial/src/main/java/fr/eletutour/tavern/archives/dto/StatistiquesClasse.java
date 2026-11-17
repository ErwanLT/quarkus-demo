package fr.eletutour.tavern.archives.dto;

/**
 * Ligne de la répartition par classe, produite par le pipeline d'agrégation.
 */
public record StatistiquesClasse(
        String classe,
        int effectif,
        int bourse) {
}
