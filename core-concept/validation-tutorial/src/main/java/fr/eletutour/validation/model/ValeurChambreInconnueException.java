package fr.eletutour.validation.model;

/**
 * Lever pendant la désérialisation JSON permet à l'enum de refuser une valeur
 * inconnue. Le mapper dédié retrouve cette cause dans l'exception JAX-RS et la
 * transforme en réponse problem+json, avec le champ fautif.
 */
public class ValeurChambreInconnueException extends IllegalArgumentException {

    private final String field;

    public ValeurChambreInconnueException(String field, String valeur) {
        super("Type de chambre inconnu : " + valeur + " (DORTOIR, SUITE ou PLACARD attendu)");
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
