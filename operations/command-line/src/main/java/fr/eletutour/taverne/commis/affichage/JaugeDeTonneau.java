package fr.eletutour.taverne.commis.affichage;

import org.jline.terminal.Terminal;

import java.io.PrintWriter;

/**
 * Affiche une jauge de tonneau : une barre de progression textuelle, pour les
 * missions dont on connait l'avancement au fur et a mesure (chargement d'une
 * carriole, remplissage de tonneaux...), contrairement au {@link Sablier} qui
 * convient mieux a une attente dont la duree est inconnue.
 *
 * <p>Le calcul de la representation textuelle est isole dans {@link #construireLigne}
 * une methode statique pure, testable sans avoir besoin d'un vrai terminal.</p>
 */
public class JaugeDeTonneau {

    private static final int LARGEUR = 24;

    private final Terminal terminal;
    private final String message;

    public JaugeDeTonneau(Terminal terminal, String message) {
        this.terminal = terminal;
        this.message = message;
    }

    /**
     * Met a jour l'affichage de la jauge pour le pourcentage donne.
     *
     * @param pourcentage avancement, entre 0 et 100
     */
    public void avancerA(int pourcentage) {
        PrintWriter ecran = terminal.writer();
        ecran.print("\r" + construireLigne(message, pourcentage));
        ecran.flush();
    }

    /**
     * Termine proprement l'affichage de la jauge (passage a la ligne finale).
     */
    public void terminer() {
        terminal.writer().println();
        terminal.writer().flush();
    }

    static String construireLigne(String message, int pourcentage) {
        int borne = Math.max(0, Math.min(100, pourcentage));
        int rempli = (borne * LARGEUR) / 100;
        String barre = "=".repeat(rempli) + " ".repeat(LARGEUR - rempli);
        return String.format("%s [%s] %3d%%", message, barre, borne);
    }
}
