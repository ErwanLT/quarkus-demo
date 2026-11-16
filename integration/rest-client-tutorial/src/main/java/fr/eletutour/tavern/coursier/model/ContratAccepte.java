package fr.eletutour.tavern.coursier.model;

/**
 * Accusé de réception renvoyé par la Guilde quand un coursier signe un contrat.
 */
public record ContratAccepte(
        long id,
        String titre,
        String coursier,
        int recompense) {
}
