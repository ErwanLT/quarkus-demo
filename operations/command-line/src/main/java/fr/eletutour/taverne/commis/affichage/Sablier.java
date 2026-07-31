package fr.eletutour.taverne.commis.affichage;

import org.jline.terminal.Terminal;

import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Fait tourner un sablier pendant qu'une tache de fond n'est pas terminee.
 *
 * <p>Utile pour les missions qui prennent quelques secondes (un aller au marche,
 * une consultation d'un registre distant...) sans qu'on connaisse a l'avance leur
 * duree exacte, contrairement a la {@link JaugeDeTonneau} qui suit un avancement
 * mesurable.</p>
 */
public class Sablier {

    private static final char[] TRAME = {'|', '/', '-', '\\'};

    private final Terminal terminal;

    public Sablier(Terminal terminal) {
        this.terminal = terminal;
    }

    /**
     * Execute la tache donnee en tache de fond, tout en affichant le sablier
     * devant le message fourni, jusqu'a ce que la tache se termine.
     *
     * @param message message affiche a cote du sablier
     * @param tache   tache a executer en tache de fond
     * @param <T>     type de resultat renvoye par la tache
     * @return le resultat de la tache
     * @throws Exception si la tache echoue
     */
    public <T> T tourner(String message, Callable<T> tache) throws Exception {
        ExecutorService executeur = Executors.newSingleThreadExecutor();
        Future<T> futur = executeur.submit(tache);
        PrintWriter ecran = terminal.writer();

        int indice = 0;
        try {
            while (!futur.isDone()) {
                ecran.print("\r" + message + " " + TRAME[indice++ % TRAME.length]);
                ecran.flush();
                Thread.sleep(120);
            }
        } finally {
            executeur.shutdown();
        }

        // Efface la ligne du sablier avant de laisser la suite s'afficher proprement.
        ecran.print("\r" + " ".repeat(message.length() + 2) + "\r");
        ecran.flush();

        return futur.get();
    }
}
