package fr.eletutour.tavern.flammes.cuisine;

/**
 * Un plat sorti de cuisine.
 *
 * @param plat      ce qui est arrive dans l'assiette
 * @param statut    {@code SERVI} si la cuisine a tenu le delai, {@code SECOURS} sinon
 * @param message   ce que le tavernier annonce a l'aventurier
 */
public record Repas(String plat, String statut, String message) {

    public static final String STATUT_SERVI = "SERVI";
    public static final String STATUT_SECOURS = "SECOURS";

    public static Repas servi(String plat) {
        return new Repas(plat, STATUT_SERVI, "Voila votre " + plat + ", bon appetit !");
    }

    public static Repas secours(String platDemande) {
        return new Repas("Pain et Eau", STATUT_SECOURS,
            "La cuisine n'a pas pu sortir le " + platDemande + " a temps, voila de quoi tenir.");
    }
}
