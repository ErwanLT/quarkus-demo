package fr.eletutour.taverne.commis.affichage;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;

/**
 * Demande une confirmation explicite avant une action qui ne se rattrape pas,
 * comme le tavernier qui s'assure une dernière fois avant de jeter un vieux
 * grimoire au feu.
 */
public class DemandeConfirmation {

    private final LineReader lecteur;

    public DemandeConfirmation(Terminal terminal) {
        this.lecteur = LineReaderBuilder.builder().terminal(terminal).build();
    }

    /**
     * Pose la question donnée et attend une réponse par oui ou par non.
     *
     * @param question la question posée, sans le suffixe "(oui/non)"
     * @return {@code true} si la réponse commence par "o" (oui), {@code false} sinon
     */
    public boolean confirmer(String question) {
        String reponse = lecteur.readLine(question + " (oui/non) : ").trim().toLowerCase();
        return reponse.equals("oui") || reponse.equals("o");
    }
}
