package fr.eletutour.validation.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * Les chambres sont un ensemble fermé : le type du champ suffit à refuser une
 * valeur inconnue, avant même la validation Bean Validation.
 */
public enum TypeChambre {
    DORTOIR,
    SUITE,
    PLACARD;

    @JsonValue
    public String valeur() {
        return name();
    }

    @JsonCreator
    public static TypeChambre depuis(String valeur) {
        if (valeur == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(chambre -> chambre.name().equalsIgnoreCase(valeur.trim()))
                .findFirst()
                .orElseThrow(() -> new ValeurChambreInconnueException("chambre", valeur));
    }
}
