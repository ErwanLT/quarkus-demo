package fr.eletutour.tavern.coursier.model;

import fr.eletutour.tavern.coursier.exception.business.CoursierException;

import java.util.Locale;

public enum Difficulte {
    FACILE,
    MOYEN,
    MORTEL;

    /**
     * Lit la difficulté saisie par l'appelant. Sans filtre, renvoie {@code null} ;
     * une valeur inconnue est une erreur de l'appelant (400), pas une ressource
     * absente (le 404 que JAX-RS renverrait sur un enum en query param).
     */
    public static Difficulte depuis(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        try {
            return valueOf(valeur.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw CoursierException.difficulteInconnue(valeur);
        }
    }
}
