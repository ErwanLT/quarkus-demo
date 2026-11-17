package fr.eletutour.tavern.coursier.model;

/**
 * Accusé de réception renvoyé par la Guilde quand un coursier signe un contrat.
 *
 * @param bordereau numéro de course tamponné par la Guilde : c'est l'en-tête
 *                  {@code X-Request-Id} calculé par le client à chaque appel
 */
public record ContratAccepte(
        long id,
        String titre,
        String coursier,
        int recompense,
        String bordereau) {
}
