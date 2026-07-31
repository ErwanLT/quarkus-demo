package fr.eletutour.taverne.commis.affichage;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

/**
 * Appose une couleur sur un texte avant affichage, comme le tavernier appose un
 * sceau de cire de couleur différente selon la nature d'un message (alerte,
 * confirmation, avertissement).
 *
 * <p>La coloration passe systématiquement par le {@link Terminal} plutôt que par
 * des codes ANSI écrits en dur : c'est ce qui permet de dégrader proprement sur
 * un terminal qui ne supporte pas les couleurs, plutôt que d'afficher des
 * séquences d'échappement brutes à l'écran.</p>
 */
public final class SceauDuTavernier {

    private SceauDuTavernier() {
    }

    public static String alerte(Terminal terminal, String texte) {
        return colorer(terminal, texte, AttributedStyle.RED);
    }

    public static String succes(Terminal terminal, String texte) {
        return colorer(terminal, texte, AttributedStyle.GREEN);
    }

    public static String avertissement(Terminal terminal, String texte) {
        return colorer(terminal, texte, AttributedStyle.YELLOW);
    }

    private static String colorer(Terminal terminal, String texte, int couleur) {
        AttributedStyle style = AttributedStyle.DEFAULT.foreground(couleur).bold();
        return new AttributedString(texte, style).toAnsi(terminal);
    }
}
