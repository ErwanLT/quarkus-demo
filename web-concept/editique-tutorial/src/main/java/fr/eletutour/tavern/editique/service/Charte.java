package fr.eletutour.tavern.editique.service;

/**
 * Les chartes graphiques de la taverne : des feuilles de styles JasperReports ({@code .jrtx}).
 * <p>
 * L'extension Quarkus copie les {@code .jrtx} de {@code src/main/jasperreports} sans les compiler,
 * à côté des {@code .jasper}. Chaque rapport déclare {@code <template>$P{CHARTE}</template>} :
 * la charte est donc choisie au remplissage, et un même rapport compilé peut changer d'habit.
 * </p>
 */
public enum Charte {

    /** La charte de The Falling Whale : encre brune, parchemin et sépia, pour les documents A4. */
    TAVERNE("jasperreports/charte-taverne.jrtx"),

    /** Sa déclinaison noir et blanc pour l'imprimante thermique du comptoir. */
    THERMIQUE("jasperreports/charte-thermique.jrtx");

    private final String emplacement;

    Charte(String emplacement) {
        this.emplacement = emplacement;
    }

    /** Emplacement du {@code .jrtx} sur le classpath, tel que l'attend le paramètre {@code CHARTE}. */
    public String emplacement() {
        return emplacement;
    }
}
