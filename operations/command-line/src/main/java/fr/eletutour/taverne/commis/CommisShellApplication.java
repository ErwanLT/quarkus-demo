package fr.eletutour.taverne.commis;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fait vivre le commis dans une boucle interactive : contrairement au commis
 * "one-shot" (reveille pour une seule course puis renvoye au lit), ce commis
 * reste dans la cour de la taverne, une lanterne a la main, et attend qu'on
 * lui donne des ordres un a un, jusqu'a ce qu'on le liberre.
 */
public class CommisShellApplication implements QuarkusApplication {

    private static final List<String> MOTS_DE_CONGE = List.of("quitter", "exit", "bonne-nuit");
    private static final Pattern TOKEN = Pattern.compile("\"([^\"]*)\"|(\\S+)");

    /**
     * Fabrique fournie par l'extension quarkus-picocli : elle resout les commandes
     * via CDI, ce qui permet aux missions (ex : {@code ExporterDettesCommand})
     * de continuer a utiliser {@code @Inject} normalement.
     */
    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) {
        CommandLine commandLine = new CommandLine(new CommisDeCourseCommand(), factory);

        afficherAccueil();

        try (Scanner clavier = new Scanner(System.in)) {
            while (true) {
                System.out.print("commis> ");

                if (!clavier.hasNextLine()) {
                    break;
                }

                String ligne = clavier.nextLine().trim();

                if (ligne.isEmpty()) {
                    continue;
                }
                if (MOTS_DE_CONGE.contains(ligne.toLowerCase())) {
                    break;
                }
                if (ligne.equalsIgnoreCase("help")) {
                    commandLine.usage(System.out);
                    continue;
                }

                commandLine.execute(decouper(ligne));
            }
        }

        System.out.println("Le commis repart se coucher. A bientot pour une prochaine course.");
        return 0;
    }

    private void afficherAccueil() {
        System.out.println("=================================================");
        System.out.println(" Le commis de course est reveille et pret.");
        System.out.println(" Tapez 'help' pour voir les missions disponibles,");
        System.out.println(" ou 'quitter' pour le renvoyer se coucher.");
        System.out.println("=================================================");
    }

    /**
     * Decoupe une ligne saisie en tokens, en respectant les guillemets doubles
     * pour permettre des arguments contenant des espaces
     * (ex : {@code purger-grimoire --nom "Almanach des Tavernes"}).
     *
     * @param ligne la ligne brute saisie par l'utilisateur
     * @return les tokens dans l'ordre, guillemets retires
     */
    private String[] decouper(String ligne) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN.matcher(ligne);
        while (matcher.find()) {
            tokens.add(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
        }
        return tokens.toArray(new String[0]);
    }
}
