package fr.eletutour.tavern.coursier.model;

/**
 * Un contrat affiché au tableau de la Guilde.
 *
 * @param id           numéro du contrat
 * @param titre        ce qu'il faut accomplir
 * @param commanditaire qui paye la récompense
 * @param difficulte   estimation de la Guilde
 * @param recompense   montant en pièces de cuivre
 */
public record Contrat(
        long id,
        String titre,
        String commanditaire,
        Difficulte difficulte,
        int recompense) {
}
